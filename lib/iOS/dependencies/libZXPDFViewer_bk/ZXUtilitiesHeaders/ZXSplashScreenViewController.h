//
//  SplashScreenViewController.h
//  ZyyxLibraries
//
//  Created by gotow on 10/10/13.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface ZXSplashScreenViewController : UIViewController {
    UIImageView *splashView;
    UIColor *backgroundColor;
    UIImage *defaultImage;
    UIImage *portraitImage;
    UIImage *landscapeImage;

    NSTimeInterval delayToHiding;
}

@property (nonatomic, strong) UIImageView *splashView;
@property (nonatomic, strong) UIColor *backgroundColor;
@property (nonatomic, strong) UIImage *defaultImage;
@property (nonatomic, strong) UIImage *portraitImage;
@property (nonatomic, strong) UIImage *landscapeImage;
@property (nonatomic) NSTimeInterval delayToHiding;

- (void)showForParentViewController:(UIViewController *)aParentViewController;
- (void)adjustFrameWithInterfaceOrientation:(UIInterfaceOrientation)anOrientation;

@end
