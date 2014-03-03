//
//  Sound.m
//  DynamicApp
//
//  Created by ZYYX on 1/20/12.
//  Copyright 2012 ZYYX Inc. All rights reserved.
//

#import "Sound.h"
#import "DynamicAppViewController.h"
#import "Shortcut.h"
#import "PluginResult.h"

@interface Sound(private)
- (BOOL) prepareToPlay:(DynamicAppAudioFile *)audioFile withId:(NSString *)theMediaId;
- (DynamicAppAudioFile*) audioFileForResource:(NSDictionary *)resourceOptions withOptions:(NSDictionary *)options;
- (NSDictionary *) createMediaErrorWithCode:(SoundError)code message:(NSString *)message;
@end

@implementation Sound

@synthesize soundCache;
@synthesize avSession;

- (void) play:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    PluginResult *result = nil;
	NSString *theMediaId = [arguments objectForKey:@"mediaId"];
	BOOL bError = NO;
	NSString *jsString = @"";
    
	DynamicAppAudioFile* audioFile = [self audioFileForResource:arguments withOptions:options];
    
	if (audioFile != nil) {
		if (audioFile.player == nil){
            bError = [self prepareToPlay:audioFile withId:theMediaId];
		}	
		if (!bError){
			// audioFile.player != nil  or player was sucessfully created
            // get the audioSession and set the category to allow Playing when device is locked or ring/silent switch engaged
            if ([self hasAudioSession]) {
                NSError* err = nil;
                [self.avSession setCategory:AVAudioSessionCategoryPlayback error:nil];
                if (![self.avSession  setActive: YES error: &err]){
                    // other audio with higher priority that does not allow mixing could cause this to fail
                    bError = YES;
                }
            }
            if (!bError) {
                NSNumber* loopOption = [arguments objectForKey:@"numberOfLoops"];
                NSInteger numberOfLoops = 0;
                if (loopOption != nil) { 
                    numberOfLoops = [loopOption intValue] - 1;
                }
                audioFile.player.numberOfLoops = numberOfLoops;
                
                if(audioFile.player.isPlaying){
                    [audioFile.player stop];
                    audioFile.player.currentTime = 0;
                }
                [audioFile.player play];
                
                double duration = round(audioFile.player.duration * 1000)/1000;
                
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithInt:SOUND_RUNNING], @"state", [NSNumber numberWithDouble:duration], @"duration", nil]];
                jsString = [result toSuccessCallbackString:theCallbackId];
            }
        } else {
			// error creating the session or player
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[self createMediaErrorWithCode: SOUND_ERR_NONE_SUPPORTED message: nil]];
            jsString = [result toErrorCallbackString:theCallbackId];
		}
	}
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) pause:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
	DynamicAppAudioFile* audioFile = [[self soundCache] objectForKey: [arguments objectForKey:@"mediaId"]];
    NSString *jsString = @"";
	
	if (audioFile != nil && audioFile.player != nil && [audioFile.player isPlaying]) {        
        [audioFile.player pause];
        
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:SOUND_PAUSED];
        jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
	} 
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) stop:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    NSString *theMediaId = [arguments objectForKey:@"mediaId"];
	DynamicAppAudioFile *audioFile = [[self soundCache] objectForKey:theMediaId];
    NSString *jsString = @"";

	if (audioFile != nil && audioFile.player!= nil) {
        [audioFile.player setCurrentTime:0];
        [audioFile.player stop];
        
        audioFile.player = nil;
        [[self soundCache] removeObjectForKey:theMediaId];
              
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:SOUND_STOPPED];
        jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
	} 
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString]; 
}

- (void) getCurrentPosition:(NSMutableDictionary*)arguments withOptions:(NSMutableDictionary*)options {
    DynamicAppAudioFile* audioFile = [[self soundCache] objectForKey: [arguments objectForKey:@"mediaId"]];
    double position = -1;
	
    if (audioFile != nil && audioFile.player != nil) {// && [audioFile.player isPlaying]){ 
        position = round(audioFile.player.currentTime *1000)/1000;
    }
    
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDouble:position];
    NSString *jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString]; 
}

- (void) setCurrentPosition:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    DynamicAppAudioFile* audioFile = [[self soundCache] objectForKey: [arguments objectForKey:@"mediaId"]];

    double position;
    if([arguments objectForKey:@"position"]) {
        position = round([[arguments objectForKey:@"position"] floatValue] * 1000) / 1000;
    } else {
        position = 0.0;
    }
    
    if (audioFile != nil && audioFile.player != nil) {
        if(position < 0.0 || audioFile.player.duration < position) {
            position = 0.0;
        }
        audioFile.player.currentTime = position;
    }
    
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDouble:position];
    NSString *jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString]; 
}

