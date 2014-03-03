//
//  ZXPDFSearchResultPage+Utility.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/24.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFSearchResultPage.h"


@interface ZXPDFSearchResultPage (ZXPDFSearchResultPage_Utility)

@property (nonatomic) NSInteger numberOfHitsInteger;

- (void)addMorphemeRect:(NSString *)aRectString; // set new (appended) 'morphemeRects'.
- (void)addMorphemeRectsWithSet:(NSSet *)aRectSet;

@end
