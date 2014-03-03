//
//  ZXPDFCMapFileParser.h
//  ZyyxLibraries
//
//  Created by gotow on 11/05/23.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFCMapParser.h"


@interface ZXPDFCMapFileParser : ZXPDFCMapParser {

}

@property (nonatomic, strong) NSURL *fileURL;

- (id)initWithFileURL:(NSURL *)aFileURL;
+ (id)parserWithFileURL:(NSURL *)aFileURL;

@end
