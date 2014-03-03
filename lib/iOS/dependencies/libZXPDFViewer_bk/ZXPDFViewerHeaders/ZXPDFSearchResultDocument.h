//
//  ZXPDFSearchResultDocument.h
//  ZyyxLibraries
//
//  Created by gotow on 11/08/29.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFSearchResultPage, ZXPDFSearchWord;

@interface ZXPDFSearchResultDocument : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * indexed;
@property (nonatomic, strong) NSString * filePath;
@property (nonatomic, strong) NSNumber * completed;
@property (nonatomic, strong) NSSet* resultPages;
@property (nonatomic, strong) ZXPDFSearchWord * word;

@end
