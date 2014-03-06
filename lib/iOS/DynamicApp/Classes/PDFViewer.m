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

#import "PDFViewer.h"
#import "Shortcut.h"

@interface PDFViewer(private)
- (NSString *)pdfPathForResource:(NSDictionary *)resourceOptions withOptions:(NSDictionary *)options;
- (void)openPDFViewer;
@end

@implementation PDFViewer

@synthesize pdfViewController;
@synthesize urlConnection;
@synthesize fileHandle;
@synthesize filePath;

- (void)show:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *aFilePath = [self pdfPathForResource:arguments withOptions:options];

    if(aFilePath) {
        NSLog(@"aFilePath:%@", aFilePath);
        [self openPDFViewer:aFilePath];
    } else {
        NSString *pdfPath = [options objectForKey:@"path"];
        if(![pdfPath hasPrefix:HTTP_SCHEME_PREFIX]) {
            [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        }
    }
}


- (NSString *)pdfPathForResource:(NSDictionary *)resourceOptions withOptions:(NSDictionary *)options {
    NSString *pdfPath = [resourceOptions objectForKey:@"path"];
    NSString *path = nil;
        
    NSFileManager *fileMgr = [NSFileManager defaultManager];
    NSString *fullPath = nil;
    
    if([pdfPath hasPrefix:HTTP_SCHEME_PREFIX]) {
        fullPath = [[Shortcut applicationDocumentsDirectory] stringByAppendingPathComponent: [pdfPath lastPathComponent]];
        NSLog(@"fullPath:%@", fullPath);        
        if([fileMgr fileExistsAtPath:fullPath]) {
            path = fullPath;
        } else {
            NSLog(@"fullPath:%@", fullPath);
            [[NSFileManager defaultManager] createFileAtPath:fullPath contents:[NSData data] attributes:nil];
            NSFileHandle *aHandle = [NSFileHandle fileHandleForWritingAtPath:fullPath];
            if (!aHandle) {

            }
            self.fileHandle = aHandle;
            self.filePath   = fullPath;

            NSURL *resourceURL = [NSURL URLWithString:pdfPath];

            NSURLRequest *aRequest = [NSURLRequest requestWithURL:resourceURL];
            self.urlConnection = [NSURLConnection connectionWithRequest:aRequest delegate:self];

            if (self.urlConnection) {
            
            }
        }
    } else {
        fullPath = [DynamicAppPlugin pathForResource:pdfPath];
        if([fileMgr fileExistsAtPath:fullPath]) {
            path = fullPath;
        } 
    }
    
    return path;
}

- (void)backButtonPressed {
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
    [pdfViewController dismissViewControllerAnimated:YES completion:nil];
#else
    [pdfViewController dismissModalViewControllerAnimated:YES];
#endif
    self.pdfViewController = nil;
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

- (void)openPDFViewer : (NSString *)aFilePath {
    self.pdfViewController = [[ZXPDFViewController alloc] initWithNibName:[[ZXPDFViewController class] description] bundle:nil];
    
    //    ZXPDFDocumentViewSimplePageAnimator (default)
    // or ZXPDFDocumentViewLinearPageAnimator
    // or ZXPDFDocumentViewHardPageFlipAnimator
    NSString *aClassOfAnimator = @"ZXPDFDocumentViewSimplePageAnimator";
    
    // ZXPDFDocumentViewLeftToRight (default) or ZXPDFDocumentViewRightToLeft
    ZXPDFDocumentViewPageOrder pageOrder = ZXPDFDocumentViewLeftToRight;
    
    NSString *aCachePath = [[DynamicAppPlugin pathForResource:CACHE_FOLDER] stringByAppendingPathComponent:[aFilePath lastPathComponent]];
    self.pdfViewController.configurations = [NSMutableDictionary dictionaryWithObjectsAndKeys:
                                             aFilePath, ZXPDFViewControllerFilePathKey,
                                             @"Back", ZXPDFViewControllerBackButtonTitleKey,
                                             self, ZXPDFViewControllerBackButtonTargetKey,
                                             NSStringFromSelector(@selector(backButtonPressed)), ZXPDFViewControllerBackButtonActionKey,
                                             [NSNumber numberWithInt:pageOrder], ZXPDFViewControllerPageOrderKey,
                                             [NSNumber numberWithBool:NO], ZXPDFViewControllerDocumentIsDoubleTruckKey,
                                             [NSNumber numberWithBool:NO], ZXPDFViewControllerMemoEnableKey,
                                             [NSNumber numberWithBool:YES], ZXPDFViewControllerBackgroundLoadingEnableKey,
                                             [NSNumber numberWithBool:YES], ZXPDFViewControllerPageCacheEnableKey,
                                             aCachePath, ZXPDFViewControllerCacheStorePathKey,
                                             [NSNumber numberWithBool:YES], ZXPDFViewControllerSearchIndexingAutoStartKey,
                                             aClassOfAnimator, ZXPDFViewControllerPageAnimatorClassNameKey,
                                             nil];
    

        
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
    [[self viewController] presentViewController:self.pdfViewController animated:YES completion:nil];
#else
    [[self viewController] presentModalViewController:self.pdfViewController animated:YES];
#endif
    
    CGRect rect = self.pdfViewController.toolbar.frame;
    rect.origin.y -= 20;
    self.pdfViewController.toolbar.frame = rect;
    
    if([ZXUtility deviceIs_iPad]) {
        NSArray *array = self.pdfViewController.toolbar.items;
        NSMutableArray *items = [NSMutableArray arrayWithArray:array];
        [items removeObjectAtIndex:0];
        self.pdfViewController.toolbar.items = items;
    }
}

#pragma mark NSURLConnection delegate

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    NSLog(@"%@", [error description]);
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    NSInteger aStatusCode = [(NSHTTPURLResponse *)response statusCode];
    if (aStatusCode / 100 != 2) {
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        return;
    }
    
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    if (self.fileHandle) {
        @try {
            [self.fileHandle writeData:data];
        } @catch (NSException *exception) {
        }
     }
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    self.urlConnection = nil;
    if (self.fileHandle) {
        [self.fileHandle closeFile];
        self.fileHandle = nil;
        [self openPDFViewer:self.filePath];
        self.filePath = nil;
    }
}

- (void)dealloc {
    self.pdfViewController = nil;
    self.urlConnection = nil;
    self.filePath = nil;
    self.fileHandle = nil;
    
    [super dealloc];
}
@end