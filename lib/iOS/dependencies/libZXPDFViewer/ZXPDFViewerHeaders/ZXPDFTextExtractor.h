//
//  ZXPDFTextExtractor.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/02.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFCMapDefinition.h"
#import "ZXPDFCMapEntryMaker.h"
#import "ZXPDFFontInfoMaker.h"


@class ZXPDFTextExtractor;

extern NSString * const ZXPDFTextExtractorError;
extern const NSInteger ZXPDFTextExtractorErrorCodeParseError;
extern const NSInteger ZXPDFTextExtractorErrorCodeDocumentError;

@protocol ZXPDFTextExtractorDelegate <NSObject>
- (void)textExtractor:(ZXPDFTextExtractor *)anExtractor didGetText:(NSString *)aText ofPageNumber:(NSInteger)pageNumber;
- (void)textExtractorDidFinishProcess:(ZXPDFTextExtractor *)anExtractor;
- (void)textExtractorDidCancelProcess:(ZXPDFTextExtractor *)anExtractor;
- (void)textExtractorDidFailProcess:(ZXPDFTextExtractor *)anExtractor withError:(NSError *)anError;

// for character index and text position (e.g. highlight search results)
- (void)textExtractor:(ZXPDFTextExtractor *)anExtractor
      didGetCharacter:(NSString *)aCharacter
               onRect:(CGRect)characterRect // based on mediaBox size rect.
              atIndex:(NSInteger)index
         ofPageNumber:(NSInteger)pageNumber;
@end


@interface ZXPDFTextExtractor : NSObject <ZXPDFCMapEntryMakerDelegate, ZXPDFFontInfoMakerDelegate> {

}

@property (nonatomic, retain, readonly) __attribute__((NSObject)) CGPDFDocumentRef document;
@property (nonatomic, retain, readonly) __attribute__((NSObject)) CGPDFPageRef currentPage;
@property (nonatomic, strong, readonly) ZXPDFCMapDefinition *predefinedCMaps;
@property (nonatomic, readonly) CGPDFScannerRef scanner; // assigned while scanning.

@property (nonatomic, unsafe_unretained) id<ZXPDFTextExtractorDelegate> delegate;
@property (nonatomic, strong) ZXPDFCMapDefinition *embeddedCMaps; // if nil, be created automatically in cache directory.
                                                                  // in this case, remove previous store file.
                                                                  // should set this by yourself if do not need its behavior.
@property (nonatomic) BOOL extractFontInformation; // if YES, extract font information using ZXPDFFontParser.
                                                   // default YES;
@property (nonatomic) BOOL extractFontInfomation DEPRECATED_ATTRIBUTE;

- (id)initWithPDFDocument:(CGPDFDocumentRef)aDocument;

- (void)startProcess; // startProcessFromPageNumber:1
- (void)startProcessFromPageNumber:(NSInteger)pageNumber;
- (void)startProcessForPageNumberRange:(NSRange)pageNumberRange;
- (void)cancelProcess;

// utility

// get 'length' bytes from 'ucharArray' at 'startIndex', and convert to NSNumber.
// this method do NOT CHECK array's range, must check by yourself.
+ (NSNumber *)numberFromUCharArray:(const unsigned char *)ucharArray
                           atIndex:(NSInteger)startIndex
                            length:(NSInteger)length;

@end
