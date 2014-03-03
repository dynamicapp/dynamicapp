//
//  ZXPDFTextPositionState.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/14.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFFontInfo.h"
#import "ZXPDFCMap.h"


@interface ZXPDFTextPositionState : NSObject {
    
}

@property (nonatomic) CGAffineTransform CTM; // Current Transfomation Matrix
@property (nonatomic) CGAffineTransform Tm; // text matrix
@property (nonatomic) CGAffineTransform Tlm; // text line matrix
@property (nonatomic) CGFloat tx; // horizontal origin
@property (nonatomic) CGFloat ty; // vertical origin

@property (nonatomic) CGFloat Tc; // character-spacing
@property (nonatomic) CGFloat Tw; // word-spacing
@property (nonatomic) CGFloat Th; // horizontal scale (default 100)
@property (nonatomic) CGFloat Tl; // leading
@property (nonatomic, strong) NSString *Tf; // font name (key of ZXPDFFontInfo)
@property (nonatomic) CGFloat Tfs; // text font size
@property (nonatomic) NSInteger Tmode; // writing mode (unused currently)
@property (nonatomic) CGFloat Trise; // text rise
@property (nonatomic) BOOL Tk; // text knockout (unused currently)

@property (nonatomic, strong) ZXPDFFontInfo *fontInfo; // shall manually set by using Tf
@property (nonatomic, strong) ZXPDFCMap *toCIDCMap;
@property (nonatomic, strong) ZXPDFCMap *toUnicodeCMap;
@property (nonatomic) NSInteger WMode;

@property (nonatomic, readonly) BOOL isVerticalMode; // convenient for WMode
                                                     // return YES if WMode=1
                                                     // return NO otherwise.

- (id)init; // setting default values

+ (CGAffineTransform)matrixWithFontSize:(CGFloat)Tfs
                        horizontalScale:(CGFloat)Th
                               textRise:(CGFloat)Trise
                             textMatrix:(CGAffineTransform)Tm
                                    CTM:(CGAffineTransform)CTM; // make text rendering matrix.
- (CGAffineTransform)textRenderingMatrix;

+ (CGFloat)horizontalPositionWithGlyphWidth:(CGFloat)w0
                                 adjustment:(CGFloat)Tj
                                   fontSize:(CGFloat)Tfs
                           characterSpacing:(CGFloat)Tc
                                wordSpacing:(CGFloat)Tw
                            horizontalScale:(CGFloat)Th;
- (CGFloat)horizontalPositionWithGlyphWidth:(CGFloat)w0 adjustment:(CGFloat)Tj useWordSpacing:(BOOL)useTw;

+ (CGFloat)verticalPositionWithGlyphHeight:(CGFloat)w1
                                adjustment:(CGFloat)Tj
                                  fontSize:(CGFloat)Tfs
                          characterSpacing:(CGFloat)Tc
                               wordSpacing:(CGFloat)Tw;
- (CGFloat)verticalPositionWithGlyphHeight:(CGFloat)w1 adjustment:(CGFloat)Tj useWordSpacing:(BOOL)useTw;

- (void)updateTextMatrixWithTx:(CGFloat)tx andTy:(CGFloat)ty;

@end
