//
//  PDFLinkAnnotation.h
//  ZyyxLibraries
//
//  Created by gotow on 10/06/28.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>
#import "ZXPDFLocalDestination.h"




@class ZXPDFPageView;
@class ZXPDFLinkAnnotation;

@protocol ZXPDFLinkAnnotationDelegate <NSObject>
- (void)linkAnnotation:(ZXPDFLinkAnnotation *)anAnnotation invokeURL:(NSURL *)aURL;
@end


@interface ZXPDFLinkAnnotation : NSObject <UIAlertViewDelegate> {
    NSURL *url;
    ZXPDFLocalDestination *localDestination;
    CGRect rectByPDFCoodinate;

    UIAlertView *alertView;
    UIViewController *__unsafe_unretained parentViewController;
    ZXPDFPageView *__unsafe_unretained pageView;

    id __unsafe_unretained delegate;
}

@property (nonatomic, strong) NSURL *url;
@property (nonatomic, strong) ZXPDFLocalDestination *localDestination;
@property (nonatomic) CGRect rectByPDFCoodinate;

@property (nonatomic, strong) UIAlertView *alertView;
@property (nonatomic, unsafe_unretained) UIViewController *parentViewController; // use to present by modal.
@property (nonatomic, unsafe_unretained) ZXPDFPageView *pageView; // use to jump in document.

@property (nonatomic, unsafe_unretained) id delegate;

- (id)initWithCGPDFDictionary:(CGPDFDictionaryRef)pdfDictionary;
+ (id)annotationWithCGPDFDictionary:(CGPDFDictionaryRef)pdfDictionary;

- (IBAction)invokeOwnLink:(id)sender;


@end
