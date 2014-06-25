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

AVAudioPlayer* getPlayer(NSString* url)
{
    if(!soundPlayers)
        soundPlayers = [[NSMutableDictionary alloc] init];
    
    return [soundPlayers objectForKey:url];
}

void setPlayer(NSString* url, AVAudioPlayer* player)
{
    if(!soundPlayers)
        soundPlayers = [[NSMutableDictionary alloc] init];
    
    [soundPlayers setObject:player forKey:url];
}

void removePlayer(NSString* url)
{
    [soundPlayers removeObjectForKey:url];
}

AVAudioPlayer* getPlayerFromContext(FREContext context)
{
    NSString *url;
    FREGetContextNativeData(context, (void**)&url);
    if(url)
        return getPlayer(url);
    
    FREDispatchStatusEventAsync(context, (const uint8_t*)"LOGGING", (const uint8_t*)"Sound player is NULL");
    return NULL;
}

void removePlayerFromContext(FREContext context)
{
    FREDispatchStatusEventAsync(context, (const uint8_t*)"LOGGING", (const uint8_t*)"remove player from context");
    NSString *url;
    FREGetContextNativeData(context, (void**)&url);
    if(url)
    {
        FREDispatchStatusEventAsync(context, (const uint8_t*)"LOGGING", (const uint8_t*)"remove player");
        removePlayer(url);
    }
}

DEFINE_ANE_FUNCTION(loadUrl)
{
    uint32_t string_length;
    const uint8_t *utf8_message;
    NSString* url = NULL;
    if (FREGetObjectAsUTF8(argv[0], &string_length, &utf8_message) == FRE_OK)
        url = [NSString stringWithUTF8String:(char*) utf8_message];

    FRESetContextNativeData(context, url);
    
    dispatch_queue_t thread = dispatch_queue_create("sound loading", NULL);
    dispatch_async(thread,
    ^{
        NSError *error;
        NSURL *myUrl = [NSURL URLWithString:url];
        NSData *myData = [NSData dataWithContentsOfURL:myUrl options:NSDataReadingUncached error:&error];
        if(error || !myData)
        {
            FREDispatchStatusEventAsync(context, (const uint8_t*)"LOGGING", (const uint8_t*)"Data error");
            FREDispatchStatusEventAsync(context, (const uint8_t*)"AAC_PLAYER_ERROR", (const uint8_t*)"Error loading sound data");
            return;
        }
        
        AVAudioPlayer *soundPlayer = [[AVAudioPlayer alloc] initWithData:myData error:&error];
        if(error || !soundPlayer)
        {
            FREDispatchStatusEventAsync(context, (const uint8_t*)"LOGGING", (const uint8_t*)"Player error");
            FREDispatchStatusEventAsync(context, (const uint8_t*)"AAC_PLAYER_ERROR", (const uint8_t*)"Error creating sound player");
            return;
        }
        
        FREDispatchStatusEventAsync(context, (const uint8_t*)"LOGGING", (const uint8_t*)"Set soundPlayer");
        setPlayer(url, soundPlayer);
        
        FREDispatchStatusEventAsync(context, (const uint8_t*)"AAC_PLAYER_PREPARED", (const uint8_t*)"prepared");
    });

    return NULL;
}

DEFINE_ANE_FUNCTION(play)
{
    double startTime = 0.0;
    FREGetObjectAsDouble(argv[0], &startTime);
    AVAudioPlayer* soundPlayer = getPlayerFromContext(context);
    
    if(!soundPlayer)
        FREDispatchStatusEventAsync(context, (const uint8_t*)"AAC_PLAYER_ERROR", (const uint8_t*)"Sound player is null in play()");
    
    if(startTime > 0)
        [soundPlayer setCurrentTime:startTime];
    
    [soundPlayer play];
    return NULL;
}

DEFINE_ANE_FUNCTION(pauseFunction)
{
    AVAudioPlayer* soundPlayer = getPlayerFromContext(context);
    if(!soundPlayer)
        FREDispatchStatusEventAsync(context, (const uint8_t*)"AAC_PLAYER_ERROR", (const uint8_t*)"Sound player is null in pause()");
    
    [soundPlayer pause];
    return NULL;
}

