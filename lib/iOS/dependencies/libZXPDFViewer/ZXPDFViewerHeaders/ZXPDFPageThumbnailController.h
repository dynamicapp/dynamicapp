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
