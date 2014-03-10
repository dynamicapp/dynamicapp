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

#import "Camera.h"
#import "NSData+Base64.h"
#import "Shortcut.h"
#import "PluginResult.h"
#import "AssetsLibrary/AssetsLibrary.h"
#import "MobileCoreServices/UTCoreTypes.h"

@implementation Camera

@synthesize imagePicker;
@synthesize quality, destinationType, sourceType, encodingType, targetSize;

- (id)initWithWebView:(UIWebView *)theWebView withViewController:(UIViewController *)theViewController {
    if(self = [super initWithWebView:theWebView withViewController:theViewController]) {
        
        self.imagePicker = [[[UIImagePickerController alloc] init] autorelease];
        self.imagePicker.delegate = self;
        [self setSettings:nil];
    }
    return self;
}

- (void)dealloc {
    self.imagePicker.delegate = nil;
    self.imagePicker = nil;
    [super dealloc];
}

- (void)getPicture:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    if([arguments objectForKey:@"quality"]) {
        self.quality = [[arguments objectForKey:@"quality"] floatValue] * 0.01;
    } else {
        self.quality = 0.1;
    }
    
    if([arguments objectForKey:@"destinationType"]) {
        self.destinationType = (DestinationType)[[arguments objectForKey:@"destinationType"]  intValue];
    } else {
        self.destinationType = DestinationTypeDataUrl;
    }
    
    if([arguments objectForKey:@"sourceType"]) {
        self.sourceType = (UIImagePickerControllerSourceType)[[arguments objectForKey:@"sourceType"]  intValue];
    } else {
        self.sourceType = UIImagePickerControllerSourceTypeCamera;
    }
    
    if([arguments objectForKey:@"encodingType"]) {
        self.encodingType = (EncodingType)[[arguments objectForKey:@"encodingType"]  intValue];
    } else {
        self.encodingType = EncodingTypeJPEG;
    }
    
    if([arguments objectForKey:@"targetWidth"] != nil && 
       [arguments objectForKey:@"targetHeight"] != nil) {
        self.targetSize = CGSizeMake([[arguments objectForKey:@"targetWidth"] floatValue], [[arguments objectForKey:@"targetHeight"] floatValue]);
    } else {
        self.targetSize = CGSizeMake(0, 0);
    }
    
    self.callbackId = [options objectForKey:@"callbackId"];
    
    if(self.imagePicker == nil){
        self.imagePicker = [[[UIImagePickerController alloc] init] autorelease];
    }
    
    self.imagePicker.delegate = self;
    self.imagePicker.sourceType = self.sourceType;
    self.imagePicker.mediaTypes = [NSArray arrayWithObjects:(NSString*)kUTTypeImage, nil];
    self.imagePicker.cameraCaptureMode = UIImagePickerControllerCameraCaptureModePhoto;
    
	[[self viewController] presentViewController:imagePicker animated:YES completion:nil];
}

- (void)recordVideo:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    PluginResult *result = nil;
    NSString *jsString = nil;
    self.callbackId = [options objectForKey:@"callbackId"];
    
    NSString* mediaType = nil;
    
	if ([UIImagePickerController isSourceTypeAvailable:UIImagePickerControllerSourceTypeCamera]) {
        if(self.imagePicker == nil){
            self.imagePicker = [[[UIImagePickerController alloc] init] autorelease];
        }
        NSArray* types = nil;
        if ([UIImagePickerController respondsToSelector: @selector(availableMediaTypesForSourceType:)]){
             types = [UIImagePickerController availableMediaTypesForSourceType:UIImagePickerControllerSourceTypeCamera];
        
            if ([types containsObject:(NSString*)kUTTypeMovie]){
                mediaType = (NSString*)kUTTypeMovie;
            } else if ([types containsObject:(NSString*)kUTTypeVideo]){
                mediaType = (NSString*)kUTTypeVideo;
            }
        }
    }
    if (!mediaType) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR messageAsInt:0];
        jsString = [result toErrorCallbackString:self.callbackId];
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        [self.webView stringByEvaluatingJavaScriptFromString:jsString];
    } else {
        self.imagePicker.delegate = self;
        self.imagePicker.sourceType = UIImagePickerControllerSourceTypeCamera;
        self.imagePicker.allowsEditing = NO;
        
        // iOS 3.0
        self.imagePicker.mediaTypes = [NSArray arrayWithObjects: mediaType, nil];
        
        // iOS 4.0
        if ([UIImagePickerController respondsToSelector:@selector(cameraCaptureMode)]) {
            self.imagePicker.cameraCaptureMode = UIImagePickerControllerCameraCaptureModeVideo;
        }
        
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
        [self.viewController presentViewController:self.imagePicker animated:YES completion:nil];
#else
        [self.viewController presentModalViewController:self.imagePicker animated:YES ];
#endif
    }
}

