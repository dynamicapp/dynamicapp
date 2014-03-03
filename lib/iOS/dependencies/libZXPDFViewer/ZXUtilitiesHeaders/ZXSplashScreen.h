//
//  SplashScreen.h
//  GenericContentFrame
//
//  Created by gotow on 10/05/27.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

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
