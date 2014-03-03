//
//  ZXPDFCMapCodespaceRange.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/06.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFCMap;

@interface ZXPDFCMapCodespaceRange : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * tailOfCode;
@property (nonatomic, strong) NSNumber * headOfCode;
@property (nonatomic, strong) NSNumber * numberOfBytes;
@property (nonatomic, strong) ZXPDFCMap * cMap;

@end
