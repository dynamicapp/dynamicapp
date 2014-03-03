//
//  PDFCatalogScanner.h
//  ZyyxLibraries
//
//  Created by gotow on 10/07/22.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFDocumentObject.h"
#import "ZXPDFLocalDestination.h"


@interface ZXPDFCatalogScanner : NSObject {
    ZXPDFDocumentObject *documentObject;
    NSMutableDictionary *localDestinationsCache;
}

@property (nonatomic, strong) ZXPDFDocumentObject *documentObject;
@property (nonatomic, strong) NSMutableDictionary *localDestinationsCache;
@property (nonatomic, readonly) CGPDFDictionaryRef catalog;

- (id)initWithDocumentObject:(ZXPDFDocumentObject *)object;
+ (id)catalogScannerWithDocumentObject:(ZXPDFDocumentObject *)object;

- (ZXPDFLocalDestination *)localDestinationWithName:(NSString *)nameOrString;


@end