- (void) release:(NSMutableDictionary*)arguments withOptions:(NSMutableDictionary*)options {
    NSString *theMediaId = [arguments objectForKey:@"mediaId"];
    NSString *jsString = @"";
	if (theMediaId != nil){
		DynamicAppAudioFile* audioFile = [[self soundCache] objectForKey: theMediaId];
		if (audioFile != nil){
			if (audioFile.player && [audioFile.player isPlaying]){
				[audioFile.player stop];
			}
            if (self.avSession) {
                [self.avSession setActive:NO error: nil];
                self.avSession = nil;
            }
            
            audioFile.player = nil;
			[[self soundCache] removeObjectForKey: theMediaId];
            
            PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:SOUND_NONE];
            jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
		}
	}
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)audioPlayerDidFinishPlaying:(AVAudioPlayer *)player successfully:(BOOL)flag{
    AudioPlayer *aPlayer = (AudioPlayer *)player;
    NSString *theMediaId = aPlayer.mediaId;
    DynamicAppAudioFile *audioFile = [[self soundCache] objectForKey:aPlayer.mediaId];
    NSString *jsString = nil;
    
    if(flag) {
        jsString = [NSString stringWithFormat: @"%@(\"%@\",%d,%d);", @"DynamicApp.Sound.onStatus", theMediaId, SOUND_STATE, SOUND_STOPPED];
    } else {
        jsString = [NSString stringWithFormat: @"%@(\"%@\",%d,%@);", @"DynamicApp.Sound.onStatus", theMediaId, SOUND_ERROR, [self createMediaErrorWithCode:SOUND_ERR_DECODE message:nil]];
    }
    
    if(self.avSession) {
        [self.avSession setActive:NO error:nil];
    }
    
    audioFile.player = nil;
    [[self soundCache] removeObjectForKey:theMediaId];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

// Creates or gets the cached audio file resource object
- (DynamicAppAudioFile*) audioFileForResource:(NSDictionary *)resourceOptions withOptions:(NSDictionary *)options {
    NSString *resourcePath = [resourceOptions objectForKey:@"audioFile"];
    NSString *theMediaId = [resourceOptions objectForKey:@"mediaId"];
	BOOL bError = NO;
	SoundError errcode = SOUND_ERR_NONE_SUPPORTED;
    NSString *errMsg = @"";
	DynamicAppAudioFile* audioFile = nil;
	NSURL* resourceURL = nil;
	
	if ([self soundCache] == nil) {
		[self setSoundCache: [NSMutableDictionary dictionaryWithCapacity:1]];
	}else {
		audioFile = [[self soundCache] objectForKey: theMediaId];
	}
	if (audioFile == nil){
		// validate resourcePath and create
        
		if (resourcePath == nil || ![resourcePath isKindOfClass:[NSString class]] || [resourcePath isEqualToString:@""]){
			bError = YES;
			errcode = SOUND_ERR_ABORTED;
			errMsg = @"invalid media src argument";
		} else {
            if (![resourcePath hasPrefix:HTTP_SCHEME_PREFIX]){
                resourcePath = [RESOURCES_FOLDER_NAME stringByAppendingPathComponent:resourcePath];
            }
			resourceURL = [DynamicAppPlugin urlForResource:resourcePath];
		}
        
		if (resourceURL == nil) {
			bError = YES;
			errcode = SOUND_ERR_ABORTED;
			errMsg = [NSString stringWithFormat: @"Cannot use audio file from resource '%@'", resourcePath];
		}
        
		if (bError) {
            PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[self createMediaErrorWithCode:errcode message:errMsg]];
            NSString *jsString = [result toErrorCallbackString:[options objectForKey:@"callbackId"]];
            [self.webView stringByEvaluatingJavaScriptFromString:jsString]; 
		} else {
			audioFile = [[DynamicAppAudioFile alloc] init];
			audioFile.resourcePath = resourcePath;
			audioFile.resourceURL = resourceURL;
            
			[[self soundCache] setObject:audioFile forKey:theMediaId];
            [audioFile release];
		}
	}
	return audioFile;
}


// returns whether or not audioSession is available - creates it if necessary 
- (BOOL) hasAudioSession {
    BOOL bSession = YES;
    if (!self.avSession) {
        NSError* error = nil;
        
        self.avSession = [AVAudioSession sharedInstance];
        if (error) {
            self.avSession = nil;
            bSession = NO;
        }
    }
    return bSession;
}


- (BOOL) prepareToPlay:(DynamicAppAudioFile*)audioFile withId:(NSString*)theMediaId {
    BOOL bError = NO;
    NSError* playerError = nil;
    
    // create the player
    NSURL* resourceURL = audioFile.resourceURL;
    NSData *data = nil;
    if ([resourceURL isFileURL]) {
        audioFile.player = [[[ AudioPlayer alloc ] initWithContentsOfURL:resourceURL error:&playerError] autorelease];
        if(playerError) {
            bError = YES;
        }
    } else {
        NSURLRequest *request = [NSURLRequest requestWithURL:resourceURL];
        NSURLResponse *response = nil;
        data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&playerError];
        
        audioFile.player = [[[ AudioPlayer alloc ] initWithData:data error:&playerError] autorelease];
        if(playerError || data == nil) {
            bError = YES;
        }
    }
    
    if (bError) {
        audioFile.player = nil;
        if (self.avSession) {
            [self.avSession setActive:NO error:nil];
        }
    } else {
        audioFile.player.mediaId = theMediaId;
        audioFile.player.delegate = self;
        bError = ![audioFile.player prepareToPlay];
    }
    return bError;
}


// helper function to create a error object string
- (NSDictionary *) createMediaErrorWithCode:(SoundError)code message:(NSString *) message {
    NSMutableDictionary* errorDict = [NSMutableDictionary dictionaryWithCapacity:2];
    [errorDict setObject: [NSNumber numberWithUnsignedInt: code] forKey:@"code"];
    [errorDict setObject: message ? message : @"" forKey: @"message"];
    return errorDict;
}

- (void) dealloc {
    self.soundCache = nil;
    self.avSession = nil;
	[super dealloc];
}

@end

@implementation DynamicAppAudioFile

@synthesize resourcePath;
@synthesize resourceURL;
@synthesize player;

- (void) dealloc {
	self.resourcePath = nil;
    self.resourceURL = nil;
    self.player = nil;
    
	[super dealloc];
}

@end

@implementation AudioPlayer
@synthesize mediaId;

- (void) dealloc {
    self.mediaId = nil;
	
	[super dealloc];
}

@end

