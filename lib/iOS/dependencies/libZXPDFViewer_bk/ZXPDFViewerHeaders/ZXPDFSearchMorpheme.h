//
//  ZXPDFSearchMorpheme.h
//  ZyyxLibraries
//
//  Created by gotow on 11/09/12.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFSearchPage;

@interface ZXPDFSearchMorpheme : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * index;
@property (nonatomic, strong) NSString * feature;
@property (nonatomic, strong) NSString * surface;
@property (nonatomic, strong) NSNumber * charType;
@property (nonatomic, strong) NSString * rect;
@property (nonatomic, strong) NSSet *rects; // contains NSString(NSStringFromCGRect)
@property (nonatomic, strong) ZXPDFSearchPage * page;

@end
