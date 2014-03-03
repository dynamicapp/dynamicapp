//
//  ZXPDFSearchMorpheme+Utility.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/17.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFSearchMorpheme.h"
#import "ZXMecabNode.h"


@interface ZXPDFSearchMorpheme (ZXPDFSearchMorpheme_Utility)

@property (nonatomic) CGRect rectValue;

- (void)assignFromMecabNode:(ZXMecabNode *)aNode;

@end
