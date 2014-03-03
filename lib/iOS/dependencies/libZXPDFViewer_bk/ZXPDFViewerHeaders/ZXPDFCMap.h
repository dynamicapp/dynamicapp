//
//  ZXPDFCMap.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/15.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFCMapCIDMap, ZXPDFCMapCIDRangeMap, ZXPDFCMapCodespaceRange, ZXPDFCMapUnicodeMap, ZXPDFCMapUnicodeRangeMap;

@interface ZXPDFCMap : NSManagedObject {
@private
}
@property (nonatomic, strong) NSString * Ordering;
@property (nonatomic, strong) NSString * baseEncoding;
@property (nonatomic, strong) ZXPDFCMap *parentCMap;
@property (nonatomic, strong) NSString * Supplement;
@property (nonatomic, strong) NSString * Registry;
@property (nonatomic, strong) NSNumber * encodingForSimpleFont;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) NSString * useCMap;
@property (nonatomic, strong) NSNumber * wMode;
@property (nonatomic, strong) NSSet* unicodeRangeMaps;
@property (nonatomic, strong) NSSet* codespaceRanges;
@property (nonatomic, strong) NSSet* cidRangeMaps;
@property (nonatomic, strong) NSSet* unicodeMaps;
@property (nonatomic, strong) NSSet* cidMaps;

@end
