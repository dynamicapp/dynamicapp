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
