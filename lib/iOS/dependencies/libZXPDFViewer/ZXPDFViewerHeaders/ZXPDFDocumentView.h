//
//  PDFDocumentView.h
//  ZyyxLibraries
//
//  Created by gotow on 10/03/01.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ZXPDFDocumentObject.h"
#import "ZXPDFPageObject.h"
#import "ZXPDFPageView.h"
#import "ZXPDFCatalogScanner.h"
#import "ZXPDFLocalDestination.h"
#import "ZXExternalDisplayViewController.h"
#import "ZXPDFSearchCoreDataModels.h"


@class ZXPDFDocumentView;
@protocol ZXPDFDocumentViewPageAnimation;


@protocol ZXPDFDocumentViewPageChangeDelegate <NSObject>
- (void)pdfDocumentView:(ZXPDFDocumentView *)aDocumentView willChangeToPageNumber:(NSInteger)pageNumber;
- (void)pdfDocumentView:(ZXPDFDocumentView *)aDocumentView didChangeToPageNumber:(NSInteger)pageNumber;
@end


typedef enum {
    ZXPDFDocumentViewPageDoNotMove = 0,
    ZXPDFDocumentViewPageFadeInOut,
    ZXPDFDocumentViewPageToRight,
    ZXPDFDocumentViewPageToUp,
    ZXPDFDocumentViewPageToLeft = -ZXPDFDocumentViewPageToRight,
    ZXPDFDocumentViewPageToDown = -ZXPDFDocumentViewPageToUp,
} ZXPDFDocumentViewPageDirection;

typedef enum {
    ZXPDFDocumentViewLeftToRight = ZXPDFDocumentViewPageToLeft,
    ZXPDFDocumentViewRightToLeft = ZXPDFDocumentViewPageToRight,
} ZXPDFDocumentViewPageOrder;

typedef enum {
    ZXPDFDocumentViewNonPageTag = 0,
    ZXPDFDocumentViewCurrentPageTag = 100,
    ZXPDFDocumentViewNextPageTag,
    ZXPDFDocumentViewPreviousPageTag,
    ZXPDFDocumentViewPageHolderTag,
    ZXPDFDocumentViewDummyPageTag,
    ZXPDFDocumentViewDummyOldPageTag,
    ZXPDFDocumentViewPreviousZoomedPageTag,
    ZXPDFDocumentViewMemoInPageTag,
    ZXPDFDocumentViewPointerTag,
    ZXPDFDocumentViewWordHighlightTag,
} ZXPDFDocumentViewPageTag;

extern NSString * const ZXPDFDocumentViewPageAnimationTimestampKey;
extern NSString * const ZXPDFDocumentViewPageChangeTriggerAnimationID;
extern NSString * const ZXPDFDocumentViewPageReplaceAnimationID;
extern NSString * const ZXPDFDocumentViewFakePageChangeAnimationID;



@interface ZXPDFDocumentView : UIScrollView {
    UIViewController *__unsafe_unretained viewController;
    ZXPDFDocumentObject *document;
    ZXPDFCatalogScanner *catalogScanner;

    CGFloat pageSpacingSize;
    NSMutableArray *pageViews;
    BOOL shouldHandleDoubleTruck;
    UIColor *fillColorForBlank;
    BOOL shouldUseBackgroundLoading;
    BOOL shouldUsePageCache;

    UINavigationBar *__unsafe_unretained navigationBar;
    UIToolbar *__unsafe_unretained toolbar;
    UINavigationController *__unsafe_unretained navigationController;

    id <UIScrollViewDelegate> delegateInternal;
    UIView *pageHolder;
    NSOperation *settingDummyOperation;

    id <ZXPDFDocumentViewPageAnimation> pageAnimator;
    NSMutableDictionary *animationContexts;
    NSMutableArray *animationContextStorage;

    ZXPDFDocumentViewPageOrder pageOrder;
    BOOL shouldOutputExternalDisplay;
    BOOL shouldHandleExternalDisplayPointer;
    BOOL shouldUsePageViewForExternalDisplay;
    ZXExternalDisplayViewController *__unsafe_unretained externalDisplayViewController;
    id externalViewDelegate;
}

@property (nonatomic, unsafe_unretained) UIViewController *viewController;
@property (nonatomic, strong) ZXPDFDocumentObject *document;
@property (nonatomic, strong) ZXPDFCatalogScanner *catalogScanner;

@property (nonatomic) CGFloat pageSpacingSize;
@property (unsafe_unretained, nonatomic, readonly) ZXPDFPageView *currentPageView;
@property (unsafe_unretained, nonatomic, readonly) UIImageView *dummyPageView;
@property (nonatomic, strong) NSMutableArray *pageViews;
@property (nonatomic, readonly) float pageSizedZoomScale;
@property (nonatomic) BOOL shouldHandleDoubleTruck;
@property (nonatomic, strong) UIColor *fillColorForBlank;
@property (nonatomic) BOOL shouldUseBackgroundLoading;
@property (nonatomic) BOOL shouldUsePageCache;
@property (nonatomic, unsafe_unretained) id<ZXPDFDocumentViewPageChangeDelegate> pageChangeDelegate;

@property (nonatomic, unsafe_unretained) UINavigationBar *navigationBar;
@property (nonatomic, unsafe_unretained) UIToolbar *toolbar;
@property (nonatomic, unsafe_unretained) UINavigationController *navigationController;

