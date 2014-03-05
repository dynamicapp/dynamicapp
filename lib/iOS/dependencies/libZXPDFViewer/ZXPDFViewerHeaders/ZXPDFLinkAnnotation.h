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
