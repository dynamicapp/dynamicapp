//
//  LoadingScreen.h
//  DynamicApp
//
//  Created by ZYYX on 3/15/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

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