- (void)imagePickerController:(UIImagePickerController *)picker didFinishPickingMediaWithInfo:(NSDictionary *)info {

    if ([picker respondsToSelector:@selector(presentingViewController)]) {
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
        [[picker presentingViewController] dismissViewControllerAnimated:YES completion:nil];
#else
        [[picker presentingViewController] dismissModalViewControllerAnimated:YES];
#endif
    } else {
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
        [[picker parentViewController] dismissViewControllerAnimated:YES completion:nil];
#else
        [[picker parentViewController] dismissModalViewControllerAnimated:YES];
#endif
    }

    UIImage* image = nil;
	NSString* mediaType = [info objectForKey:UIImagePickerControllerMediaType];
    if (!mediaType || [mediaType isEqualToString:(NSString*)kUTTypeImage]){
		if ([UIImagePickerController respondsToSelector: @selector(allowsEditing)] && (picker.allowsEditing && [info objectForKey:UIImagePickerControllerEditedImage])) {
                image = [info objectForKey:UIImagePickerControllerEditedImage];
        } else {
			image = [info objectForKey:UIImagePickerControllerOriginalImage];
		}
    }
    
    if (image != nil) {
        UIImage *scaledImage = nil;
        
        if (self.targetSize.width > 0 || self.targetSize.height > 0) {
            scaledImage = [self imageByScalingAndCroppingForSize:image toSize:self.targetSize];
        }
        
        //flatten it to NSData as a JPEG, low quality
        NSData *flatImage = nil;
        if(self.encodingType == EncodingTypeJPEG) {
            flatImage = UIImageJPEGRepresentation(scaledImage == nil ? image : scaledImage, self.quality);
        } else {
            flatImage = UIImagePNGRepresentation(scaledImage == nil ? image : scaledImage);
        }
        
        if(self.destinationType == DestinationTypeDataUrl){
            PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsString:[NSString stringWithFormat: @"data:image/jpeg;base64,%@", [flatImage base64EncodedString] ]];
            NSString *jsString = [result toSuccessCallbackString:self.callbackId];
        
            [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
            [self.webView stringByEvaluatingJavaScriptFromString:jsString];
        } else {
            ALAssetsLibrary *library = [[ALAssetsLibrary alloc] init];
            [library writeImageDataToSavedPhotosAlbum:UIImageJPEGRepresentation(image, 1.0) metadata:nil completionBlock:^(NSURL *assetURL, NSError *error){  
                PluginResult *result = nil;
                NSString *jsString;
                
                if (error == nil) {  
                    NSArray *parameters = [[assetURL query] componentsSeparatedByCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"=&"]];
                    NSMutableDictionary *query = [NSMutableDictionary dictionary];
                    for (int i = 0; i < [parameters count]; i=i+2) {
                        [query setObject:[parameters objectAtIndex:i+1] forKey:[parameters objectAtIndex:i]];
                    }
    #if TARGET_IPHONE_SIMULATOR
                    NSString *fullPath = [NSHomeDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"/tmp/%@.%@", [query objectForKey:@"id"], [query objectForKey:@"ext"]]];
    #else 
                    NSString *fullPath = [NSTemporaryDirectory() stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.%@", [query objectForKey:@"id"], [query objectForKey:@"ext"]]];
    #endif                
                    if([flatImage writeToFile:fullPath atomically:YES]) {
                        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsString:[NSString stringWithFormat:@"file://%@", fullPath]];
                        jsString = [result toSuccessCallbackString:self.callbackId];
                    }
                }
                
                if(result == nil) {
                    result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
                    jsString = [result toErrorCallbackString:self.callbackId];
                }
        
                [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
                [self.webView stringByEvaluatingJavaScriptFromString:jsString];
            }];
            [library release];
        }
    } else if([mediaType isEqualToString:(NSString *)kUTTypeMovie]) {
        NSString *moviePath = [[info objectForKey: UIImagePickerControllerMediaURL] path];
        
        if (moviePath) {
            ALAssetsLibrary *library = [[ALAssetsLibrary alloc] init];
            [library writeVideoAtPathToSavedPhotosAlbum:[NSURL fileURLWithPath:moviePath] completionBlock:^(NSURL *assetURL, NSError *error){  
                PluginResult *result = nil;
                NSString *jsString;
                
                if (error == nil) {
                    result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsString:[NSString stringWithFormat:@"file://%@", moviePath]];
                    jsString = [result toSuccessCallbackString:self.callbackId];
                }
                
                if(result == nil) {
                    result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
                    jsString = [result toErrorCallbackString:self.callbackId];
                }
        
                [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
                [self.webView stringByEvaluatingJavaScriptFromString:jsString];
            }];
            [library release];
        }
    }
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

