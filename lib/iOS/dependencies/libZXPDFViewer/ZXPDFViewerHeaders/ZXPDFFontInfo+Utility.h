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
