//
//  NSObject+ZXPDFObjectConverter.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/11.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

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
