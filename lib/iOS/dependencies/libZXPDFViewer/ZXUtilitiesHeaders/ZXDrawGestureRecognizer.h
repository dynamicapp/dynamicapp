//
//  ZXDrawGestureRecognizer.h
//  ZyyxLibraries
//
//  Created by gotow on 11/02/15.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


@interface ZXDrawGestureRecognizer : UIGestureRecognizer {
    NSValue *firstLocationValue;
    UITouch *firstTouch;
}

@property (nonatomic, strong) NSValue *firstLocationValue;
@property (nonatomic, strong) UITouch *firstTouch;

- (CGPoint)firstLocationInView:(UIView *)aView;

@end
