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

@interface LoadingScreen : DynamicAppPlugin {
    UIView *loadingView;
    UIActivityIndicatorView *activityIndicator;
    UILabel *loadingLabel;
}

@property (nonatomic, retain) UIView *loadingView;
@property (nonatomic, retain) UIActivityIndicatorView *activityIndicator;
@property (nonatomic, retain) UILabel *loadingLabel;

- (void)startLoad:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void)stopLoad:(NSDictionary *)arguments withOptions:(NSDictionary *)options;

@end
