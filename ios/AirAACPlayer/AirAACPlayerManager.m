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
        [self.connection start];
    }
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
        [self.data appendData:data];
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
