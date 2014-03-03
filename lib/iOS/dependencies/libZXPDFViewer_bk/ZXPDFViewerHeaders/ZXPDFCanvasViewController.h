//
//  ZXPDFCanvasViewController.h
//  ZyyxLibraries
//
//  Created by gotow on 10/12/20.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

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
