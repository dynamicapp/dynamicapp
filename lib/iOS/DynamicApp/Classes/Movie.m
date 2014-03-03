//
//  Movie.m
//  DynamicApp
//
//  Created by ZYYX on 2/2/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "Movie.h"
#import "DynamicAppViewController.h"
#import "PluginResult.h"
#import "Shortcut.h"
#import "NSData+Base64.h"
#import <AVFoundation/AVFoundation.h>

@interface Movie(private)
- (BOOL) prepareToPlay:(DynamicAppMovieFile *)movieFile withId:(NSString *)theMediaId withOptions:(NSDictionary *)options;
- (DynamicAppMovieFile *) movieFileForResource:(NSDictionary *)resourceOptions withOptions:(NSDictionary *)options;
- (NSDictionary *) createMediaErrorWithCode:(MovieError)code message:(NSString*)message;
- (void)moviePreloadDidFinish:(NSNotification *)notification;
- (void)moviePlaybackComplete:(NSNotification *)notification;
- (void)movieDurationAvailable:(NSNotification *)notification;
@end

@implementation Movie

@synthesize movieCache;


- (void) play:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    PluginResult *result = nil;
    NSString *theMediaId = [arguments objectForKey:@"mediaId"];
	NSString* jsString = nil;
    BOOL bError = NO;
    
    DynamicAppMovieFile *movieFile = [self movieFileForResource:arguments withOptions:options];
    
    if (movieFile != nil) {
        movieFile.callbackId = theCallbackId;
        
        if([movieFile.player playbackState] == MPMoviePlaybackStatePlaying) {
            [movieFile.player stop];
            movieFile.player.currentPlaybackTime = 0;
        }
        
        if([movieFile.player playbackState] != MPMoviePlaybackStatePaused) {
            bError = [self prepareToPlay:movieFile withId:theMediaId withOptions:arguments];
        }
        if (!bError) {
            NSNumber* loopOption = [arguments objectForKey:@"numberOfLoops"];
            if (loopOption != nil) { 
                movieFile.numberOfLoops = [loopOption intValue] - 1;
            }
            
            if([movieFile.player loadState] & MPMovieLoadStatePlayable) {
                [movieFile.player play];
                movieFile.isPlaying = YES;
                
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:MOVIE_RUNNING];
                jsString = [result toSuccessCallbackString:theCallbackId];
            }        
        } else {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[self createMediaErrorWithCode: MOVIE_ERR_NONE_SUPPORTED message: nil]];
            jsString = [result toErrorCallbackString:theCallbackId];
        }
	}
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    if(jsString) {
        [self.webView stringByEvaluatingJavaScriptFromString:jsString];
    }
    
}

- (void) pause:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
	DynamicAppMovieFile *movieFile = [[self movieCache] objectForKey: [arguments objectForKey:@"mediaId"]];
    NSString *jsString = @"";
	
	if (movieFile != nil && movieFile.player != nil) {
        [movieFile.player pause];
        movieFile.isPlaying = NO;
        
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:MOVIE_PAUSED];
        jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
	} 
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) stop:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
	DynamicAppMovieFile* movieFile = [[self movieCache] objectForKey: [arguments objectForKey:@"mediaId"]];
    
    if (movieFile != nil && movieFile.player != nil) {
        movieFile.isPlaying = NO;
        [movieFile.player setCurrentPlaybackTime:0];
        [movieFile.player stop];	
    }
}

- (void) getCurrentPosition:(NSDictionary *)arguments withOptions:(NSDictionary *)options {    
    DynamicAppMovieFile* movieFile = [[self movieCache] objectForKey: [arguments objectForKey:@"mediaId"]];
    double position = 0;
    
    if (movieFile != nil && movieFile.player != nil) {// && movieFile.isPlaying){ 
        position = round(movieFile.player.currentPlaybackTime *1000)/1000;
    }
    
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDouble:position];
    NSString *jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) setCurrentPosition:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
	NSString *theMediaId = [arguments objectForKey:@"mediaId"]; 
	
    DynamicAppMovieFile* movieFile = [[self movieCache] objectForKey: theMediaId];
    
    double position;
    if([arguments objectForKey:@"position"]) {
        position = round([[arguments objectForKey:@"position"] floatValue] * 1000) / 1000;
    } else {
        position = 0.0;
    }
    
    if (movieFile != nil && movieFile.player != nil) {
        if(position < 0.0 || movieFile.player.duration < position) {
            position = 0.0;
        }
        movieFile.player.currentPlaybackTime = position;
    }
    
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDouble:position];
    NSString *jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString]; 
}

