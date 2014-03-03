//
//  ZXPDFCMap+Utility.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/06.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFCMapCoreDataModels.h"
#import "ZXPDFCMapDefinition.h"


@interface ZXPDFCMap (ZXPDFCMap_Utility)

@property (nonatomic, readonly) BOOL isSimpleFontEncoding;
@property (nonatomic, readonly) NSStringEncoding stringEncoding;

- (BOOL)isParentCMapReady;
- (BOOL)codeNumberIsInRange:(NSNumber *)aCode forNumberOfBytes:(NSInteger)numberOfBytes;
- (void)setUpParentCMapWithDefinitions:(NSArray *)aDefinitionList; // aDefinitionList contains ZXPDFCMapDefinition.

- (NSNumber *)cidFromCharacterCode:(NSNumber *)aCode;
- (NSString *)stringFromCharacterCode:(NSNumber *)aCode;

@end
