//////////////////////////////////////////////////////////////////////////////////////
//
//  Copyright 2012 Freshplanet (http://freshplanet.com | opensource@freshplanet.com)
//  
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//  
//    http://www.apache.org/licenses/LICENSE-2.0
//  
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//  
//////////////////////////////////////////////////////////////////////////////////////

#import "AirAACPlayer.h"
#import <AVFoundation/AVFoundation.h>
#import "FPANEUtils.h"

static dispatch_queue_t soundLoadingQueue;

AVAudioPlayer *getAudioPlayerFromContext(FREContext context)
{
    CFTypeRef audioPlayerRef;
    FREGetContextNativeData(context, (void *)&audioPlayerRef);
    return (__bridge AVAudioPlayer *)audioPlayerRef;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_load)
{
    NSURL *url = [NSURL URLWithString:FPANE_FREObjectToNSString(argv[0])];
    
    /* Retain audio player and attach it to FREContext.
       We do this before initializing the player because
       we can only call FRESetContextNativeData in the main
       thread, and we'll only be able to initialize the
       player after downloading the data, in a background
       thread.
     */
    __block AVAudioPlayer *audioPlayer = [AVAudioPlayer alloc];
    CFTypeRef audioPlayerRef = CFBridgingRetain(audioPlayer);
    FRESetContextNativeData(context, (void *)audioPlayerRef);
    
    /* Contrary to what FREDispatchStatusEventAsync's
       documentation says, the event is dispatched if
       the given context has already been disposed.
       The problem is that it is dispatched to other
       instances...
       To alleviate this issue, we compare the player's
       current and initial retain count before dispatching
       an event. If the retain count has decreased, it
       means we released it when disposing the context
       so events shouldn't be dispatched.
     */
    CFIndex initialRetainCount = CFGetRetainCount(audioPlayerRef);
    
    dispatch_async(soundLoadingQueue, ^{
        
        NSError *error;
        NSData *soundData = [NSData dataWithContentsOfURL:url options:NSDataReadingUncached error:&error];
        if (!soundData)
        {
            if (CFGetRetainCount(audioPlayerRef) >= initialRetainCount)
            {
                FPANE_DispatchEventWithInfo(context, @"AAC_PLAYER_ERROR", [error description]);
            }
            return;
        }
        
        audioPlayer = [audioPlayer initWithData:soundData error:&error];
        if(!audioPlayer)
        {
            if (CFGetRetainCount(audioPlayerRef) >= initialRetainCount)
            {
                FPANE_DispatchEventWithInfo(context, @"AAC_PLAYER_ERROR", [error description]);
            }
            return;
        }
        
        if (CFGetRetainCount(audioPlayerRef) >= initialRetainCount)
        {
            FPANE_DispatchEventWithInfo(context, @"AAC_PLAYER_PREPARED", @"OK");
        }
    });

    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_play)
{
    double startTime = FPANE_FREObjectToDouble(argv[0]);
    AVAudioPlayer *audioPlayer = getAudioPlayerFromContext(context);
    if (startTime > 0) audioPlayer.currentTime = startTime;
    [audioPlayer play];
    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_pause)
{
    AVAudioPlayer *audioPlayer = getAudioPlayerFromContext(context);
    [audioPlayer pause];
    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_stop)
{
    AVAudioPlayer *audioPlayer = getAudioPlayerFromContext(context);
    [audioPlayer stop];
    audioPlayer.currentTime = 0;
    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_getDuration)
{
    AVAudioPlayer *audioPlayer = getAudioPlayerFromContext(context);
    return FPANE_IntToFREObject(1000*audioPlayer.duration);
}

DEFINE_ANE_FUNCTION(AirAACPlayer_getProgress)
{
    AVAudioPlayer *audioPlayer = getAudioPlayerFromContext(context);
    double progress = audioPlayer.isPlaying ? audioPlayer.currentTime : audioPlayer.duration;
    return FPANE_IntToFREObject(1000*progress);
}

void AirAACPlayerContextInitializer(void* extData, const uint8_t* ctxType, FREContext ctx,
                        uint32_t* numFunctionsToTest, const FRENamedFunction** functionsToSet) 
{
    static FRENamedFunction functions[] = {
        MAP_FUNCTION(AirAACPlayer_load, NULL),
        MAP_FUNCTION(AirAACPlayer_play, NULL),
        MAP_FUNCTION(AirAACPlayer_pause, NULL),
        MAP_FUNCTION(AirAACPlayer_stop, NULL),
        MAP_FUNCTION(AirAACPlayer_getDuration, NULL),
        MAP_FUNCTION(AirAACPlayer_getProgress, NULL)
    };
    *numFunctionsToTest = sizeof(functions) / sizeof(FRENamedFunction);
    *functionsToSet = functions;
}

void AirAACPlayerContextFinalizer(FREContext ctx)
{
    CFTypeRef audioPlayerRef;
    FREGetContextNativeData(ctx, (void **)&audioPlayerRef);
    CFBridgingRelease(audioPlayerRef);
}

void AirAACPlayerInitializer(void** extDataToSet, FREContextInitializer* ctxInitializerToSet, FREContextFinalizer* ctxFinalizerToSet)
{
	*extDataToSet = NULL;
	*ctxInitializerToSet = &AirAACPlayerContextInitializer;
	*ctxFinalizerToSet = &AirAACPlayerContextFinalizer;
    
    soundLoadingQueue = dispatch_queue_create("soundLoadingQueue", NULL);
}

void AirAACPlayerFinalizer(void *extData) {}
