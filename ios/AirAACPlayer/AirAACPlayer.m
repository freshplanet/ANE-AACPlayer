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
#import "AirAACPlayerManager.h"
#import "FPANEUtils.h"

AirAACPlayerManager *getPlayerManagerFromContext(FREContext context)
{
    CFTypeRef playerManagerRef;
    FREGetContextNativeData(context, (void *)&playerManagerRef);
    return (__bridge AirAACPlayerManager *)playerManagerRef;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_load)
{
    NSURL *url = [NSURL URLWithString:FPANE_FREObjectToNSString(argv[0])];
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    [playerManager loadURL:url];
    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_play)
{
    double startTime = FPANE_FREObjectToDouble(argv[0]);
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    if (startTime > 0) playerManager.player.currentTime = startTime;
    [playerManager.player play];
    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_pause)
{
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    [playerManager.player pause];
    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_stop)
{
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    [playerManager.player stop];
    playerManager.player.currentTime = 0;
    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_getDuration)
{
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    return FPANE_IntToFREObject(1000*playerManager.player.duration);
}

DEFINE_ANE_FUNCTION(AirAACPlayer_getProgress)
{
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    double progress = playerManager.player.isPlaying ? playerManager.player.currentTime : playerManager.player.duration;
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
    
    AirAACPlayerManager *playerManager = [[AirAACPlayerManager alloc] initWithContext:ctx];
    FRESetContextNativeData(ctx, (void *)CFBridgingRetain(playerManager));
}

void AirAACPlayerContextFinalizer(FREContext ctx)
{
    CFTypeRef playerManagerRef;
    FREGetContextNativeData(ctx, (void **)&playerManagerRef);
    CFBridgingRelease(playerManagerRef);
}

void AirAACPlayerInitializer(void** extDataToSet, FREContextInitializer* ctxInitializerToSet, FREContextFinalizer* ctxFinalizerToSet)
{
	*extDataToSet = NULL;
	*ctxInitializerToSet = &AirAACPlayerContextInitializer;
	*ctxFinalizerToSet = &AirAACPlayerContextFinalizer;
}

void AirAACPlayerFinalizer(void *extData) {}
