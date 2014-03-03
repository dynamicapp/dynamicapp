//
//  ZXPDFSearchResultController.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/25.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFSearchIndex.h"
#import "ZXPDFSearchResult.h"
#import "ZXPDFSearchCoreDataModels.h"
#import "ZXMecabParser.h"
#import "ZXPDFSearchIndexMaker.h"


@class ZXPDFSearchResultController;

@protocol ZXPDFSearchResultControllerDelegate <NSObject>
@required
- (void)searchResultControllerDidStart:(ZXPDFSearchResultController *)aController
                      withSearchWordID:(NSManagedObjectID *)aSearchWordID; // ID of ZXPDFSearchWord object.
- (void)searchResultControllerDidFinish:(ZXPDFSearchResultController *)aController;
- (void)searchResultControllerDidCancel:(ZXPDFSearchResultController *)aController;
- (void)searchResultControllerDidFinishIndexing:(ZXPDFSearchResultController *)aController;
@end


@interface ZXPDFSearchResultController : NSObject <ZXMecabParserDelegate, ZXPDFSearchIndexMakerDelegate> {

}

@property (nonatomic, strong, readonly) ZXPDFSearchIndex *searchIndex;
@property (nonatomic, strong, readonly) ZXPDFSearchResult *searchResult;
@property (nonatomic, strong, readonly) ZXPDFSearchWord *currentSearchWord;
@property (nonatomic, strong, readonly) ZXPDFSearchIndexMaker *searchIndexMaker;
@property (nonatomic, strong, readonly) NSString *objectiveFilePath; // set by setUpIndexingWithFileURL:
@property (readonly) BOOL nowSearching;
@property (readonly) BOOL nowIndexing;

// delegate methods will invoke in main thread.
@property (unsafe_unretained) id<ZXPDFSearchResultControllerDelegate> delegate;
@property (unsafe_unretained) id<ZXPDFSearchIndexMakerDelegate> searchIndexMakerDelegate; // forwarding target.
@property (nonatomic) BOOL shouldUpdateResultsWhenIndexed; // default is NO.
                                                           // if YES, re-search by current word
                                                           // when invokeIndexMakerExtractPageDelegateWithNumber: invoked.
@property (nonatomic) BOOL shouldUseMorphemeIndex; // default is NO.
                                                   // if YES, search by morpheme index and create it. (CURRENTLY NOT SUPPORTED)

- (id)initWithSearchIndex:(ZXPDFSearchIndex *)aSearchIndex searchResult:(ZXPDFSearchResult *)aSearchResult;

- (BOOL)setUpIndexingWithFileURL:(NSURL *)aFileURL; // startProcess of index maker if needed.
                                                    // should set searchIndexMakerDelegate before this call.
                                                    // return YES if did start process or already running.
- (BOOL)setUpIndexingWithFileURL:(NSURL *)aFileURL onOperationQueue:(NSOperationQueue *)aQueue; // using operation queue.
- (void)tearDownIndexing; // cancelProcess of index maker if now processing.

- (void)startSearchForWord:(NSString *)aSearchWordString; // if re-start before finish,
                                                          // recycle current process without finish/cancel delegation.
- (void)cancelSearch;

@end
