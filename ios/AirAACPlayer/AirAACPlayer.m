/*
 * Copyright 2017 FreshPlanet
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "AirAACPlayer.h"
#import "Constants.h"


@interface AirAACPlayer ()
@property (nonatomic, readonly) FREContext context;
@property (nonatomic, strong, readwrite) AVAudioPlayer *player;
@property (nonatomic, retain) NSMutableData *dataToDownload;
@property (nonatomic) float downloadSize;
@end

@implementation AirAACPlayer

- (instancetype)initWithContext:(FREContext)extensionContext {
    
    if ((self = [super init])) {
        
        _context = extensionContext;
    }
    
    return self;
}

- (void) sendLog:(NSString*)log {
    [self sendEvent:@"log" level:log];
}

- (void) sendEvent:(NSString*)code {
    [self sendEvent:code level:@""];
}

- (void) sendEvent:(NSString*)code level:(NSString*)level {
    FREDispatchStatusEventAsync(_context, (const uint8_t*)[code UTF8String], (const uint8_t*)[level UTF8String]);
}

- (void) loadURL:(NSURL *)url {
    if ([url isFileURL]) {
        
        NSError *error;
        self.player = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:&error];
        
        if (error != nil) {
            [self sendEvent:kAirAACPlayerEvent_AAC_PLAYER_ERROR level:error.localizedDescription];
        }
        
    }
    else
    {
        NSURLSessionConfiguration *defaultConfigObject = [NSURLSessionConfiguration defaultSessionConfiguration];
        
        NSURLSession *defaultSession = [NSURLSession sessionWithConfiguration: defaultConfigObject delegate: self delegateQueue: [NSOperationQueue mainQueue]];
        
        NSURLSessionDataTask *dataTask = [defaultSession dataTaskWithURL: url];
        
        [dataTask resume];

    }
}

- (void) play:(double)startTime {
    if (self.player)
    {
        if (startTime > 0)
        {
            self.player.currentTime = startTime;
        }
        [self.player setDelegate:self];
        [self.player play];
    }
}

- (void) pause {
    if (self.player)
    {
        [self.player pause];
    }
}

- (void) stop {
    if (self.player)
    {
        [self.player stop];
        [self.player setCurrentTime:0];
    }
}

#pragma mark NSURLSessionDataDelegate

- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveResponse:(NSURLResponse *)response completionHandler:(void (^)(NSURLSessionResponseDisposition disposition))completionHandler {
    completionHandler(NSURLSessionResponseAllow);
    
    _downloadSize = [response expectedContentLength];
    _dataToDownload = [[NSMutableData alloc]init];
}


- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data {
    [_dataToDownload appendData:data];
    int progress = ([ _dataToDownload length ]/_downloadSize ) * 100;
    [self sendEvent:kAirAACPlayerEvent_AAC_PLAYER_DOWNLOAD level:[NSString stringWithFormat:@"%i", progress]];
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task
didCompleteWithError:(NSError *)error {
    if (error) {
       [self sendEvent:kAirAACPlayerEvent_AAC_PLAYER_ERROR level:error.localizedDescription];
    }
    else {
        
        NSError *playerError;
        self.player = [[AVAudioPlayer alloc] initWithData:_dataToDownload error:&playerError];
        if (playerError != nil) {
            [self sendEvent:kAirAACPlayerEvent_AAC_PLAYER_ERROR level:playerError.localizedDescription];
        }
        else {
            [self sendEvent:kAirAACPlayerEvent_AAC_PLAYER_PREPARED];
        }

        
    }
}

#pragma AVAudioPlayerDelegate

- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player
                       successfully:(BOOL)flag {
    
    [self sendEvent:kAirAACPlayerEvent_AAC_PLAYER_PLAYBACK_FINISHED];
    
}
- (void)audioPlayerDecodeErrorDidOccur:(AVAudioPlayer *)player
                                 error:(NSError *)error {
    
    [self sendEvent:kAirAACPlayerEvent_AAC_PLAYER_ERROR level:error.localizedDescription];
}


@end

AirAACPlayer* GetAirAACPlayerContextNativeData(FREContext context) {
    
    CFTypeRef controller;
    FREGetContextNativeData(context, (void**)&controller);
    return (__bridge AirAACPlayer*)controller;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_load) {
 
    AirAACPlayer* controller = GetAirAACPlayerContextNativeData(context);
    
    if (!controller)
        return FPANE_CreateError(@"context's AirAACPlayer is null", 0);
    
    @try {
        
        NSURL *url = [NSURL URLWithString:FPANE_FREObjectToNSString(argv[0])];
        [controller loadURL:url];
        
    }
    @catch (NSException *exception) {
        [controller sendLog:[@"Exception occured while trying to load : " stringByAppendingString:exception.reason]];
    }
    
    return nil;
    
}

DEFINE_ANE_FUNCTION(AirAACPlayer_play) {
    
    AirAACPlayer* controller = GetAirAACPlayerContextNativeData(context);
    
    if (!controller)
        return FPANE_CreateError(@"context's AirAACPlayer is null", 0);
   
    @try {
        
        double startTime = FPANE_FREObjectToDouble(argv[0]);
        [controller play:startTime];
        
    }
    @catch (NSException *exception) {
        [controller sendLog:[@"Exception occured while trying to play : " stringByAppendingString:exception.reason]];
    }
    return nil;
}

DEFINE_ANE_FUNCTION(AirAACPlayer_pause) {
    
    AirAACPlayer* controller = GetAirAACPlayerContextNativeData(context);
    
    if (!controller)
        return FPANE_CreateError(@"context's AirAACPlayer is null", 0);
    
    [controller pause];
    
    return nil;
    
}

DEFINE_ANE_FUNCTION(AirAACPlayer_stop) {
    
    AirAACPlayer* controller = GetAirAACPlayerContextNativeData(context);
    
    if (!controller)
        return FPANE_CreateError(@"context's AirAACPlayer is null", 0);
    
    @try {
        
        [controller stop];
        
    }
    @catch (NSException *exception) {
        [controller sendLog:[@"Exception occured while trying to stop : " stringByAppendingString:exception.reason]];
    }
    
}

DEFINE_ANE_FUNCTION(AirAACPlayer_getDuration) {
    
    AirAACPlayer* controller = GetAirAACPlayerContextNativeData(context);
    
    if (!controller)
        return FPANE_CreateError(@"context's AirAACPlayer is null", 0);
    
     return FPANE_IntToFREObject(controller.player ? 1000*controller.player.duration : 0);
    
}

DEFINE_ANE_FUNCTION(AirAACPlayer_getProgress) {
    
    AirAACPlayer* controller = GetAirAACPlayerContextNativeData(context);
    
    if (!controller)
        return FPANE_CreateError(@"context's AirAACPlayer is null", 0);
    
    if (controller.player){
        double progress = controller.player.isPlaying ? controller.player.currentTime : controller.player.duration;
        return FPANE_IntToFREObject(1000*progress);
    } else{
        return FPANE_IntToFREObject(0);
    }
    
}

DEFINE_ANE_FUNCTION(AirAACPlayer_setVolume) {
    
    AirAACPlayer* controller = GetAirAACPlayerContextNativeData(context);
    
    if (!controller)
        return FPANE_CreateError(@"context's AirAACPlayer is null", 0);
    
    @try {
        
        float volume = FPANE_FREObjectToDouble(argv[0]);
        volume = volume < 0 ? 0 : volume;
        volume = volume > 1 ? 1 : volume;
        
        if (controller.player)
        {
            controller.player.volume = volume;
        }
        
    }
    @catch (NSException *exception) {
        [controller sendLog:[@"Exception occured while trying to setVolume : " stringByAppendingString:exception.reason]];
    }
    
    return nil;
    
}

#pragma mark - ANE setup

void AirAACPlayerContextInitializer(void* extData, const uint8_t* ctxType, FREContext ctx,
                                uint32_t* numFunctionsToTest, const FRENamedFunction** functionsToSet) {
    
    AirAACPlayer* controller = [[AirAACPlayer alloc] initWithContext:ctx];
    FRESetContextNativeData(ctx, (void*)CFBridgingRetain(controller));
    
    static FRENamedFunction functions[] = {
        MAP_FUNCTION(AirAACPlayer_load, NULL),
        MAP_FUNCTION(AirAACPlayer_play, NULL),
        MAP_FUNCTION(AirAACPlayer_pause, NULL),
        MAP_FUNCTION(AirAACPlayer_stop, NULL),
        MAP_FUNCTION(AirAACPlayer_getDuration, NULL),
        MAP_FUNCTION(AirAACPlayer_getProgress, NULL),
        MAP_FUNCTION(AirAACPlayer_setVolume, NULL)
    };
    
    *numFunctionsToTest = sizeof(functions) / sizeof(FRENamedFunction);
    *functionsToSet = functions;
    
}

void AirAACPlayerContextFinalizer(FREContext ctx) {
    CFTypeRef controller;
    FREGetContextNativeData(ctx, (void **)&controller);
    CFBridgingRelease(controller);
}

void AirAACPlayerInitializer(void** extDataToSet, FREContextInitializer* ctxInitializerToSet, FREContextFinalizer* ctxFinalizerToSet ) {
    *extDataToSet = NULL;
    *ctxInitializerToSet = &AirAACPlayerContextInitializer;
    *ctxFinalizerToSet = &AirAACPlayerContextFinalizer;
}

void AirAACPlayerFinalizer(void *extData) {}
