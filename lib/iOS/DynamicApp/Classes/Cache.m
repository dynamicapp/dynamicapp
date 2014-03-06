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