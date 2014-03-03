//
//  Utility.h
//  ZyyxLibraries
//
//  Created by gotow on 10/02/26.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import <UIKit/UIKit.h>


@interface ZXUtility : NSObject {

}

+ (NSString *)descriptionOfCGRect:(CGRect)rect;
+ (BOOL)rectIsPortrait:(CGRect)rect;
+ (BOOL)rectIsLandscape:(CGRect)rect;
+ (CGFloat)scaleForRect:(CGRect)targetRect byRect:(CGRect)baseRect;
+ (CGRect)rectAsCenterForRect:(CGRect)targetRect inRect:(CGRect)baseRect;
+ (CGPoint)integralPoint:(CGPoint)point;
+ (CGSize)integralSize:(CGSize)size;
+ (CGRect)integralRect:(CGRect)rect;

+ (BOOL)deviceIsHighResolution;
+ (BOOL)deviceIs_iPad;

+ (void)showAlertThatFeatureIsNotAvailable;

+ (UIImage *)blankImageForRect:(CGRect)rect;
+ (UIImage *)blankImageForRect:(CGRect)rect withStroke:(BOOL)shouldStroke;

// 'dataRef' argument is required. raise NSInvalidArgumentException if it is NULL.
// keep returned NSData with returned CGContext.
+ (CGContextRef)newBlankContextForRect:(CGRect)rect intoData:(NSData **)dataRef;

+ (void)runApplicationTests; // affect only on Simulator.

@end
