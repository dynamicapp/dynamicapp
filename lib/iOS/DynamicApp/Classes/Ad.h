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
#import <iAd/iAd.h>

enum IAdPosition {
	POSITION_TOP = 0,
	POSITION_BOTTOM = 1
};
typedef NSInteger IAdPosition;

@interface Ad : DynamicAppPlugin <ADBannerViewDelegate>

@property (nonatomic, retain) ADBannerView *adBannerView;
@property (nonatomic) IAdPosition position;

- (void)create:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void)show:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void)hide:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;

@end
