//
//  ZXPDFSearchPage.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/21.
//  Copyright (c) 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFSearchCharacter, ZXPDFSearchDocument, ZXPDFSearchMorpheme;

@interface ZXPDFSearchPage : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * number;
@property (nonatomic, strong) NSNumber * completed;
@property (nonatomic, strong) ZXPDFSearchDocument * document;
@property (nonatomic, strong) NSSet* morphemes;
@property (nonatomic, strong) NSSet* characters;

@end
