//
//  ZXPDFSearchResult.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/24.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXCoreDataRepresentationBase.h"
#import "ZXPDFSearchCoreDataModels.h"


@interface ZXPDFSearchResult : ZXCoreDataRepresentationBase {
    
}

+ (id)searchResultWithPersistentStoreURL:(NSURL *)aPersistentStoreURL;
+ (id)searchResultWithPersistentStoreCoordinator:(NSPersistentStoreCoordinator *)aCoordinator;

- (NSFetchRequest *)requestForSearchWordWithString:(NSString *)aSearchWordString;
- (ZXPDFSearchWord *)searchWordWithString:(NSString *)aSearchWordString;
- (ZXPDFSearchWord *)latestSearchWord;

- (NSFetchRequest *)requestForSearchResultDocumentWithFilePath:(NSString *)aFilePath;
- (ZXPDFSearchResultDocument *)searchResultDocumentWithFilePath:(NSString *)aFilePath;

@end
