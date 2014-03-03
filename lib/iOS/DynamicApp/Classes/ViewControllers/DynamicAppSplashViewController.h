//
//  DynamicAppSplashViewController.h
//  DynamicApp
//
//  Created by ZYYX on 12/02/13.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

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
