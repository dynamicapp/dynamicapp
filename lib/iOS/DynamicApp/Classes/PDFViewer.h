//
//  PDFViewer.h
//  DynamicApp
//
//  Created by Zyyx on 11/6/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"
#import "ZXPDFViewController.h"

@interface PDFViewer : DynamicAppPlugin <NSURLConnectionDelegate>

@property (nonatomic, retain) ZXPDFViewController *pdfViewController;
@property (nonatomic, retain) NSURLConnection *urlConnection;
@property (nonatomic, retain) NSFileHandle *fileHandle;
@property (nonatomic, retain) NSString  *filePath;

- (void)show:(NSDictionary *)arguments withOptions:(NSDictionary *)options;

@end
