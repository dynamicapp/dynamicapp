//
//  PDFDocumentObject.h
//  ZyyxLibraries
//
//  Created by gotow on 10/02/18.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import "ZXUtility.h"
#import "ZXPDFPageObject.h"


@interface ZXPDFDocumentObject : NSObject {
    NSURL *fileURL;
    CGPDFDocumentRef document;
    ZXPDFPageObject *currentPage;
    BOOL isDoubleTruck;

    NSString *cacheStorePath;
}

@property (nonatomic, strong, readonly) NSURL *fileURL;
@property (nonatomic, readonly) CGPDFDocumentRef document;
@property (nonatomic, strong, readonly) ZXPDFPageObject *currentPage;
@property (nonatomic, readonly) NSInteger numberOfPages;
@property (nonatomic) BOOL isDoubleTruck;

@property (nonatomic, copy) NSString *cacheStorePath;


- (id)initWithPath:(NSString *)path;
- (id)initWithURL:(NSURL *)url;

- (void)setCurrentPageWithPageNumber:(NSInteger)pageNumber;
- (ZXPDFPageObject *)pageWithPageNumber:(NSInteger)pageNumber;

- (NSString *)sharedPageCacheStorePathWithPageNumber:(NSInteger)pageNumber;
- (NSString *)sharedPageCacheStorePathForCurrentPage;
- (NSString *)pageCacheStorePathWithPageNumber:(NSInteger)pageNumber orientation:(UIInterfaceOrientation)orientation;
- (NSString *)pageCacheStorePathForCurrentPageWithOrientation:(UIInterfaceOrientation)orientaiton;
- (NSString *)memoDataStorePathWithPageNumber:(NSInteger)pageNumber;
- (NSString *)memoDataStorePathForCurrentPage;
- (NSURL *)searchIndexStoreURL;
- (NSURL *)searchResultStoreURL;
- (NSURL *)embeddedCMapStoreURL;

+ (NSString *)pageCacheStorePrefix;
+ (NSString *)sharedPageCacheStoreLastPathWithPageNumber:(NSInteger)pageNumber;
+ (NSString *)sharedPageCacheStorePathWithBasePath:(NSString *)aCacheBasePath pageNumber:(NSInteger)pageNumber;
+ (NSString *)pageCacheStoreLastPathWithPageNumber:(NSInteger)pageNumber orientation:(UIInterfaceOrientation)orientation;
+ (NSString *)memoDataStorePrefix;
+ (NSString *)memoDataStoreLastPathWithPageNumber:(NSInteger)pageNumber;
+ (NSString *)searchIndexStoreLastPathComponent;
+ (NSURL *)searchIndexStoreURLWithBasePath:(NSString *)aBasePath;
+ (NSString *)searchResultStoreLastPathComponent;
+ (NSURL *)searchResultStoreURLWithBasePath:(NSString *)aBasePath;
+ (NSString *)embeddedCMapStoreLastPathComponent;
+ (NSURL *)embeddedCMapStoreURLWithBasePath:(NSString *)aBasePath;


@end
