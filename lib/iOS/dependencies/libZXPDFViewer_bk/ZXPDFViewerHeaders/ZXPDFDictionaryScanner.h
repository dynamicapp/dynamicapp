//
//  PDFDictionaryScanner.h
//  ZyyxLibraries
//
//  Created by gotow on 10/06/28.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import "ZXPDFPageObject.h"


@interface ZXPDFDictionaryScanner : NSObject {
    ZXPDFPageObject *pageObject;
}

@property (nonatomic, strong) ZXPDFPageObject *pageObject;

- (id)initWithPDFPageObject:(ZXPDFPageObject *)object;
+ (id)dictionaryScannerWithPDFPageObject:(ZXPDFPageObject *)object;

- (NSArray *)linkAnnotations;


@end
