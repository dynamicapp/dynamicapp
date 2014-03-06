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


// keys of dictionary created by dictionaryWithPDFStream:
extern NSString * const ZXPDFStreamDictionaryKey; // dictionary -> NSDictionary(stream dictionary)
extern NSString * const ZXPDFStreamDataKey; // data -> NSData(stream data)
extern NSString * const ZXPDFStreamFormatKey; // format -> NSNumber(CGPDFDataFormat/NSInteger)


@interface NSObject (ZXPDFObjectConverter)

+ (NSObject *)objectWithPDFObject:(CGPDFObjectRef)aPDFObject; // check type and use following methods.
                                                              // NOTICE: care for using array, dictionary and stream,
                                                              // they may use huge memory or process time.

// simple data types
+ (NSNull *)nullForPDFNull;
+ (NSNumber *)numberWithPDFBoolean:(CGPDFBoolean)pdfBoolean; // unsigned char
+ (NSNumber *)numberWithPDFInteger:(CGPDFInteger)pdfInteger; // long
+ (NSNumber *)numberWithPDFReal:(CGPDFReal)pdfReal; // CGFload
+ (NSString *)stringWithPDFName:(const char *)pdfName; // C string using ascii encoding.

// complex data types
+ (NSData *)dataWithPDFString:(CGPDFStringRef)aPDFString;
+ (NSString *)stringWithPDFASCIIString:(CGPDFStringRef)aPDFStringOfAsciiForm;
+ (NSDate *)dateWithPDFDateString:(CGPDFStringRef)aPDFStringOfDateForm;
// care for memory or process time using.
+ (NSArray *)arrayWithPDFArray:(CGPDFArrayRef)aPDFArray;
+ (NSDictionary *)dictionaryWithPDFDictionary:(CGPDFDictionaryRef)aPDFDictionary;
+ (NSDictionary *)dictionaryWithPDFStream:(CGPDFStreamRef)aPDFStream;

@end
