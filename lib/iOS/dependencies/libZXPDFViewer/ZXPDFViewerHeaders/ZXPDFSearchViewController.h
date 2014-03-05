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
#import "ZXPDFDocumentObject.h"
#import "ZXPDFSearchResultController.h"
#import "ZXPDFSearchCoreDataModels.h"
#import "ZXPDFSearchViewControllerResultViewCell.h"


@class ZXPDFSearchViewController;
@class ZXPDFSearchHistroryTableController;

@protocol ZXPDFSearchViewControllerDelegate <NSObject>
- (void)searchViewController:(ZXPDFSearchViewController *)aController didSetCurrentSearchWordID:(NSManagedObjectID *)aSearchWordID_OrNil;
- (void)searchViewController:(ZXPDFSearchViewController *)aController didSelectResultPage:(ZXPDFSearchResultPage *)aResultPage;
- (void)searchViewControllerWillClearAllSearchWords:(ZXPDFSearchViewController *)aController; // should purge stored search words.
- (void)searchViewControllerWillStartSearch:(ZXPDFSearchViewController *)aController; // should purge stored search words.
@end


@interface ZXPDFSearchViewController : UIViewController
<UITableViewDataSource, UITableViewDelegate, ZXPDFSearchResultControllerDelegate, UIAlertViewDelegate,
 UISearchDisplayDelegate, UISearchBarDelegate, NSFetchedResultsControllerDelegate>
{

}

@property (nonatomic, strong) ZXPDFDocumentObject *documentObject; // for thumbnail

@property (nonatomic, strong) IBOutlet UIView *indexingView;
@property (nonatomic, strong) IBOutlet UILabel *indexingMessageLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView *indexingIndicator;
@property (nonatomic, strong) IBOutlet UIView *footerView;
@property (nonatomic, strong) IBOutlet UILabel *footerMessageLabel;
@property (nonatomic, strong) IBOutlet UIActivityIndicatorView *footerIndicator;

@property (nonatomic, strong) IBOutlet UITableView *resultTableView;
@property (nonatomic, strong) IBOutlet UIView *headerView;
@property (nonatomic, strong) IBOutlet UILabel *numberOfHitsLabel;
@property (nonatomic, strong) IBOutlet UIButton *searchHistoryButton;
@property (nonatomic, strong) IBOutlet UITableView *historyTableView;
@property (nonatomic, strong) IBOutlet UIView *historyHeader;
@property (nonatomic, strong) IBOutlet UILabel *historyLabel;
@property (nonatomic, strong) IBOutlet UIButton *historyClearButton;

@property (nonatomic, strong) IBOutlet UISearchBar *searchBar;
@property (nonatomic, strong) UISwipeGestureRecognizer *showResultRecognizer;
@property (nonatomic, strong) UISwipeGestureRecognizer *showHistoryRecognizer;

@property (nonatomic, unsafe_unretained) IBOutlet ZXPDFSearchViewControllerResultViewCell *loadedCell; // temporary use

// be created by using searchResultController
@property (nonatomic, strong, readonly) NSFetchedResultsController *fetchedResultsController;
@property (unsafe_unretained, nonatomic, readonly) NSManagedObjectContext *managedObjectContext; // shortcut of [self.fetchedResultsController managedObjectContext]
@property (nonatomic, strong, readonly) ZXPDFSearchWord *currentSearchWord;

@property (nonatomic, strong, readonly) ZXPDFSearchResultController *searchResultController; // use setUpWithSearchResultController: to set.
@property (nonatomic, unsafe_unretained) id<ZXPDFSearchViewControllerDelegate> delegate;
@property (nonatomic, unsafe_unretained) id<ZXPDFSearchResultControllerDelegate> searchResultControllerDelegate; // forwarding

@property (nonatomic, strong) ZXPDFSearchHistroryTableController *historyTableController;

@property (nonatomic, strong) UIAlertView *historyClearAlert;

// controller status (for restore when viewDidLoad)
@property (nonatomic) BOOL isResultViewMode; // YES if showing search results.
@property (nonatomic, copy) NSString *searchBarTextBackup;
@property (nonatomic, strong) NSManagedObjectID *searchWordIDBackup;

+ (CGSize)popoverSizeSmall;
+ (CGSize)popoverSizeSmallWithMessage;
+ (CGSize)popoverSizeLarge;

- (NSDictionary *)backupSettingDictionary;
- (void)restoreSettingWithDictionary:(NSDictionary *)aDictionary;

- (void)setUpWithSearchResultController:(ZXPDFSearchResultController *)aController
                             forFileURL:(NSURL *)aFileURL DEPRECATED_ATTRIBUTE;
- (void)setUpWithSearchResultController:(ZXPDFSearchResultController *)aController
                             forFileURL:(NSURL *)aFileURL
                 indexingOperationQueue:(NSOperationQueue *)aQueue;
- (void)setUpIndexingInformation:(BOOL)isReady;
- (void)setUpIndexingInformationStoppedByMemoryWarning;
- (void)setUpIndexingInfomation:(BOOL)isReady DEPRECATED_ATTRIBUTE;
- (void)setUpIndexingInfomationStoppedByMemoryWarning DEPRECATED_ATTRIBUTE;

- (void)startSearchWithString:(NSString *)aSearchString;
- (void)cancelSearch;
- (void)restartSearchForUpdatedIndex;

- (void)showResultViewAnimated:(BOOL)animated;
- (void)showHistoryViewAnimated:(BOOL)animated;

- (IBAction)historyAllClear:(id)sender;
- (IBAction)showSearchHistoryViewAction:(id)sender;

@end



#pragma mark - for internal use

@interface ZXPDFSearchHistroryTableController : NSObject <UITableViewDelegate, UITableViewDataSource, NSFetchedResultsControllerDelegate> {

}

@property (nonatomic, unsafe_unretained) ZXPDFSearchViewController *searchViewController;
@property (nonatomic, strong, readonly) NSFetchedResultsController *fetchedResultsController;


@end