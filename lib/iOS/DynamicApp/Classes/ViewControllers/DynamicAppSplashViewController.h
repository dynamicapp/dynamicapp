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

#import <UIKit/UIKit.h>

#import "DownloadConttroller.h"

@interface DynamicAppSplashViewController : UIViewController<DownloadControllerDelegate> {
    DownloadConttroller *downloadController;
    UIImageView *splashView;
    UIColor *backgroundColor;
    UIImage *defaultImage;
    UIImage *portraitImage;
    UIImage *landscapeImage;
    UIActivityIndicatorView *activityIndicatorView;
    UILabel *loadingLabel;
    NSTimeInterval delayToHiding;
}

@property (nonatomic, retain) UIImageView *splashView;
@property (nonatomic, retain) UIColor *backgroundColor;
@property (nonatomic, retain) UIImage *defaultImage;
@property (nonatomic, retain) UIImage *portraitImage;
@property (nonatomic, retain) UIImage *landscapeImage;
@property (nonatomic, retain) UIActivityIndicatorView *activityIndicatorView;
@property (nonatomic, retain) UILabel *loadingLabel;
@property (nonatomic) NSTimeInterval delayToHiding;

@property (nonatomic, retain) DownloadConttroller *downloadController;
@end
