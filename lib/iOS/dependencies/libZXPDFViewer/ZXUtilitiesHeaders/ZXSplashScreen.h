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
#import <UIKit/UIKit.h>


typedef enum {
    ZXSplashScreenHidingFadeOut = 0, // default
    ZXSplashScreenHidingCurlUp,
} ZXSplashScreenHidingStyle;


@interface ZXSplashScreen : NSObject {
    UIImage *image;
    UIWindow *__unsafe_unretained window;
    ZXSplashScreenHidingStyle hidingStyle;
    NSTimeInterval delayToHiding;
    NSTimeInterval durationForHiding;
}

@property (nonatomic, strong, readonly) UIImage *image;
@property (nonatomic, unsafe_unretained, readonly) UIWindow *window;

@property (nonatomic) ZXSplashScreenHidingStyle hidingStyle;
@property (nonatomic) NSTimeInterval delayToHiding;
@property (nonatomic) NSTimeInterval durationForHiding;

- (id)initWithImage:(UIImage *)splashImage withWindow:(UIWindow *)targetWindow;
- (id)initWithImagePath:(NSString *)imagePath withWindow:(UIWindow *)targetWindow;
+ (id)splashWithImagePath:(NSString *)imagePath withWindow:(UIWindow *)targetWindow;

- (void)show;
- (void)showWithOrientation:(UIInterfaceOrientation)orientation;

+ (NSTimeInterval)defaultDelayToHidingForStyle:(ZXSplashScreenHidingStyle)style;
+ (NSTimeInterval)defaultDutationForHidingForStyle:(ZXSplashScreenHidingStyle)style;

@end
