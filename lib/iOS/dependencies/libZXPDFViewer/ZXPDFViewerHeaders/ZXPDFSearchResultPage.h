//
//  ZXPDFSearchResultPage.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/26.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFSearchResultDocument, ZXPDFSearchWord;

@interface ZXPDFSearchResultPage : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * pageNumber;
@property (nonatomic, strong) NSNumber * numberOfHits;
@property (nonatomic, strong) NSSet *morphemeRects; // NSSet(NSStringFromCGRect())
@property (nonatomic, strong) ZXPDFSearchWord * word;
@property (nonatomic, strong) ZXPDFSearchResultDocument * resultDocument;

@end