- (void) release:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theMediaId = [arguments objectForKey:@"mediaId"];
    NSString *jsString = @"";
    
	if (theMediaId != nil){
		DynamicAppMovieFile* movieFile = [[self movieCache] objectForKey: theMediaId];
        if (movieFile != nil && movieFile.player && [movieFile.player playbackState] == MPMoviePlaybackStatePlaying){
			[movieFile.player stop];
		}
        
        movieFile.player = nil;
        [[self movieCache] removeObjectForKey: theMediaId];
        
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:MOVIE_NONE];
        jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
	}
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];    
}

- (void) getThumbnail:(NSDictionary *)arguments withOptions:(NSDictionary *)options {    
    DynamicAppMovieFile *movieFile = [self movieFileForResource:arguments withOptions:options];
    PluginResult *result = nil;
    NSString *jsString = nil;
    if(movieFile != nil) {
        NSTimeInterval offset = [[arguments objectForKey:@"offset"] doubleValue];
        CGFloat quality = [[arguments objectForKey:@"quality"] floatValue];
        MoviePlayer *player = [[MoviePlayer alloc] initWithContentURL:movieFile.resourceURL];
        player.shouldAutoplay = NO;

//      UIImage *thumbnail = [player thumbnailImageAtTime:offset timeOption:MPMovieTimeOptionNearestKeyFrame];
        
		//address deprecated method thumbnailAtTie:timeOption by ALFRED
        AVURLAsset *asset = [[AVURLAsset alloc] initWithURL:movieFile.resourceURL options:nil];
        AVAssetImageGenerator *generateImg = [[AVAssetImageGenerator alloc] initWithAsset:asset];
        NSError *error = nil;
        CMTime time = CMTimeMake(offset, 1);
        CGImageRef refImg = [generateImg copyCGImageAtTime:time actualTime:NULL error:&error];
        UIImage *thumbnail = [[UIImage alloc] initWithCGImage:refImg];
   		//end of revision

        if([arguments objectForKey:@"width"] && [arguments objectForKey:@"height"]) {
            CGSize newSize = CGSizeMake([[arguments objectForKey:@"width"] floatValue], [[arguments objectForKey:@"height"] floatValue]);
            
            UIGraphicsBeginImageContext(newSize);
            [thumbnail drawInRect:CGRectMake(0, 0, newSize.width, newSize.height)];
            thumbnail = UIGraphicsGetImageFromCurrentImageContext();    
            UIGraphicsEndImageContext();
        }
        [player release];
        NSData *thumbnailData = UIImageJPEGRepresentation(thumbnail, quality);
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsString: [NSString stringWithFormat: @"data:image/jpeg;base64,%@", [thumbnailData base64EncodedString]]];
        jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
        
        [thumbnail release];
        CGImageRelease(refImg);
        [generateImg release];
        [asset release];
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[self createMediaErrorWithCode: MOVIE_ERR_NONE_SUPPORTED message: nil]];
        jsString = [result toErrorCallbackString:[options objectForKey:@"callbackId"]];
    }
    
    [[self movieCache] removeObjectForKey:[arguments objectForKey:@"mediaId"]];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)moviePlaybackComplete:(NSNotification *)notification {
    MoviePlayer *moviePlayer = [notification object];

    DynamicAppMovieFile *movieFile = [movieCache objectForKey:moviePlayer.mediaId];
    
    NSInteger reason = [[notification.userInfo objectForKey:MPMoviePlayerPlaybackDidFinishReasonUserInfoKey] integerValue];
    if (reason == MPMovieFinishReasonPlaybackEnded && movieFile.numberOfLoops > 0) {
        [movieFile.player play];
        movieFile.numberOfLoops = movieFile.numberOfLoops - 1;
        return;
    }
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerPlaybackDidFinishNotification object:moviePlayer];
    
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerDidExitFullscreenNotification object:moviePlayer];
    
    [movieFile.player setCurrentPlaybackTime:0];
    [movieFile.player.view removeFromSuperview];
    [movieFile setIsPlaying:NO];
    if([movieFile.player playbackState] != MPMoviePlaybackStateStopped) {
        [movieFile.player stop];
    }    
    
    NSString *jsString = [NSString stringWithFormat: @"%@(\"%@\",%d,%d);", @"DynamicApp.Movie.onStatus", moviePlayer.mediaId, MOVIE_STATE, MOVIE_STOPPED];
    
    movieFile.player = nil;
    [[self movieCache] removeObjectForKey:moviePlayer.mediaId];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)movieDurationAvailable:(NSNotification *)notification {
    MoviePlayer *moviePlayer = [notification object];
    
    double duration = round(moviePlayer.duration * 1000)/1000;    
    NSString *jsString = [NSString stringWithFormat: @"%@(\"%@\",%d,%.3f);", @"DynamicApp.Movie.onStatus", moviePlayer.mediaId, MOVIE_DURATION, duration];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMovieDurationAvailableNotification object:moviePlayer];
}

