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
