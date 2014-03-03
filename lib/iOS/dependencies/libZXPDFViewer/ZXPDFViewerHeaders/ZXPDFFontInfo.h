//
//  ZXPDFFontInfo.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/13.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFFontCharacterRangeWidth, ZXPDFFontCharacterRangeWidthVertical, ZXPDFFontCharacterWidth, ZXPDFFontCharacterWidthVertical;

@interface ZXPDFFontInfo : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * ascent;
@property (nonatomic, strong) NSArray *defaultWidthVertical;
@property (nonatomic, strong) NSNumber * leading;
@property (nonatomic, strong) NSNumber * descent;
@property (nonatomic, strong) NSNumber * defaultWidth;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) NSSet* widths;
@property (nonatomic, strong) NSSet* widthsVertical;
@property (nonatomic, strong) NSSet* rangeWidthsVertical;
@property (nonatomic, strong) NSSet* rangeWidths;

@end
