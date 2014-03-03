//
//  ZXPDFFontCharacterWidthVertical.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/14.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFFontInfo;

@interface ZXPDFFontCharacterWidthVertical : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * cid;
@property (nonatomic, strong) NSArray *widthArray;
@property (nonatomic, strong) ZXPDFFontInfo * fontInfo;

@end
