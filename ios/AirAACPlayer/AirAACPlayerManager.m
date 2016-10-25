//
//  AirAACPlayerManager.m
//  AirAACPlayer
//
//  Created by Alexis Taugeron on 6/27/14.
//
//

#import "AirAACPlayerManager.h"
#import "FPANEUtils.h"

@interface AirAACPlayerManager ()

@property (nonatomic, readonly) FREContext context;
@property (nonatomic, readonly) NSMutableData *data;
@property (nonatomic, readwrite) long long size;
@property (nonatomic, readwrite) long downloaded;
@property (nonatomic, readwrite) int download;
@property (nonatomic, strong) NSURLConnection *connection;
@property (nonatomic, strong, readwrite) AVAudioPlayer *player;

@end

@implementation AirAACPlayerManager

- (instancetype)initWithContext:(FREContext)context
{
    self = [super init];
    if (self)
    {
        _context = context;
        _data = [NSMutableData data];
    }
    return self;
}

- (void)dealloc
{
    [self.connection cancel];
    [self.player stop];
}

- (void)loadURL:(NSURL *)url
{
    if ([url isFileURL]) {
        
        NSError *error;
        self.player = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:&error];
        [self handlePlayerEventDispatch:error];
    }
    else
    {
        NSURLRequest *request = [NSURLRequest requestWithURL:url];
        self.connection = [NSURLConnection connectionWithRequest:request delegate:self];
        self.downloaded = 0;
        self.size = 0;
        [self.connection start];
    }
}


- (void)setCustomData:(NSMutableData *)customData
{
    NSError *error;
    self.player = [[AVAudioPlayer alloc] initWithData:customData error:&error];
    [self handlePlayerEventDispatch:error];
}



- (void)handlePlayerEventDispatch:(NSError*)error
{
    if (self.player)
        FPANE_DispatchEventWithInfo(self.context, @"AAC_PLAYER_PREPARED", @"OK");
    else
        FPANE_DispatchEventWithInfo(self.context, @"AAC_PLAYER_ERROR", [error description]);
}

#pragma mark - NSURLConnectionDataDelegate

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data
{
    if (connection == self.connection)
    {
        self.downloaded += [data length];
        self.download = self.size ? (int)(self.downloaded * 100 / self.size) : 0;
        FPANE_DispatchEvent(self.context, @"AAC_PLAYER_DOWNLOAD");
        [self.data appendData:data];
    }
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response
{
    if (connection == self.connection)
    {
        self.size = response.expectedContentLength;
    }
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection
{
    if (connection == self.connection)
    {
        NSError *error;
        self.player = [[AVAudioPlayer alloc] initWithData:self.data error:&error];
        [self handlePlayerEventDispatch:error];
    }
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error
{
    if (connection == self.connection)
    {
        FPANE_DispatchEventWithInfo(self.context, @"AAC_PLAYER_ERROR", [error description]);
    }
}

@end
