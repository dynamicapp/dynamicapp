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
#import "ZXPDFDocumentView.h"
#import "ZXPDFDocumentObject.h"
#import "ZXPDFPageView.h"
#import "ZXPDFPageObject.h"
#import "ZXPDFCanvasViewController.h"
#import "ZXPDFPageIndicatorViewController.h"
#import "ZXPDFSearchResultController.h"
#import "ZXPDFSearchViewController.h"


// keys for configurations.                                   // expected value.
// required always.
extern NSString * const ZXPDFViewControllerFilePathKey;         // NSString (absolute file path)
// required when needs to save and restore state.
extern NSString * const ZXPDFViewControllerDataSourceKey;       // id (data source it compliant with KVC)
extern NSString * const ZXPDFViewControllerPageNumberKey;       // NSString (KVC key using read&write for data source)
extern NSString * const ZXPDFViewControllerDoubleTruckStateKey; // NSString (KVC key using read&write for data source)
extern NSString * const ZXPDFViewControllerToolbarHiddenKey;    // NSString (KVC key using read&write for data source)
// required when needs back button.
extern NSString * const ZXPDFViewControllerBackButtonTitleKey;  // NSString
extern NSString * const ZXPDFViewControllerBackButtonTargetKey; // id
extern NSString * const ZXPDFViewControllerBackButtonActionKey; // NSString (using NSStringFromSelector)
// optional, default is ZXPDFDocumentViewRightToLeft (see -[PDFDocumentView setUpDefaults])
extern NSString * const ZXPDFViewControllerPageOrderKey;        // NSNumber (PDFDocumentViewPageOrder)
extern NSString * const ZXPDFViewControllerExternalViewControllerKey; // id (ExternalDisplayViewController or has same interface)
extern NSString * const ZXPDFViewControllerUseHighQualityModeForExternalViewKey; // NSNumber (BOOL)
extern NSString * const ZXPDFViewControllerDocumentIsDoubleTruckKey; // NSNumber (BOOL)
extern NSString * const ZXPDFViewControllerMemoEnableKey;       // NSNumber (BOOL)
extern NSString * const ZXPDFViewControllerCacheStorePathKey;   // NSString (directory to store any caches)
extern NSString * const ZXPDFViewControllerBackgroundLoadingEnableKey; // NSNumber (BOOL)
extern NSString * const ZXPDFViewControllerPageCacheEnableKey;  // NSNumber (BOOL)
extern NSString * const ZXPDFViewControllerSearchIndexingAutoStartKey; // NSNumber (BOOL)
extern NSString * const ZXPDFViewControllerPageAnimatorClassNameKey; // NSString (class name that conforms ZXPDFDocumentViewPageAnimation)


// keys for archive
extern NSString * const ZXPDFViewControllerArchiveMemoDataKey;



@class ZXPDFViewController;

@protocol ZXPDFViewControllerDelegate <NSObject>
- (void)pdfViewController:(ZXPDFViewController *)aController willChangeToPage:(NSInteger)pageNumber;
- (void)pdfViewController:(ZXPDFViewController *)aController didChangeToPage:(NSInteger)pageNumber;
@end


@interface ZXPDFViewController : UIViewController
<UIPopoverControllerDelegate, ZXPDFPageIndicatorViewControllerDelegate, ZXPDFDocumentViewPageChangeDelegate,
 ZXPDFSearchIndexMakerDelegate, ZXPDFSearchResultControllerDelegate, ZXPDFSearchViewControllerDelegate>
{
    ZXPDFDocumentObject *documentObject;
    ZXPDFDocumentView *documentView;
    UINavigationBar *navigationBar;
    UIToolbar *toolbar;

    NSDictionary *configurations;
    NSDictionary *backButtonOptions;
    NSString *filePath;
    NSInteger pageNumberForInitalize;
    ZXPDFPageViewDoubleTruckState doubleTruckStateForInitialize;

    id <UINavigationBarDelegate> navigationItemDelegator;
    id popoverController;

    NSOperationQueue *creatingCacheQueue;

    ZXPDFPageIndicatorViewController *pageIndicatorViewController;
    UIView *pageIndicatorBaseView;
}

