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
#import <CoreGraphics/CoreGraphics.h>

#import "ZXUtility.h"

@class ZXPDFPageObject;
@class ZXPDFDocumentObject;

@interface ZXPDFPageObject : NSObject {
    ZXPDFDocumentObject *__unsafe_unretained document;
    CGPDFPageRef page;
    CGRect mediaBox; // page size
    NSNumber *isLandscape;
    NSInteger pageNumber;
    ZXPDFPageObject *_previousPage;
    ZXPDFPageObject *_nextPage;

    NSNumber *isDoubleTruck_;
}

@property (nonatomic, unsafe_unretained) ZXPDFDocumentObject *document;
@property (nonatomic, readonly) CGPDFPageRef page;
@property (nonatomic, readonly) CGRect mediaBox;
@property (nonatomic, readonly) BOOL isLandscape;
@property (nonatomic, readonly) NSInteger pageNumber;

// dynamic (cusomized) property (for casual use).
// should use these commonly.
@property (nonatomic, strong) ZXPDFPageObject *previousPage;
@property (nonatomic, strong) ZXPDFPageObject *nextPage;

// low level property (for development use).
// should NOT use for casual.
@property (nonatomic, strong) ZXPDFPageObject *_previousPage;
@property (nonatomic, strong) ZXPDFPageObject *_nextPage;

@property (nonatomic, readonly) BOOL isDoubleTruck;


- (id)initWithPDFPage:(CGPDFPageRef)pageRef;
+ (id)pdfPageObjectWithPDFPage:(CGPDFPageRef)pageRef;

- (void)setPageObject:(ZXPDFPageObject *)pageObject
          ForProperty:(NSString *)propertyName
  withInverseProperty:(NSString *)inversePropertyName;

- (ZXPDFPageObject *)findPreviousPage DEPRECATED_ATTRIBUTE;
- (ZXPDFPageObject *)findNextPage DEPRECATED_ATTRIBUTE;
- (ZXPDFPageObject *)findPageForProperty:(NSString *)propertyName
                             addNumber:(NSInteger)number DEPRECATED_ATTRIBUTE;
- (CGPDFPageRef)findPageRefWithNumber:(NSInteger)number;

- (void)unlinkPreviousPageObjects;
- (void)unlinkNextPageObjects;
- (void)unlinkPageObjectsForProperty:(NSString *)propertyName
                 withInverseProperty:(NSString *)inversePropertyName;
- (void)unlinkAllLinkedPageObjects;
- (void)releaseWithAllLinkedPageObjects;

@end
