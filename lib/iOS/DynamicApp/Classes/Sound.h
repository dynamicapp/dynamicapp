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

#import "DynamicAppPlugin.h"
#import <AudioToolbox/AudioServices.h>
#import <AVFoundation/AVFoundation.h>


enum SoundError {
	SOUND_ERR_ABORTED = 1,
	SOUND_ERR_NETWORK = 2,
	SOUND_ERR_DECODE = 3,
	SOUND_ERR_NONE_SUPPORTED = 4
};
typedef NSUInteger SoundError;

enum SoundStates {
	SOUND_NONE = 0,
	SOUND_STARTING = 1,
	SOUND_RUNNING = 2,
	SOUND_PAUSED = 3,
	SOUND_STOPPED = 4
};
typedef NSUInteger SoundStates;

enum SoundMsg {
	SOUND_STATE = 1,
	SOUND_DURATION = 2,
    SOUND_POSITION = 3,
	SOUND_ERROR = 9
};
typedef NSUInteger SoundMsg;

@interface AudioPlayer : AVAudioPlayer {
	NSString *mediaId;
}
@property (nonatomic,copy) NSString *mediaId;
@end

@interface DynamicAppAudioFile : NSObject {
	NSString *resourcePath;
	NSURL *resourceURL;
	AudioPlayer *player;
}

@property (nonatomic, retain) NSString *resourcePath;
@property (nonatomic, retain) NSURL *resourceURL;
@property (nonatomic, retain) AudioPlayer *player;

@end


@interface Sound : DynamicAppPlugin <AVAudioPlayerDelegate> {
    NSMutableDictionary *soundCache;
    AVAudioSession *avSession;
}


@property (nonatomic, retain) NSMutableDictionary *soundCache;
@property (nonatomic, retain) AVAudioSession *avSession;



- (void) play:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) pause:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) stop:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) release:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) getCurrentPosition:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) setCurrentPosition:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;

- (BOOL) hasAudioSession;

@end
