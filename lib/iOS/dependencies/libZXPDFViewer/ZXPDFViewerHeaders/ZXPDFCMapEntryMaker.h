/*
 * Copyright (C) 2014 ZYYX, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
