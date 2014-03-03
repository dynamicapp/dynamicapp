//
//  ZXPDFPageThumbnailController.h
//  ZyyxLibraries
//
//  Created by gotow on 11/04/01. (Copied from iBizPro project)
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFDocumentObject.h"
#import "ZXPDFPageObject.h"
#import "ZXPDFPageView.h"


// this is not completed implementation.
// TODO: complete this.
@interface ZXPDFPageThumbnailController : NSObject {

}

@property (nonatomic, strong) NSURL *fileURL;
@property (nonatomic, strong) NSURL *cacheURL;

+ (CGSize)thumbnailSize;
+ (NSString *)thumbnailNamePrefix;
+ (NSString *)thumbnailNameWithPageNumber:(NSInteger)pageNumber;

- (id)initWithPDFDocument:(ZXPDFDocumentObject *)aPDFDocument;
+ (id)thumbnailControllerWithPDFDocument:(ZXPDFDocumentObject *)aPDFDocument;

- (NSURL *)thumnailURLAtPageNumber:(NSInteger)pageNumber;
- (BOOL)hasThumbnailAtPageNumber:(NSInteger)pageNumber;
- (UIImage *)thumnailAtPageNumber:(NSInteger)pageNumber;
- (BOOL)createThumbnailAtPageNumber:(NSInteger)pageNumber; // heavy operation, typically should call in another thread.

@end
