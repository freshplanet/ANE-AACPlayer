//
//  AirAACPlayerManager.h
//  AirAACPlayer
//
//  Created by Alexis Taugeron on 6/27/14.
//
//

#import "FlashRuntimeExtensions.h"
#import <AVFoundation/AVFoundation.h>

@interface AirAACPlayerManager : NSObject <NSURLConnectionDataDelegate, AVAudioPlayerDelegate>

@property (nonatomic, readonly) AVAudioPlayer *player;
@property (nonatomic, readonly) int download;

- (instancetype)initWithContext:(FREContext)context;
- (void)loadURL:(NSURL *)url;
- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player
                       successfully:(BOOL)flag;
- (void)audioPlayerDecodeErrorDidOccur:(AVAudioPlayer *)player
                                 error:(NSError *)error;

@end
