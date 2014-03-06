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