- (void)moviePreloadDidFinish:(NSNotification *)notification {
    MoviePlayer *moviePlayer = [notification object];
    NSString *jsString = @"";
    
    if([moviePlayer loadState] & MPMovieLoadStatePlayable) {
        DynamicAppMovieFile *movieFile = [movieCache objectForKey:moviePlayer.mediaId];
        [[NSNotificationCenter defaultCenter] removeObserver:self name:MPMoviePlayerLoadStateDidChangeNotification object:moviePlayer];
    
        [moviePlayer play];
        
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:MOVIE_RUNNING];
        jsString = [result toSuccessCallbackString:movieFile.callbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

// Creates or gets the cached movie file resource object
- (DynamicAppMovieFile *) movieFileForResource:(NSDictionary *)resourceOptions withOptions:(NSDictionary *)options {
    NSString *theMediaId = [resourceOptions objectForKey:@"mediaId"];
	BOOL bError = NO;
	MovieError errcode = MOVIE_ERR_NONE_SUPPORTED;
    NSString *errMsg = @"";    
	DynamicAppMovieFile* movieFile = nil;
	NSURL *resourceURL = nil;
    NSString *resourcePath = [resourceOptions objectForKey:@"movieFile"];
    
	if ([self movieCache] == nil) {
		[self setMovieCache: [NSMutableDictionary dictionaryWithCapacity:1]];
	} else {
		movieFile = [[self movieCache] objectForKey:theMediaId];
	}
	if (movieFile == nil){
		// validate resourcePath and create
        
		if (resourcePath == nil || ![resourcePath isKindOfClass:[NSString class]] || [resourcePath isEqualToString:@""]){
			bError = YES;
			errcode = MOVIE_ERR_ABORTED;
			errMsg = @"invalid media src argument";
		} else {
            if (![resourcePath hasPrefix:HTTP_SCHEME_PREFIX] && ![resourcePath hasPrefix:FILE_SCHEME_PREFIX]){
                resourcePath = [RESOURCES_FOLDER_NAME stringByAppendingPathComponent:resourcePath];
            }
			resourceURL = [DynamicAppPlugin urlForResource:resourcePath];
        }
        
        if (resourceURL == nil) {
			bError = YES;
			errcode = MOVIE_ERR_ABORTED;
			errMsg = [NSString stringWithFormat: @"Cannot use movie file from resource '%@'", resourcePath];
		}
        
		if (bError) {            
            PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[self createMediaErrorWithCode:errcode message:errMsg]];
            NSString *jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
            [self.webView stringByEvaluatingJavaScriptFromString:jsString]; 
		} else {
			movieFile = [[DynamicAppMovieFile alloc] init];
			movieFile.resourcePath = resourcePath;
			movieFile.resourceURL = resourceURL;
            
			[[self movieCache] setObject:movieFile forKey:theMediaId];
            [movieFile release];
		}
	}
    
	return movieFile;
}

- (BOOL) prepareToPlay:(DynamicAppMovieFile *)movieFile withId:(NSString *)theMediaId withOptions:(NSDictionary *)options {
    
    BOOL bError = NO;
    NSURL* resourceURL = movieFile.resourceURL;
    
    movieFile.player = [[[MoviePlayer alloc] initWithContentURL:resourceURL] autorelease];
  
    if ([resourceURL isFileURL]) {
        [movieFile.player setMovieSourceType:MPMovieSourceTypeFile];
    } else {
        [movieFile.player setMovieSourceType:MPMovieSourceTypeUnknown];
    }
        
    if (movieFile.player != nil) {
        movieFile.player.mediaId = theMediaId; 
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(moviePlaybackComplete:) name:MPMoviePlayerPlaybackDidFinishNotification object:movieFile.player];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(moviePlaybackComplete:) name:MPMoviePlayerDidExitFullscreenNotification object:movieFile.player];   
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(movieDurationAvailable:) name:MPMovieDurationAvailableNotification object:movieFile.player];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(moviePreloadDidFinish:) name:MPMoviePlayerLoadStateDidChangeNotification object:movieFile.player];
         
        if([options objectForKey:@"frame"]) {
            NSDictionary *frameValues = [options objectForKey:@"frame"];
            [movieFile.player.view setFrame:CGRectMake(
                                           [[frameValues objectForKey:@"posX"] floatValue],
                                           [[frameValues objectForKey:@"posY"] floatValue],
                                           [[frameValues objectForKey:@"width"] floatValue],
                                           [[frameValues objectForKey:@"height"] floatValue])];
        }
        
        if([options objectForKey:@"scalingMode"]) {
            [movieFile.player setScalingMode:(MPMovieScalingMode)[[options objectForKey:@"scalingMode"] intValue]];
        }
        
        if([options objectForKey:@"controlStyle"]) {
            [movieFile.player setControlStyle:(MPMovieControlStyle)[[options objectForKey:@"controlStyle"] intValue]];
            
            if([[options objectForKey:@"controlStyle"] intValue] == MPMovieControlStyleNone) {
                movieFile.player.view.userInteractionEnabled = NO;                
            }
        }
        
        [[self.webView.subviews objectAtIndex:0] addSubview:movieFile.player.view];
         
        if(![options objectForKey:@"scalingMode"]) {
            [movieFile.player setFullscreen:YES animated:YES];
        }
        
        movieFile.player.shouldAutoplay = NO;
        movieFile.numberOfLoops = 0;
        [movieFile.player prepareToPlay];
    } else {
        movieFile.player = nil;
        bError = YES;
    }
    return bError;
}

// helper function to create a error object string
- (NSDictionary *)createMediaErrorWithCode:(MovieError)code message:(NSString*)message {
    NSMutableDictionary* errorDict = [NSMutableDictionary dictionaryWithCapacity:2];
    [errorDict setObject: [NSNumber numberWithUnsignedInt: code] forKey:@"code"];
    [errorDict setObject: message ? message : @"" forKey: @"message"];
    return errorDict;
}

- (void) dealloc {
    self.movieCache = nil;
	[super dealloc];
}

@end

@implementation DynamicAppMovieFile

@synthesize resourcePath;
@synthesize resourceURL;
@synthesize player;
@synthesize isPlaying;
@synthesize callbackId;
@synthesize numberOfLoops;

- (void) dealloc {
	self.resourcePath = nil;
    self.resourceURL = nil;
    self.player = nil;
    self.callbackId = nil;
    
	[super dealloc];
}

@end

@implementation MoviePlayer
@synthesize mediaId;

- (void) dealloc {
    self.mediaId = nil;
	
	[super dealloc];
}

@end
