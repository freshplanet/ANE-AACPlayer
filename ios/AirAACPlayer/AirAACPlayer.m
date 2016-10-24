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
    
    //NSInteger tempUseBA = FPANE_FREObjectToInt(argv[1]);
    
    /*
    FREObject useBA = argv[1];
    uint32_t useBAValue;
    FREGetObjectAsBool(useBA, &useBAValue);
    BOOL usingBA = (useBAValue != 0);*/
    
    //BOOL useByteArray = FPANE_FREObjectToBool(argv[1]);
    
    NSLog(@"-------------------- ??? alors alors --------------------");
    double tempUseBA = FPANE_FREObjectToDouble(argv[1]);
    
    
    NSLog(@"-------------------- useByteArray? %f --------------------", tempUseBA);
    
    
    FREByteArray byteArray;
    
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    
    if(tempUseBA!=0)
    {
        NSLog(@"-------------------- AVANT ACQUI HOHO --------------------");
        
        FREObject objectBA = argv[2];
        
        FREAcquireByteArray(objectBA, &byteArray);
        
        //FREByteArray byteArray;
        //FREAcquireByteArray(argv[1], &byteArray);
        
        NSLog(@"-------------------- APRES ACQUI HOHO -------------------- length = %d", byteArray.length);
        
        if (playerManager)
        {
            NSLog(@"-------------------- AVANT --------------------");
            
            NSMutableData *mydata = [NSMutableData dataWithBytes:byteArray.bytes length:byteArray.length];
            NSLog(@"%@", mydata);
            
            NSLog(@"-------------------- APRES 1 --------------------");
            
            
            [playerManager setCustomData:mydata];
            
            NSLog(@"-------------------- APRES 2 --------------------");
        }
        
        
    }
    
    
    
    /*
    uint32_t string1Length;
    const uint8_t *string1;
    FREGetObjectAsUTF8(argv[1], &string1Length, &string1);
     */
    
    //NSLog(@"PZ PASSED step0 : %s", string1);
    
    
    
    NSLog(@"-------------------- step A --------------------");
    
    if (playerManager && playerManager.player)
    {
        NSLog(@"-------------------- step B --------------------");
        
        if (startTime > 0)
        {
            NSLog(@"-------------------- step C --------------------");
            playerManager.player.currentTime = startTime;
        }
        
        
        //if(byteArray)
        //{
            /*
            NSMutableData *mydata = [NSMutableData data];
            [mydata appendData: (__bridge NSData *) argv[1]];
           
            [playerManager setCustomData:mydata];*/
            
            //NSLog(myArg2);
            
            //NSLog(@"PZ SETCUSTOMDATA step1");
        //}
        
        [playerManager.player play];
        NSLog(@"PZ SETCUSTOMDATA step3");
    }
    return NULL;
}





DEFINE_ANE_FUNCTION(AirAACPlayer_pause)
{
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    if (playerManager && playerManager.player)
    {
        [playerManager.player pause];
    }
    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_stop)
{
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    if (playerManager && playerManager.player)
    {
        [playerManager.player stop];
        [playerManager.player setCurrentTime:0];
    }
    return NULL;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_getDuration)
{
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    return FPANE_IntToFREObject(playerManager && playerManager.player ? 1000*playerManager.player.duration : 0);
}

DEFINE_ANE_FUNCTION(AirAACPlayer_getProgress)
{
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    if (playerManager && playerManager.player)
    {
        double progress = playerManager.player.isPlaying ? playerManager.player.currentTime : playerManager.player.duration;
        return FPANE_IntToFREObject(1000*progress);
    } else
    {
        return FPANE_IntToFREObject(0);
    }
}

DEFINE_ANE_FUNCTION(AirAACPlayer_getDownload)
{
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    int download = playerManager ? playerManager.download : 0;
    return FPANE_DoubleToFREObject(download);
}

DEFINE_ANE_FUNCTION(AirAACPlayer_setVolume)
{
    float volume = FPANE_FREObjectToDouble(argv[0]);
    volume = volume < 0 ? 0 : volume;
    volume = volume > 1 ? 1 : volume;
    AirAACPlayerManager *playerManager = getPlayerManagerFromContext(context);
    if (playerManager && playerManager.player)
    {
        playerManager.player.volume = volume;
    }
    return NULL;
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
        MAP_FUNCTION(AirAACPlayer_getProgress, NULL),
        MAP_FUNCTION(AirAACPlayer_getDownload, NULL),
        MAP_FUNCTION(AirAACPlayer_setVolume, NULL)
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
