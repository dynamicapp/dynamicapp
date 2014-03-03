//
//  ExternalDisplayViewController.h
//  ZyyxLibraries
//
//  Created by gotow on 10/07/15.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface ZXExternalDisplayViewController : UIViewController {
    UIImageView *pointerView;
    UIView *viewForDisplaying;
}

@property (nonatomic, strong) IBOutlet UIImageView *pointerView;
@property (nonatomic, strong) UIView *viewForDisplaying;


- (void)adjustSizeByFrame;
- (void)adjustSizeByTransform;
- (void)adjustSizeByTransfer DEPRECATED_ATTRIBUTE;

- (void)setPointWithCGPoint:(CGPoint)point; // convert to coordinate from viewForDisplaying.
- (void)setPointWithTouch:(UITouch *)touch;
- (void)setPointWithTouch:(UITouch *)touch inView:(UIView *)baseView;


@end
