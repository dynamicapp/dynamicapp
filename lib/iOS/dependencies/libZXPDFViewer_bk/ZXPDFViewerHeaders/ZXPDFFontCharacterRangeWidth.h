//
//  ZXPDFFontCharacterRangeWidth.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/14.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFFontInfo;

@interface ZXPDFFontCharacterRangeWidth : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * lastCID;
@property (nonatomic, strong) NSNumber * firstCID;
@property (nonatomic, strong) NSNumber * width;
@property (nonatomic, strong) ZXPDFFontInfo * fontInfo;

@end
