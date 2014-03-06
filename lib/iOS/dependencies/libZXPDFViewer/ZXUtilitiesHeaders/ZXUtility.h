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
