//
//  ZXPDFFontInfo+Utility.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/13.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFFontCoreDataModels.h"


@interface ZXPDFFontInfo (ZXPDFFontInfo_Utility)

@property (nonatomic, readonly) CGFloat ascentValue;
@property (nonatomic, readonly) CGFloat descentValue;
@property (nonatomic, readonly) CGFloat leadingValue;
@property (nonatomic, readonly) NSNumber *coordinatedDefaultWidth;
@property (nonatomic, readonly) NSArray *coordinatedDefaultWidthVertical;

- (CGRect)rectForCID:(NSNumber *)aCID forVertical:(BOOL)forVertical;


- (ZXPDFFontCharacterWidth *)widthForCID:(NSNumber *)aCID;
- (ZXPDFFontCharacterRangeWidth *)rangeWidthForCID:(NSNumber *)aCID;
- (ZXPDFFontCharacterWidthVertical *)verticalWidthForCID:(NSNumber *)aCID;
- (ZXPDFFontCharacterRangeWidthVertical *)verticalRangeWidthForCID:(NSNumber *)aCID;

- (NSNumber *)w0ForCID:(NSNumber *)aCID;
- (NSArray *)widthArrayForCID:(NSNumber *)aCID;

- (CGFloat)heightOfWidthArray:(NSArray *)aWidthArray; // return w1 of following format array.
                                                      // [ ? , w1 ] (default width vertical)
                                                      // [ w1, ? , ? ] (widthArray of character (range) width vertical)
- (CGFloat)widthOfWidthArray:(NSArray *)aWidthArray; // return default width value if array count is 2 (maybe incorrect)
                                                     // return x2 of 2nd number of array if array count is 3

@end