DEFINE_ANE_FUNCTION(stop)
{
    AVAudioPlayer* soundPlayer = getPlayerFromContext(context);
    if(!soundPlayer)
        FREDispatchStatusEventAsync(context, (const uint8_t*)"AAC_PLAYER_ERROR", (const uint8_t*)"Sound player is null in stop()");
    
    [soundPlayer stop];
    [soundPlayer setCurrentTime:0];
    return NULL;
}

DEFINE_ANE_FUNCTION(closeFunction)
{
    AVAudioPlayer* soundPlayer = getPlayerFromContext(context);
    if(!soundPlayer)
        FREDispatchStatusEventAsync(context, (const uint8_t*)"AAC_PLAYER_ERROR", (const uint8_t*)"Sound player is null in close()");
    removePlayerFromContext(context);
    [soundPlayer release];
    return NULL;
}

DEFINE_ANE_FUNCTION(getLength)
{
    AVAudioPlayer* soundPlayer = getPlayerFromContext(context);
    if(!soundPlayer)
        FREDispatchStatusEventAsync(context, (const uint8_t*)"AAC_PLAYER_ERROR", (const uint8_t*)"Sound player is null in getLength()");
    
    double len = 0.0;
    if(soundPlayer)
        len = soundPlayer.duration;
    
    int32_t milliseconds = (int32_t)(len*1000);
    
    FREObject ret;
    FRENewObjectFromInt32(milliseconds, &ret);
    return ret;
}

DEFINE_ANE_FUNCTION(getProgress)
{
    AVAudioPlayer* soundPlayer = getPlayerFromContext(context);
    if(!soundPlayer)
        FREDispatchStatusEventAsync(context, (const uint8_t*)"AAC_PLAYER_ERROR", (const uint8_t*)"Sound player is null in getProgress()");
    
    double progress;
    if(soundPlayer)
        progress = soundPlayer.duration;
    else
        progress = 0.0;
    
    if(soundPlayer && soundPlayer.isPlaying)
        progress = soundPlayer.currentTime;
    
    int32_t milliseconds = (int32_t)(progress*1000);
    FREObject ret;
    FRENewObjectFromInt32(milliseconds, &ret);
    
    return ret;
}


#pragma mark - C interface

void AirAACPlayerContextInitializer(void* extData, const uint8_t* ctxType, FREContext ctx,
                        uint32_t* numFunctionsToTest, const FRENamedFunction** functionsToSet) 
{
    // Register the links btwn AS3 and ObjC. (dont forget to modify the nbFuntionsToLink integer if you are adding/removing functions)
    NSInteger nbFuntionsToLink = 7;
    *numFunctionsToTest = nbFuntionsToLink;
    
    FRENamedFunction* func = (FRENamedFunction*) malloc(sizeof(FRENamedFunction) * nbFuntionsToLink);
    
    func[0].name = (const uint8_t*) "loadUrl";
    func[0].functionData = NULL;
    func[0].function = &loadUrl;
    
    func[1].name = (const uint8_t*) "play";
    func[1].functionData = NULL;
    func[1].function = &play;
    
    func[2].name = (const uint8_t*) "pause";
    func[2].functionData = NULL;
    func[2].function = &pauseFunction;
    
    func[3].name = (const uint8_t*) "stop";
    func[3].functionData = NULL;
    func[3].function = &stop;
    
    func[4].name = (const uint8_t*) "close";
    func[4].functionData = NULL;
    func[4].function = &closeFunction;
    
    func[5].name = (const uint8_t*) "getLength";
    func[5].functionData = NULL;
    func[5].function = &getLength;
    
    func[6].name = (const uint8_t*) "getProgress";
    func[6].functionData = NULL;
    func[6].function = &getProgress;

    *functionsToSet = func;
}

void AirAACPlayerContextFinalizer(FREContext ctx) { }

void AirAACPlayerInitializer(void** extDataToSet, FREContextInitializer* ctxInitializerToSet, FREContextFinalizer* ctxFinalizerToSet)
{
	*extDataToSet = NULL;
	*ctxInitializerToSet = &AirAACPlayerContextInitializer;
	*ctxFinalizerToSet = &AirAACPlayerContextFinalizer;
}

void AirAACPlayerFinalizer(void *extData) { }
