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

#import "QRReader.h"

#import <QRCodeReader.h>
#import <UniversalResultParser.h>
#import <ParsedResult.h>
#import <ResultAction.h>

#import "PluginResult.h"

static const int FAILED = 1;

@interface QRReader ()
- (void)showZXingWidgetController;
@end

@implementation QRReader

@synthesize zXingWidgetController;
@synthesize parsedResult;

- (void)scan:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
    
    self.zXingWidgetController = [[[ZXingWidgetController alloc] initWithDelegate:self showCancel:YES OneDMode:NO] autorelease];
    
    if([[arguments objectForKey:@"source"] intValue] > 0) {//
        self.zXingWidgetController.isCamera = NO;
        
        UIImagePickerController *imagePicker = [[UIImagePickerController alloc] init];
        imagePicker.delegate = self;
        imagePicker.sourceType = UIImagePickerControllerSourceTypePhotoLibrary;
        
        [[self viewController] presentViewController:imagePicker animated:YES completion:nil];
        [imagePicker release];
    } else {
        self.zXingWidgetController.isCamera = YES;
        
        [self showZXingWidgetController];
    }    
}

- (void)executeQRCodeResult:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    self.parsedResult = [[[UniversalResultParser parsedResultForString:[arguments objectForKey:@"resultString"]] retain] autorelease];
    
    if(!(self.parsedResult == nil || self.parsedResult.actions == nil || self.parsedResult.actions.count == 0)) {        
        UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithFrame:self.viewController.view.bounds];
        
        for (ResultAction *action in self.parsedResult.actions) {
            [actionSheet addButtonWithTitle:[action title]];
        }
        
        int cancelIndex = [actionSheet addButtonWithTitle:@"Cancel"];
        actionSheet.cancelButtonIndex = cancelIndex;
        actionSheet.delegate = self;
        
        [actionSheet showInView:[UIApplication sharedApplication].keyWindow];
        [actionSheet release];
    }
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

#pragma mark -
#pragma mark ZXingDelegateMethods
// ZXingDelegate methods
- (void)zxingController:(ZXingWidgetController*)controller didScanResult:(NSString *)resultString {
    [controller dismissViewControllerAnimated:YES completion:nil];
    
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsString:resultString];
    NSString *jsString = [result toSuccessCallbackString:self.callbackId];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
    self.zXingWidgetController = nil;
}

- (void)zxingController:(ZXingWidgetController*)controller didScanFail:(NSString *)resultString {
    [controller dismissViewControllerAnimated:YES completion:nil];
    
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR messageAsInt:FAILED];
    NSString *jsString = [result toErrorCallbackString:self.callbackId];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
    self.zXingWidgetController = nil;
}

- (void)zxingControllerDidCancel:(ZXingWidgetController*)controller {
    [controller dismissViewControllerAnimated:YES completion:nil];
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    self.zXingWidgetController = nil;
}

- (void)showZXingWidgetController {
    QRCodeReader *qrcodeReader = [[QRCodeReader alloc] init];
    NSSet *readers = [[NSSet alloc ] initWithObjects:qrcodeReader,nil];
    [qrcodeReader release];
    
    self.zXingWidgetController.readers = readers;
    [readers release];
    
    [[self viewController] presentViewController:self.zXingWidgetController animated:YES completion:nil];
}

// UIImagePickerDelegate methods
- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingImage:(UIImage *)image editingInfo:(NSDictionary *)editingInfo {
    [picker dismissModalViewControllerAnimated:NO];
    
    self.zXingWidgetController.scanImage = image;
    
    [self showZXingWidgetController];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
	[picker dismissViewControllerAnimated:YES completion:nil];
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

//UIActionSheetDelegate method
- (void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex {
    if(buttonIndex != actionSheet.cancelButtonIndex) {
        [[self.parsedResult.actions objectAtIndex:buttonIndex] performActionWithController:[self viewController] shouldConfirm:YES];
    }
}

- (void)dealloc {
    self.zXingWidgetController = nil;
    self.parsedResult = nil;
    [super dealloc];
}

@end