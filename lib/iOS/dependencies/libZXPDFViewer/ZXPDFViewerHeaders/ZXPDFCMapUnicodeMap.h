//
//  ZXPDFCMapUnicodeMap.h
//  ZyyxLibraries
//
//  Created by gotow on 11/05/30.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFCMap;

@interface ZXPDFCMapUnicodeMap : NSManagedObject {
@private
}
@property (nonatomic, strong) NSString * unicode;
@property (nonatomic, strong) NSNumber * code;
@property (nonatomic, strong) ZXPDFCMap * cMap;

@end
