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
#import "ZXCanvasViewController.h"
#import "ZXPDFDocumentObject.h"
#import "ZXPDFPageView.h"
#import "ZXPDFDocumentView.h"
#import "ZXPDFDocumentViewPageAnimation.h"
#import "ZXLoadingIndicatorView.h"

@class ZXPDFViewController;


@interface ZXPDFCanvasViewController : ZXCanvasViewController {
    UIImage *pageImage;
    UIImageView *pageImageView;
    ZXPDFDocumentObject *documentObject;
    ZXPDFDocumentViewPageOrder pageOrder;
    BOOL shouldUsePageCache;

    CGFloat baseScale;
    UIImage *initialImage;
    UIImage *imageForPortrait;
    UIImage *imageForLandscape;
    UIColor *backgroundColor;

    id<ZXPDFDocumentViewPageAnimation> pageAnimator;
    NSMutableDictionary *pageAnimationContexts;
    NSOperation *createPageCacheOperation;
    ZXLoadingIndicatorView *loadingIndicatorView;
}

@property (nonatomic, strong) UIImage *pageImage;
@property (nonatomic, strong) UIImageView *pageImageView;
@property (strong) ZXPDFDocumentObject *documentObject;
@property (nonatomic) ZXPDFDocumentViewPageOrder pageOrder;
@property (nonatomic) BOOL shouldUsePageCache;

@property (nonatomic, readonly) CGFloat baseScale;
@property (nonatomic, strong) UIImage *initialImage DEPRECATED_ATTRIBUTE;
@property (nonatomic, strong) UIImage *imageForPortrait DEPRECATED_ATTRIBUTE;
@property (nonatomic, strong) UIImage *imageForLandscape DEPRECATED_ATTRIBUTE;
@property (nonatomic, strong) UIColor *backgroundColor;
@property (nonatomic, strong) id<ZXPDFDocumentViewPageAnimation> pageAnimator;
@property (nonatomic, strong) NSMutableDictionary *pageAnimationContexts;
@property (strong) NSOperation *createPageCacheOperation;
@property (nonatomic, strong) ZXLoadingIndicatorView *loadingIndicatorView;

// alias of parentViewController if it is kind of PDFViewController.
@property (unsafe_unretained, nonatomic, readonly) ZXPDFViewController *parentPDFViewController;
@property (nonatomic, readonly) BOOL isPageChangeEnabled;

// following actions are applicable only in documentObject.isDoubleTruck=NO and shouldUsePageCache=YES situation.
- (IBAction)changeToNextPage:(id)sender;
- (IBAction)changeToPreviousPage:(id)sender;
- (IBAction)changeToLeftPage:(id)sender;
- (IBAction)changeToRightPage:(id)sender;
- (IBAction)changeToFirstPage:(id)sender;
- (IBAction)changeToFinalPage:(id)sender;
- (void)changeToPageNumber:(NSInteger)pageNumber;

- (void)storeMemoData:(NSArray *)aLineArray toFile:(NSString *)aFilePath;
- (NSMutableArray *)restoreMemoDataFromFile:(NSString *)aFilePath;
- (void)storeMemoDataForCurrentPage;
- (void)restoreMemoDataForCurrentPage;

@end
