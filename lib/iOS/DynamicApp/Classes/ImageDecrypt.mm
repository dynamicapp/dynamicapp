//
//  ImageDecrypt.m
//  DynamicApp
//
//  Created by ZYYX on 2/17/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

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
