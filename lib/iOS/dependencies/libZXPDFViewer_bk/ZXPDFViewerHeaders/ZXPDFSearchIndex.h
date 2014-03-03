//
//  ZXPDFSearchIndex.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/09.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXCoreDataRepresentationBase.h"
#import "ZXPDFSearchDocument.h"


@interface ZXPDFSearchIndex : ZXCoreDataRepresentationBase {

}

+ (id)searchIndexWithPersistentStoreURL:(NSURL *)aPersistentStoreURL;
+ (id)searchIndexWithPersistentStoreCoordinator:(NSPersistentStoreCoordinator *)aCoordinator;

- (NSFetchRequest *)requestForCompletedDocumentWithPath:(NSString *)aFilePath; // 'filePath'=aFilePath AND 'completed'=YES.
- (BOOL)searchDocumentIsAvailableWithPath:(NSString *)aFilePath; // count by using requestForCompletedDocumentWithPath:
- (ZXPDFSearchDocument *)searchDocumentWithPath:(NSString *)aFilePath; // fetch by using requestForCompletedDocumentWithPath:

@end
