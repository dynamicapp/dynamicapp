//
//  ZXPDFCMapToUnicodeStreamParser.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/05.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFCMapStreamParser.h"


@interface ZXPDFCMapToUnicodeStreamParser : ZXPDFCMapStreamParser {
    
}

@property (nonatomic, copy) NSString *cMapName;

// NOTICE: MUST keep alive parent CGPDFDocumentRef and CGPDFContentStreamRef object while parsing,
// because CGPDFStreamRef is NOT retainable.
- (id)initWithPDFStream:(CGPDFStreamRef)aCMapStream forName:(NSString *)aCMapName;
+ (id)parserWithPDFStream:(CGPDFStreamRef)aCMapStream forName:(NSString *)aCMapName;

@end
