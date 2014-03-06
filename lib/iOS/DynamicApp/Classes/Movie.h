/*
 * Copyright (C) 2014 ZYYX, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import <Foundation/Foundation.h>
#import <MediaPlayer/MediaPlayer.h>
#import "DynamicAppPlugin.h"

enum MovieError {
	MOVIE_ERR_ABORTED = 1,
	MOVIE_ERR_NETWORK = 2,
	MOVIE_ERR_DECODE = 3,
	MOVIE_ERR_NONE_SUPPORTED = 4
};
typedef NSUInteger MovieError;

enum MovieStates {
	MOVIE_NONE = 0,
	MOVIE_STARTING = 1,
	MOVIE_RUNNING = 2,
	MOVIE_PAUSED = 3,
	MOVIE_STOPPED = 4
};
typedef NSUInteger MovieStates;

enum MovieMsg {
	MOVIE_STATE = 1,
	MOVIE_DURATION = 2,
    MOVIE_POSITION = 3,
	MOVIE_ERROR = 9
};
typedef NSUInteger MovieMsg;


@interface MoviePlayer : MPMoviePlayerController {
	NSString *mediaId;
}
@property (nonatomic,copy) NSString *mediaId;
@end

@interface DynamicAppMovieFile : NSObject {
	NSString *resourcePath;
	NSURL *resourceURL;
    MoviePlayer *player;
    BOOL isPlaying;
    NSString *callbackId;
    NSInteger numberOfLoops;
}

@property (nonatomic, retain) NSString *resourcePath;
@property (nonatomic, retain) NSURL *resourceURL;
@property (nonatomic, retain) MoviePlayer *player;
@property (nonatomic) BOOL isPlaying;
@property (nonatomic, retain) NSString *callbackId;
@property (nonatomic) NSInteger numberOfLoops;

@end


@interface Movie : DynamicAppPlugin {
    NSMutableDictionary *movieCache;
}

@property (nonatomic, retain) NSMutableDictionary *movieCache;

- (void) play:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) pause:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) stop:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) getCurrentPosition:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) setCurrentPosition:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) release:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) getThumbnail:(NSDictionary *)arguments withOptions:(NSDictionary *)options;

@end
