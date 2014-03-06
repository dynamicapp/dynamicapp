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

#import "ImageDecrypt.h"
#import "NSData+Base64.h"
#import "PluginResult.h"
#import "FileEncryptor.h"


@implementation ImageDecrypt

- (void)decrypt:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    NSArray *imgSrcList = [arguments objectForKey:@"imgSrcList"];
    
    for (NSString *imgSrcStr in imgSrcList) {
        const char *imgSrc = [[DynamicAppPlugin pathForResource:imgSrcStr] UTF8String];
        unsigned char *imgData = nil;
        long size = 0;
        
        FileEncryptor *fileEncryptor = new FileEncryptor;
        fileEncryptor->Decrypt(imgSrc, &imgData, &size);
        delete fileEncryptor;        
        
        NSData *imageData = [NSData dataWithBytes:imgData length:size];
        
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:imgSrcStr, @"srcEnc", [NSString stringWithFormat: @"data:image/jpeg;base64,%@", [imageData base64EncodedString]], @"srcDec", nil]];
        result.keepCallback = [NSNumber numberWithInt:1];
        NSString *jsString = [result toSuccessCallbackString:theCallbackId];
        
        [self.webView stringByEvaluatingJavaScriptFromString:jsString];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

- (void) dealloc {
    [super dealloc];
}

@end