@property (nonatomic, strong, readonly) ZXPDFDocumentObject *documentObject;
@property (nonatomic, strong) IBOutlet ZXPDFDocumentView *documentView;
@property (nonatomic, strong) IBOutlet UINavigationBar *navigationBar;
@property (nonatomic, strong) IBOutlet UIToolbar *toolbar;
@property (strong) NSDictionary *configurations;
@property (nonatomic, strong) NSDictionary *backButtonOptions;
@property (nonatomic, strong) NSString *filePath DEPRECATED_ATTRIBUTE;
@property (nonatomic) NSInteger pageNumberForInitalize;
@property (nonatomic) ZXPDFPageViewDoubleTruckState doubleTruckStateForInitialize;

@property (nonatomic, strong) UIPopoverController *popoverController;

@property (strong) NSOperationQueue *creatingCacheQueue;
@property () BOOL shouldContinueCreatingCacheChain;
@property (strong) NSOperation *settingDummyOperation;

@property (nonatomic, strong) ZXPDFPageIndicatorViewController *pageIndicatorViewController;
@property (nonatomic, strong) UIView *pageIndicatorBaseView;

@property (nonatomic, strong) UIPopoverController *searchPopoverController;
@property (nonatomic, strong) ZXPDFSearchResultController *searchResultController;

@property (nonatomic, strong) ZXPDFSearchResult *searchResult;
@property (nonatomic, strong) ZXPDFSearchWord *searchWord;
@property (nonatomic, strong) ZXPDFSearchIndex *searchIndex;
@property (nonatomic, strong) NSDictionary *searchViewControllerSetting;

@property (nonatomic, unsafe_unretained) id<ZXPDFViewControllerDelegate> delegate;


- (void)setUpWithConfigurations:(NSDictionary *)configurations;
- (void)storeCurrentStatusUsingConfigurations;

- (IBAction)changeToNextPage:(id)sender;
- (IBAction)changeToPreviousPage:(id)sender;
- (IBAction)changeToLeftPage:(id)sender;
- (IBAction)changeToRightPage:(id)sender;
- (IBAction)changeToFirstPage:(id)sender;
- (IBAction)changeToFinalPage:(id)sender;
- (IBAction)toggleZoomScale:(id)sender;
- (void)changeToPageNumber:(NSInteger)pageNumber withPageDirection:(ZXPDFDocumentViewPageDirection)direction;

- (IBAction)presentPageListViewController:(UIBarButtonItem *)sender;
- (IBAction)presentCanvasViewController:(id)sender;
- (IBAction)presentPageIndicatorViewController:(id)sender; // only for iPad.
- (IBAction)presentSearchViewController:(id)sender;

+ (void)storeMemoData:(NSArray *)aLineArray toFile:(NSString *)aFilePath;
+ (NSMutableArray *)restoreMemoDataFromFile:(NSString *)aFilePath;
- (void)storeMemoData:(NSArray *)aLineArray toFile:(NSString *)aFilePath;
- (NSMutableArray *)restoreMemoDataFromFile:(NSString *)aFilePath;

// create caches by looking ahead.
// not applicable when documentObject.isDoubleTruck=YES
- (void)startCreatingCacheChainFromNextPage;
- (void)stopCreatingCacheChain; // not release creatingCacheQueue.
- (void)stopCreatingCacheChainFully; // release creatingCacheQueue.
- (void)startCreatingCacheChainFromPageNumber:(NSNumber *)aPageNumber;
- (BOOL)createSharedPageCacheWithPageNumber:(NSNumber *)aPageNumber; // heavy operation.

@end