@property (nonatomic, strong) UIView *pageHolder;
@property (strong) NSOperation *settingDummyOperation DEPRECATED_ATTRIBUTE;

@property (nonatomic, strong) id <ZXPDFDocumentViewPageAnimation> pageAnimator;
@property (nonatomic, strong, readonly) NSMutableDictionary *animationContexts DEPRECATED_ATTRIBUTE;
@property (nonatomic, strong, readonly) NSMutableArray *animationContextStorage;

@property (nonatomic) ZXPDFDocumentViewPageOrder pageOrder;
@property (nonatomic, readonly) ZXPDFPageViewDoubleTruckState defaultState;
@property (nonatomic, readonly) ZXPDFDocumentViewPageDirection directionToLargerPage;
@property (nonatomic, readonly) ZXPDFDocumentViewPageDirection directionToSmallerPage;

@property (nonatomic) BOOL shouldOutputExternalDisplay;
@property (nonatomic) BOOL shouldHandleExternalDisplayPointer;
@property (nonatomic) BOOL shouldUsePageViewForExternalDisplay;
@property (nonatomic, unsafe_unretained) ZXExternalDisplayViewController *externalDisplayViewController;
@property (nonatomic, strong) UILongPressGestureRecognizer *externalDisplayPointerRecognizer;

@property (nonatomic, strong) ZXPDFSearchWord *searchWord;

@property () BOOL purged;


- (void)setUpDefaults;
- (void)purgeForRelease;

- (void)setContentsWithPDFDocumentObject:(ZXPDFDocumentObject *)documentObject;
- (void)setInitialPageViewWithPageNumber:(NSInteger)page
                    withDoubleTruckState:(ZXPDFPageViewDoubleTruckState)state;
- (void)changeToNextPage;
- (void)changeToPreviousPage;
- (void)changeToLeftPage;
- (void)changeToRightPage;
- (void)changeToFirstPage;
- (void)changeToFinalPage;
- (void)changeToPageNumber:(NSInteger)pageNum toLargePage:(BOOL)toLarge DEPRECATED_ATTRIBUTE;
- (void)changeToPageNumber:(NSInteger)pageNumber
               toLargePage:(BOOL)toLarge
      withDoubleTruckState:(ZXPDFPageViewDoubleTruckState)doubleTrackState;
- (void)changeToLocalDestination:(ZXPDFLocalDestination *)destination;

- (void)setPageViewWithPageNumber:(NSInteger)page
             withDoubleTruckState:(ZXPDFPageViewDoubleTruckState)state;
- (void)setPageViewWithPageNumber:(NSInteger)page
             withDoubleTruckState:(ZXPDFPageViewDoubleTruckState)state
                      toDirection:(ZXPDFDocumentViewPageDirection)direction;
- (void)adjustPageSizeToCurrentFrame;
- (void)adjustMemoDataForPageView:(ZXPDFPageView *)aPageView;
- (void)adjustWordHighlightForPageView:(ZXPDFPageView *)aPageView;
- (void)adjustExternalDisplayWithPageView:(ZXPDFPageView *)aPageView;

- (void)toggleZoomScale;
- (void)toggleZoomToRectWithPoint:(NSValue *)point;
- (void)zoomToLarger;
- (void)zoomToSmaller;
- (void)zoomToPageSize;

- (void)showToolbar;
- (void)hideToolbar;
- (void)toggleToolbarVisible;
- (BOOL)toolbarHidden;

- (void)clearAllContents;

@end



#pragma mark -
#pragma mark internal use

@interface ZXPDFDocumentViewPageStateContainer : NSObject {
    NSURL *documentURL;
    NSString *cacheStorePath;
    BOOL isDoubleTruck;

    NSInteger pageNumber;
    ZXPDFPageViewDoubleTruckState doubleTruckState;
    BOOL shouldHandleDoubleTruck;

    ZXPDFPageView *pageView;
    ZXPDFDocumentObject *documentObject;
    ZXPDFPageView *originalPageView;
}

@property (nonatomic, strong) NSURL *documentURL;
@property (nonatomic, strong) NSString *cacheStorePath;
@property (nonatomic) BOOL isDoubleTruck;

@property (nonatomic) NSInteger pageNumber;
@property (nonatomic) ZXPDFPageViewDoubleTruckState doubleTruckState;
@property (nonatomic) BOOL shouldHandleDoubleTruck;

@property (nonatomic, strong) ZXPDFPageView *pageView; // create new page view.
@property (nonatomic, strong) ZXPDFDocumentObject *documentObject; // created new object in pageView method.
@property (nonatomic, strong) ZXPDFPageView *originalPageView; // should not send message in other thread.

- (id)initWithDocument:(ZXPDFDocumentObject *)aDocument pageView:(ZXPDFPageView *)aPageView;
+ (id)containerWithDocument:(ZXPDFDocumentObject *)aDocument pageView:(ZXPDFPageView *)aPageView;

@end



@interface ZXPDFDocumentViewWordHighlightHolder : UIView {

}

@property (nonatomic, strong, readonly) NSSet *highlightRects; // PDF coordinate rect strings.

- (id)initWithPDFPageView:(ZXPDFPageView *)aPageView highlightRects:(NSSet *)aPDFRectSet;

@end
