//
//  LoadingIndicatorView.h
//  NESICMeetingSystem
//
//  Created by gotow on 11/01/13.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>


@interface ZXLoadingIndicatorView : UIView {
    UIActivityIndicatorView *indicator;
}

@property (nonatomic, strong) UIActivityIndicatorView *indicator;

+ (id)defaultView;
+ (id)defaultViewWithSize:(CGSize)size;

@end
