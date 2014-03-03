//
//  PDFLocalDestination.h
//  ZyyxLibraries
//
//  Created by gotow on 10/07/22.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface ZXPDFLocalDestination : NSObject {
    // explicit destination
    NSNumber *pageNumber; // long number
    // TODO: add some variables for another information.

    // named destination
    NSString *nameString;
}

@property (nonatomic, strong) NSNumber *pageNumber;
@property (nonatomic, copy) NSString *nameString;

- (id)initWithCGPDFObject:(CGPDFObjectRef)destinationValue;
+ (id)destinationWithCGPDFObject:(CGPDFObjectRef)destinationValue;


@end
