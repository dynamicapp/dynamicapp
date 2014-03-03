//
//  ZXPDFSearchWord.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/29.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFSearchResultDocument, ZXPDFSearchResultPage;

@interface ZXPDFSearchWord : NSManagedObject {
@private
}
@property (nonatomic, strong) NSArray *mecabNodes;
@property (nonatomic, strong) NSNumber * maxHits;
@property (nonatomic, strong) NSDate * timestamp;
@property (nonatomic, strong) NSNumber * completed;
@property (nonatomic, strong) NSString * word;
@property (nonatomic, strong) NSSet* resultPages;
@property (nonatomic, strong) NSSet* resultDocuments;

@end