- (void)imagePickerControllerDidCancel:(UIImagePickerController *)picker {
	[picker dismissViewControllerAnimated:YES completion:nil];
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

- (UIImage*)imageByScalingAndCroppingForSize:(UIImage*)anImage toSize:(CGSize)theTargetSize
{
    UIImage *sourceImage = anImage;
    UIImage *newImage = nil;        
    CGSize imageSize = sourceImage.size;
    CGFloat width = imageSize.width;
    CGFloat height = imageSize.height;
    CGFloat targetWidth = theTargetSize.width;
    CGFloat targetHeight = theTargetSize.height;
    CGFloat scaleFactor = 0.0;
    CGFloat scaledWidth = targetWidth;
    CGFloat scaledHeight = targetHeight;
    CGPoint thumbnailPoint = CGPointMake(0.0,0.0);
    
    if (CGSizeEqualToSize(imageSize, theTargetSize) == NO) 
    {
        CGFloat widthFactor = targetWidth / width;
        CGFloat heightFactor = targetHeight / height;
        
        if (widthFactor > heightFactor) 
            scaleFactor = widthFactor; // scale to fit height
        else
            scaleFactor = heightFactor; // scale to fit width
        scaledWidth  = width * scaleFactor;
        scaledHeight = height * scaleFactor;
        
        // center the image
        if (widthFactor > heightFactor)
        {
            thumbnailPoint.y = (targetHeight - scaledHeight) * 0.5; 
        }
        else 
            if (widthFactor < heightFactor)
            {
                thumbnailPoint.x = (targetWidth - scaledWidth) * 0.5;
            }
    }       
    
    UIGraphicsBeginImageContext(theTargetSize); // this will crop
    
    CGRect thumbnailRect = CGRectZero;
    thumbnailRect.origin = thumbnailPoint;
    thumbnailRect.size.width  = scaledWidth;
    thumbnailRect.size.height = scaledHeight;
    
    [sourceImage drawInRect:thumbnailRect];
    
    newImage = UIGraphicsGetImageFromCurrentImageContext();
    
    //pop the context to get back to the default
    UIGraphicsEndImageContext();
    
    return newImage;
}

@end
