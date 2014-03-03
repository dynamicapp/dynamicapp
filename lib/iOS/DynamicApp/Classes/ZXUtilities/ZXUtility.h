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

+ (BOOL)deviceIsHighResolution;
+ (BOOL)deviceIs_iPad;

+ (void)showAlertThatFeatureIsNotAvailable;

+ (UIImage *)blankImageForRect:(CGRect)rect;
+ (UIImage *)blankImageForRect:(CGRect)rect withStroke:(BOOL)shouldStroke;
+ (CGContextRef)newBlankContextForRect:(CGRect)rect intoData:(NSData **)dataRef;

+ (void)runApplicationTests; // affect only on Simulator.

+ (CGFloat)deviceOSVersion;

@end
