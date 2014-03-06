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


extern NSString * const ZXPDFCMapParserError;
extern const NSInteger ZXPDFCMapParserErrorCodeInvalidFormat;
extern const NSInteger ZXPDFCMapParserErrorCodeLineIsTooLong;
extern const NSInteger ZXPDFCMapParserErrorCodeUnknown;

extern const NSInteger ZXPDFCMapParserRemainsUnsetValue; // -1


@class ZXPDFCMapParser;

@protocol ZXPDFCMapParserDelegate <NSObject>
@required
- (void)cMapParser:(ZXPDFCMapParser *)aParser didGetName:(NSString *)aCMapName;
- (void)cMapParser:(ZXPDFCMapParser *)aParser didGetCIDSystemInfo:(NSDictionary *)aCIDSystemInfoDictionary;
- (void)cMapParser:(ZXPDFCMapParser *)aParser didGetUseCMap:(NSString *)aParentCMapName;
- (void)cMapParserDidFinishParsing:(ZXPDFCMapParser *)aParser;
- (void)cMapParserDidFailParsing:(ZXPDFCMapParser *)aParser withError:(NSError *)anError;

@optional
- (void)cMapParser:(ZXPDFCMapParser *)aParser didGetWMode:(NSNumber *)aWMode;

// character code (include code space) and cid is 16bit or 32bit unsigned integer.
// typically 32bit value is surrogate pair of UTF16.

// for code space range
- (void)          cMapParser:(ZXPDFCMapParser *)aParser
    didGetCodespaceRangeFrom:(NSNumber *)aStartOfRange
                          to:(NSNumber *)anEndOfRange
            forNumberOfBytes:(NSNumber *)aNumberOfBytes;

// for character code to CID
- (void)  cMapParser:(ZXPDFCMapParser *)aParser
           didGetCID:(NSNumber *)aCID
    forCharacterCode:(NSNumber *)aCharCode;
- (void)cMapParser:(ZXPDFCMapParser *)aParser
         didGetCID:(NSNumber *)aCID
      forRangeFrom:(NSNumber *)aStartOfCharCode
                to:(NSNumber *)anEndOfCharCode;

// for notdef character code to CID
- (void)  cMapParser:(ZXPDFCMapParser *)aParser
     didGetNotdefCID:(NSNumber *)aCID
    forCharacterCode:(NSNumber *)aCharCode;
- (void) cMapParser:(ZXPDFCMapParser *)aParser
    didGetNotdefCID:(NSNumber *)aCID
       forRangeFrom:(NSNumber *)aStartOfCharCode
                 to:(NSNumber *)anEndOfCharCode;

// for character code to Unicode (e.g. ToUnicode entry)
// unicode value is a list(NSArray) of unichar(NSNumber of 16bit/unsignedShortValue) values.
- (void)  cMapParser:(ZXPDFCMapParser *)aParser
       didGetUnicode:(NSArray *)anUnicodeValue
    forCharacterCode:(NSNumber *)aCharCode;
- (void)cMapParser:(ZXPDFCMapParser *)aParser
     didGetUnicode:(NSArray *)anUnicodeValue
      forRangeFrom:(NSNumber *)aStartOfCharCode
                to:(NSNumber *)anEndOfCharCode;
@end


/* Abstract class for CMap parsing classes.
 * ZXPDFCMapFileParser, ZXPDFCMapStreamParser
 */
@interface ZXPDFCMapParser : NSObject {

}

@property (nonatomic, unsafe_unretained) id<ZXPDFCMapParserDelegate> delegate;
@property (nonatomic, strong) NSInputStream *dataStream;
@property (nonatomic, strong) NSError *failedReason;

- (void)parse; // Abstract method. MUST override on subclass.
               // if using default parse process, use following example as base.
               // - (void)parse {
               //   [self cleanupProcess];
               //   /* set dataStream and open it */
               //   self.dataStream = ...;
               //   [self.dataStream open];
               //   [self startProcess];
               // }

// default parse process.
- (void)cleanupProcess; // SHOULD call at first in 'parse' method.
- (void)startProcess; // SHOULD call to start default parse process after set dataStream.

- (void)failWithError:(NSError *)anError; // set error to failedReason and invoke failed delegate.


// utilities for subclass.
// it's not necessary to use them if using default parse process.

// check responsibility and invoke if possible.
// use self.delegate as receiver, self as cMapParser: parameter.
- (void)invokeDelegateSelector:(SEL)selector
                    withObject:(id)aFirstParam
                    withObject:(id)aSecondParam
                    withObject:(id)aThirdParam;

@property (nonatomic, strong) NSData *oddData;
@property (nonatomic) NSInteger cidcharRemains;
@property (nonatomic) NSInteger cidrangeRemains;
@property (nonatomic) NSInteger bfcharRemains;
@property (nonatomic) NSInteger bfrangeRemains;
@property (nonatomic) NSInteger codespacerangeRemains;
@property (nonatomic) NSInteger notdefcharRemains;
@property (nonatomic) NSInteger notdefrangeRemains;
@property (nonatomic, strong) NSMutableDictionary *systemInfoBuffer;
@property (nonatomic) NSInteger systemInfoRemains;
@property (nonatomic, strong) NSCharacterSet *separators;

// setting read buffer size.
+ (NSInteger)bufferSize;
+ (void)setBufferSize:(NSInteger)newBufferSize;
+ (void)resetBufferSize;
+ (NSInteger)lineLengthLimit;

- (void)finishProcess;
- (NSArray *)splitDataByFirstDelimiter:(NSData *)aData; // return NSData pair or nil.
- (NSString *)lineFromStream:(NSInputStream *)aStream withOddData:(NSData *)anOddData;
- (BOOL)processLine:(NSString *)aLine;

// return 1 if success, 0 if failed.
// do NOT check aList's valid range (may crash if unexpected array given).
- (NSInteger)processCidchar:(NSArray *)aList notdef:(BOOL)isNotdef;
- (NSInteger)processCidrange:(NSArray *)aList notdef:(BOOL)isNotdef;
- (NSInteger)processBfchar:(NSArray *)aList;
- (NSInteger)processBfrange:(NSArray *)aList;
- (NSInteger)processArrayedBfrange:(NSArray *)aList;
- (NSInteger)processCodespacerange:(NSArray *)aList;
- (NSInteger)processCodespacerangeOfOneLiner:(NSArray *)aList;

- (void)processCMapNameWithPDFName:(NSString *)aPDFNameFormatString;
- (void)processCIDSystemInfoDictionaryWithBuffer; // use systemInfoBuffer.
- (void)processCIDSystemInfoDictionaryWithOneLiner:(NSArray *)aList;
- (void)processUseCMapWithName:(NSString *)aNameString;
- (void)processWModeWithIntegerString:(NSString *)aString;

- (NSString *)stringFromPDFName:(NSString *)aPDFNameFormatString;
- (NSString *)stringFromPDFString:(NSString *)aPDFStringFormatString;
- (NSString *)stringFromPDFHexString:(NSString *)aPDFHexStringFormatString; // do not convert to encoded string, only remove parentheses.
- (NSNumber *)numberFromIntegerString:(NSString *)aString; // return nil if string is not number or has over short range value.
- (NSNumber *)numberFromHexString:(NSString *)aHexString; // return nil if string length is not 4.
- (NSArray *)numberListFromHexString:(NSString *)aHexString;

@end
