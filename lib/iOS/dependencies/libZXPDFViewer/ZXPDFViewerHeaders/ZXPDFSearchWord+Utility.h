//
//  ZXPDFSearchWord+Utility.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/24.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFSearchWord.h"


@interface ZXPDFSearchWord (ZXPDFSearchWord_Utility)

@property (nonatomic) BOOL isCompleted;
@property (nonatomic) NSInteger maxHitsInteger;

- (ZXPDFSearchResultDocument *)resultDocumentWithFileURL:(NSURL *)aFileURL;
- (ZXPDFSearchResultDocument *)resultDocumentWithNormalizedFilePath:(NSString *)aNormalizedPath;

- (ZXPDFSearchResultPage *)resultPageWithFileURL:(NSURL *)aFileURL
                                      pageNumber:(NSNumber *)aPageNumber;

+ (ZXPDFSearchWord *)latestSearchWordInContext:(NSManagedObjectContext *)aContext completedOnly:(BOOL)completedOnly;

+ (void)clearAllSearchWordsWithContext:(NSManagedObjectContext *)aContext;

@end
