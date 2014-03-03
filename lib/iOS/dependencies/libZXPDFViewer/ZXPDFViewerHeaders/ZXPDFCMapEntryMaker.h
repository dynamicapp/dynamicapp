//
//  ZXPDFCMapEntryMaker.h
//  ZyyxLibraries
//
//  Created by gotow on 11/05/23.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFCMapParser.h"
#import "ZXPDFCMap.h"
#import "ZXPDFCMapCIDMap.h"
#import "ZXPDFCMapCIDRangeMap.h"
#import "ZXPDFCMapUnicodeMap.h"
#import "ZXPDFCMapUnicodeRangeMap.h"
#import "ZXPDFCMapCodespaceRange.h"


@class ZXPDFCMapEntryMaker;


@protocol ZXPDFCMapEntryMakerDelegate <NSObject>
- (void)cMapEntryMakerDidFinishProcess:(ZXPDFCMapEntryMaker *)aMaker;
- (void)cMapEntryMakerDidFailProcess:(ZXPDFCMapEntryMaker *)aMaker withError:(NSError *)anError;
@end


@interface ZXPDFCMapEntryMaker : NSObject <ZXPDFCMapParserDelegate> {

}

@property (nonatomic, strong) ZXPDFCMapParser *parser;
@property (nonatomic, strong, readonly) NSString *cMapName;
@property (nonatomic, strong, readonly) NSDictionary *cidSystemInfo;
@property (nonatomic, strong, readonly) NSString *parentCMapName;
@property (nonatomic, strong) NSManagedObjectContext *managedObjectContext;
@property (nonatomic, unsafe_unretained) id<ZXPDFCMapEntryMakerDelegate> delegate;

- (id)initWithPDFCMapParser:(ZXPDFCMapParser *)aParser managedObjectContext:(NSManagedObjectContext *)aContext;

- (void)startProcess;

@end
