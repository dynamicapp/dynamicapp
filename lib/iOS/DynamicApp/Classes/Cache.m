//
//  Cache.m
//  DynamicApp
//
//  Created by ZYYX on 3/16/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "Cache.h"
#import "define.h"
#import "PluginResult.h"
#import "DynamicAppPlugin.h"
#import "File.h"

@implementation ResourceCache

- (void) add:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSURL *resourceURL = [DynamicAppPlugin urlForResource:[arguments objectForKey:@"resourceURL"]];
    NSString *resourceId = [arguments objectForKey:@"id"];
    NSString *expireDate = [arguments objectForKey:@"expireDate"];
    
    PluginResult *result = nil;
    NSString *jsString = nil;
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    NSError *error = nil;
    
    NSURLRequest *request = [NSURLRequest requestWithURL:resourceURL];
    NSURLResponse *response = nil;
    NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
    
    if(data) {
        NSString *fullPath = [DynamicAppPlugin pathForResource:[NSString stringWithFormat:@"%@/%@%@%@", CACHE_FOLDER, CACHE_PREFIX, resourceId, [resourceURL lastPathComponent]]];
        NSFileManager *fileMgr = [NSFileManager defaultManager];
        
        if([fileMgr createDirectoryAtPath:[fullPath stringByDeletingLastPathComponent] withIntermediateDirectories:YES attributes:nil error:&error] ) {
            
            if([fileMgr createFileAtPath:fullPath contents:data attributes:nil]) {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK
                          messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:resourceId, @"id", expireDate, @"expireDate", fullPath, @"fullPath", [arguments objectForKey:@"resourceURL"], @"resourceURL", nil]];
                jsString = [result toSuccessCallbackString:theCallbackId];
            }
        }
    }
    
    if(!jsString) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        jsString = [result toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) remove:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    PluginResult *result = nil;
    NSString *jsString = nil;
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    
    if([[arguments objectForKey:@"fullPath"] isEqualToString:@"CLEAR_RESOURCE_CACHE"]) {
        [arguments setValue:[DynamicAppPlugin pathForResource:[NSString stringWithFormat:@"%@", CACHE_FOLDER]] forKey:@"fullPath"];
    }
    
    FileError errorCode = [File doRemove:arguments];
    
    if(!errorCode) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
        jsString = [result toSuccessCallbackString:theCallbackId];
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR messageAsInt:errorCode];
        jsString = [result toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

@end