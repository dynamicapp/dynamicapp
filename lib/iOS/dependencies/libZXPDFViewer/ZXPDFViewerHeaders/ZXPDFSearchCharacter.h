//
//  ZXPDFSearchCharacter.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/21.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFSearchCharacter, ZXPDFSearchPage;

@interface ZXPDFSearchCharacter : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * index;
@property (nonatomic, strong) NSString * string;
@property (nonatomic, strong) NSString * rect;
@property (nonatomic, strong) ZXPDFSearchPage * page;
@property (nonatomic, strong) ZXPDFSearchCharacter * previousCharacter;
@property (nonatomic, strong) ZXPDFSearchCharacter * nextCharacter;

@end
