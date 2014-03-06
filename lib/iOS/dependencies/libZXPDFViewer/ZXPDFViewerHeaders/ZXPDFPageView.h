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
#import "ZXPDFPageObject.h"
#import "ZXPDFDictionaryScanner.h"
#import "ZXPDFLinkAnnotation.h"
#import "ZXUtility.h"


typedef enum {
    ZXPDFPageViewUnstated = 0,
    ZXPDFPageViewLeftPageSelected,
    ZXPDFPageViewRightPageSelected,
} ZXPDFPageViewDoubleTruckState;

@class ZXPDFDocumentView;


@interface ZXPDFPageView : UIView {
    ZXPDFDocumentView *__unsafe_unretained documentView;
    BOOL shouldHandleDoubleTruck;
    UIColor *fillColorForBlank;
    ZXPDFPageObject *pageObject;
    ZXPDFDictionaryScanner *dictionaryScanner;
    NSTimeInterval storedTimestamp;
    CGPoint storedPoint;
    CGPoint storedPointInWindow;
    BOOL isDoubleTruck_;
    ZXPDFPageViewDoubleTruckState doubleTruckState;

    UIImage *imageByFrame;
    UIImage *imageByHalfScale;
    UIImage *imageByDoubleScale;
    UIImage *currentImage;

    NSArray *linkAnnotations;
    BOOL linkButtonsAdded;
}

@property (/* atomic */ unsafe_unretained) ZXPDFDocumentView *documentView;
@property (nonatomic) BOOL shouldHandleDoubleTruck;       // shadow by documentView's one if it assigned.
@property (nonatomic, strong) UIColor *fillColorForBlank; // shadow by documentView's one if it assigned.
@property (strong) ZXPDFPageObject *pageObject;
@property (nonatomic, strong) ZXPDFDictionaryScanner *dictionaryScanner;
@property (nonatomic) NSTimeInterval storedTimestamp;
@property (nonatomic) CGPoint storedPoint;
@property (nonatomic) CGPoint storedPointInWindow;
@property (nonatomic, readonly) CGRect mediaBox;
@property (nonatomic, readonly) NSInteger pageNumber;
@property (nonatomic, readonly) BOOL isDoubleTruck;
@property (nonatomic) ZXPDFPageViewDoubleTruckState doubleTruckState;

@property (nonatomic, strong) UIImage *imageByFrame;
@property (nonatomic, strong) UIImage *imageByHalfScale;
@property (nonatomic, strong) UIImage *imageByDoubleScale;
@property (nonatomic, strong) UIImage *currentImage;

@property (nonatomic, strong) NSArray *linkAnnotations;


- (id)initWithPDFPageObject:(ZXPDFPageObject *)page;
- (id)initWithPDFPageObject:(ZXPDFPageObject *)page withFrame:(CGRect)frame;
+ (id)pageViewWithPDFPageObject:(ZXPDFPageObject *)page;
+ (id)pageViewWithPDFPageObject:(ZXPDFPageObject *)page withFrame:(CGRect)frame;

// must call this before removeFromSuperview, to care for illegal access from CATiledLayer's sub threads.
- (void)purgeForRelease;

- (UIImage *)imageForRect:(CGRect)rect;
- (UIImage *)imageForRect:(CGRect)rect strokeFrame:(BOOL)toStroke;
- (UIImage *)imageByKeepingAspectRatioForRect:(CGRect)rectForFrame;
- (UIImage *)imageByDefaultSize; // fit to screen size by keeping aspect ratio.

- (BOOL)storeImageAsSharedPageCache:(UIImage *)anImage;
- (UIImage *)imageByRestoringFromSharedPageCache;
+ (NSString *)storePathForSharedPageCacheWithDocument:(ZXPDFDocumentObject *)aDocument
                                           pageNumber:(NSInteger)number
                                     doubleTruckState:(ZXPDFPageViewDoubleTruckState)state;
+ (NSString *)suffixForSharedPageCacheWithState:(ZXPDFPageViewDoubleTruckState)state;
- (NSString *)storePathForSharedPageCache;

- (void)duplicateStatusFromOtherView:(ZXPDFPageView *)view;
- (void)copyStatusFromOtherView:(ZXPDFPageView *)view DEPRECATED_ATTRIBUTE;
- (CGFloat)scaleForRect:(CGRect)rect;
- (CGFloat)scaleOfPointByPoint;
- (CGFloat)fixedScaleForRect:(CGRect)rect;
- (CGFloat)fixedScaleOfPointByPoint;
- (void)cleanupStores;

- (void)setUpLinkButtons;
- (UIButton *)linkButtonWithAnnotation:(ZXPDFLinkAnnotation *)linkAnnotation;
- (void)setAnnotationsHidden:(BOOL)isHidden;
- (void)setAnnotationsHiddenByNumber:(NSNumber *)isHiddenNumber;

- (BOOL)isEqualToPDFPageView:(ZXPDFPageView *)other;

- (CGRect)rectOfMediaBoxInCurrentFrame;
- (CGRect)rectFromPDFRect:(CGRect)rectByPDFCoodinate;


@end
