//
//  ZXCanvasLineWidthViewController.h
//  ZyyxLibraries
//
//  Created by gotow on 10/12/24.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "ZXSliderViewController.h"


@class ZXCanvasLineWidthView;
@class ZXCanvasLineWidthInnerView;


@interface ZXCanvasLineWidthViewController : ZXSliderViewController {
    CGFloat scale;
    ZXCanvasLineWidthView *lineWidthView;
}

@property (nonatomic) CGFloat scale;
@property (nonatomic) CGFloat width; // alias of self.value
@property (nonatomic, strong) IBOutlet ZXCanvasLineWidthView *lineWidthView;
@property (nonatomic, strong) UIColor *lineColor;

@end



@interface ZXCanvasLineWidthView : UIView {
    ZXCanvasLineWidthInnerView *widthView;
}

@property (nonatomic, strong) ZXCanvasLineWidthInnerView *widthView;

- (void)adjustWidthViewSizeWithWidth:(CGFloat)width scale:(CGFloat)scale;

@end



@interface ZXCanvasLineWidthInnerView : UIView {
    UIColor *fillColor;
    UIColor *strokeColor;
}

@property (nonatomic, strong) UIColor *fillColor;
@property (nonatomic, strong) UIColor *strokeColor;

@end
