//
//  ZXPDFFontCharacterRangeWidthVertical.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/14.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFFontInfo;

@interface ZXPDFFontCharacterRangeWidthVertical : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * lastCID;
@property (nonatomic, strong) NSNumber * firstCID;
@property (nonatomic, strong) NSArray *widthArray;
@property (nonatomic, strong) ZXPDFFontInfo * fontInfo;

@end
