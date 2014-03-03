//
//  ZXPDFFontParser.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/11.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFCMapDefinition.h"


extern NSString * const ZXPDFFontParserError;
extern const NSInteger ZXPDFFontParserErrorCodeInvalidFormat;
extern const NSInteger ZXPDFFontParserErrorCodeUnknown;


@class ZXPDFFontParser;

@protocol ZXPDFFontParserDelegate <NSObject>
- (void)fontParser:(ZXPDFFontParser *)aParser didGetAscent:(NSNumber *)anAscentNumber; // CGPDFReal
- (void)fontParser:(ZXPDFFontParser *)aParser didGetDescent:(NSNumber *)aDescentNumber; // CGPDFReal
- (void)fontParser:(ZXPDFFontParser *)aParser didGetLeading:(NSNumber *)aLeadingNumber; // CGPDFReal

- (void)fontParser:(ZXPDFFontParser *)aParser didGetDefaultWidth:(NSNumber *)aDefaultWidth; // CGPDFInteger
- (void)fontParser:(ZXPDFFontParser *)aParser
       didGetWidth:(NSNumber *)aWidth
            forCID:(NSNumber *)aCID;
- (void)  fontParser:(ZXPDFFontParser *)aParser
         didGetWidth:(NSNumber *)aWidth
    forCIDRangeFirst:(NSNumber *)aFirstCID
           rangeLast:(NSNumber *)aLastCID;

- (void)fontParserDidFinish:(ZXPDFFontParser *)aParser;
- (void)fontParserDidFail:(ZXPDFFontParser *)aParser withError:(NSError *)anError;

@optional
// for vertical setting font.
- (void)fontParser:(ZXPDFFontParser *)aParser didGetDefaultWidthVertical:(NSArray *)aDefaultWidthArray;
- (void)     fontParser:(ZXPDFFontParser *)aParser
    didGetWidthVertical:(NSArray *)aWidthArray
                 forCID:(NSNumber *)aCID;
- (void)     fontParser:(ZXPDFFontParser *)aParser
    didGetWidthVertical:(NSArray *)aWidthArray
       forCIDRangeFirst:(NSNumber *)aFirstCID
              rangeLast:(NSNumber *)aLastCID;
@end


@interface ZXPDFFontParser : NSObject {
    
}

@property (nonatomic) CGPDFDictionaryRef fontDictionary;

@property (nonatomic, unsafe_unretained) id<ZXPDFFontParserDelegate> delegate;

// NOTICE: MUST keep alive parent CGPDFDocumentRef and CGPDFContentStreamRef object while parsing,
// because CGPDFDictionaryRef is NOT retainable.
- (id)initWithFontDictionary:(CGPDFDictionaryRef)aFontDictionary;

- (void)parse;


// sub-process of parsing.
// typically it's not necessary to use these directly.
// not check Subtype entry. (check in 'parse')
- (void)parseForType0;
- (void)parseForType1; // standard 14 font currently not supported.
- (void)parseForMMType1; // not supported (have no plan)
- (void)parseForType3;
- (void)parseForTrueType;

@end
