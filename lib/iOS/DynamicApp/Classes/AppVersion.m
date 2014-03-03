//
//  AppVersion.m
//  DynamicApp
//
//  Created by ZYYX Inc on 10/11/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "AppVersion.h"
#import "PluginResult.h"

@implementation AppVersion

- (void) get:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *version = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"];
    if([version length] < 1) {
        version = @"";
    }
    
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsString:version];
    NSString *jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

@end
