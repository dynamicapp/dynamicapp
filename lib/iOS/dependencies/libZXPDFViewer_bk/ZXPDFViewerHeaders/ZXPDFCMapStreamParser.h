//
//  ZXPDFCMapStreamParser.h
//  ZyyxLibraries
//
//  Created by gotow on 11/05/23.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFCMapParser.h"


extern NSString * const ZXPDFCMapStreamParserError;
extern const NSInteger ZXPDFCMapStreamParserErrorCodeInvalidFormatStream;


@interface ZXPDFCMapStreamParser : ZXPDFCMapParser {

}

@property (nonatomic) CGPDFStreamRef stream; // is NOT retained property.

// NOTICE: MUST keep alive parent CGPDFDocumentRef and CGPDFContentStreamRef object while parsing,
// because CGPDFStreamRef is NOT retainable.
- (id)initWithPDFStream:(CGPDFStreamRef)aCMapStream;
+ (id)parserWithPDFStream:(CGPDFStreamRef)aCMapStream;

- (void)processAndInvokeGetUseCMapDelegate;

// Utilities
+ (NSString *)getCMapNameFromPDFStream:(CGPDFStreamRef)aCMapStream;
+ (NSString *)getUseCMapNameFromPDFStream:(CGPDFStreamRef)aCMapStream;
+ (CGPDFStreamRef)getUseCMapStreamFromPDFStream:(CGPDFStreamRef)aCMapStream;

@end
