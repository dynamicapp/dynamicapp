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
#import "ZXPDFTextExtractor.h"
#import "ZXPDFSearchIndex.h"
#import "ZXMecabParser.h"


@class ZXPDFSearchIndexMaker;

@protocol ZXPDFSearchIndexMakerDelegate <NSObject>
@required
- (void)searchIndexMakerDidStart:(ZXPDFSearchIndexMaker *)aMaker; // did be set auto-generated embeddedCMapDefinitionStoreURL if it was nil.
- (void)searchIndexMakerDidCancel:(ZXPDFSearchIndexMaker *)aMaker;
- (void)searchIndexMakerDidFinish:(ZXPDFSearchIndexMaker *)aMaker;
- (void)searchIndexMakerDidFail:(ZXPDFSearchIndexMaker *)aMaker withError:(NSError *)anError;
@optional
- (void)searchIndexMakerWillStart:(ZXPDFSearchIndexMaker *)aMaker; // be able to set embeddedCMapDefinitionStoreURL in this if needed.
- (void)searchIndexMaker:(ZXPDFSearchIndexMaker *)aMaker didExtractPage:(NSInteger)pageNumber;
@end


@interface ZXPDFSearchIndexMaker : NSObject <ZXPDFTextExtractorDelegate, ZXMecabParserDelegate> {

}

@property (nonatomic, strong, readonly) ZXPDFTextExtractor *textExtractor;
@property (nonatomic, strong, readonly) NSArray *processedPageNumbers; // contains NSNumber(page number) that processed.
@property (nonatomic, strong, readonly) NSURL *fileURL;
@property (nonatomic, strong, readonly) ZXPDFSearchIndex *searchIndex;
@property (readonly) BOOL nowProcessing;

@property (unsafe_unretained) id<ZXPDFSearchIndexMakerDelegate> delegate;
@property (nonatomic, strong) NSURL *embeddedCMapDefinitionStoreURL; // using for ZXPDFTextExtractor.embeddedCMaps
                                                                     // be set automatically if nil.
@property (nonatomic) BOOL shouldCreateMorphemeIndex; // default is NO.
                                                      // if set YES, alalyze and create morpheme index.

+ (NSString *)filePathForDocumentWithURL:(NSURL *)aFileURL;

- (id)initWithPDFFileURL:(NSURL *)aFileURL searchIndex:(ZXPDFSearchIndex *)aSearchIndex;

- (BOOL)startProcess; // return YES if started normally. process all pages.
- (BOOL)startProcessWithPageNumber:(NSNumber *)aPageNumber; // process only specified page.
- (void)cancelProcess;

@end
