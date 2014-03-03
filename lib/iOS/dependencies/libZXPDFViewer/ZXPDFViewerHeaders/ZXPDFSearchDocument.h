//
//  ZXPDFSearchDocument.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/17.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>


@interface ZXPDFSearchDocument : NSManagedObject {
@private
}
@property (nonatomic, strong) NSString * filePath;
@property (nonatomic, strong) NSNumber * completed;
@property (nonatomic, strong) NSSet* pages;

@end
