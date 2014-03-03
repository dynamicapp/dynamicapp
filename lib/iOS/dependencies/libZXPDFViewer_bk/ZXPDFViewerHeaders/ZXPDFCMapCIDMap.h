//
//  ZXPDFCMapCIDMap.h
//  ZyyxLibraries
//
//  Created by gotow on 11/05/30.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFCMap;

@interface ZXPDFCMapCIDMap : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * cid;
@property (nonatomic, strong) NSNumber * code;
@property (nonatomic, strong) NSNumber * isNotdef;
@property (nonatomic, strong) ZXPDFCMap * cMap;

@end
