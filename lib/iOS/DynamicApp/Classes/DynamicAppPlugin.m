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

#import "DynamicAppPlugin.h"
#import "Shortcut.h"

@implementation DynamicAppPlugin

@synthesize webView;
@synthesize settings;
@synthesize viewController;
@synthesize callbackId;

- (id) initWithWebView:(UIWebView *)theWebView withSettings:(NSDictionary *)theSettings withViewController:(UIViewController *)theViewController {
    if(self = [super init]) {
        [self initWithWebView:theWebView withViewController:theViewController];
        self.settings = theSettings;
    }
    return self;
}

- (id)initWithWebView:(UIWebView *)theWebView withViewController:(UIViewController *)theViewController {
    self.webView = theWebView;
    self.viewController = theViewController;
    
    return self;
}

- (void) dealloc {
    self.webView = nil;
    self.settings = nil;
    self.viewController = nil;
    self.callbackId = nil;
    
    [super dealloc];
}

+ (NSString*) pathForResource:(NSString*)resourcePath {
    NSString *fullPath = nil;
    if([resourcePath hasPrefix:DOCUMENTS_SCHEME_PREFIX]) {
        fullPath = [[resourcePath stringByReplacingOccurrencesOfString:DOCUMENTS_SCHEME_PREFIX withString:[NSString stringWithFormat:@"%@/",[Shortcut applicationDocumentsDirectory]]] stringByStandardizingPath];
    } else {
#if 1   // Changes to bundle resources to Documents folder/
        fullPath = [[Shortcut wwwPath] stringByAppendingPathComponent:resourcePath];
        if(![[NSFileManager defaultManager] fileExistsAtPath:fullPath]) {
            NSBundle * mainBundle = [NSBundle mainBundle];
            NSString *filename = [[NSMutableArray arrayWithArray:[resourcePath componentsSeparatedByString:@"/"]] lastObject];
            
            fullPath = [mainBundle pathForResource:filename ofType:@""];
        }
#else
        NSBundle * mainBundle = [NSBundle mainBundle];
        NSMutableArray *directoryParts = [NSMutableArray arrayWithArray:[resourcePath componentsSeparatedByString:@"/"]];
        NSString *filename = [directoryParts lastObject];
        [directoryParts removeLastObject];
        
        NSString* directoryPartsJoined =[directoryParts componentsJoinedByString:@"/"];
        NSString* directoryStr = WWW_FOLDER_NAME;
        
        if ([directoryPartsJoined length] > 0) {
            directoryStr = [NSString stringWithFormat:@"%@/%@", WWW_FOLDER_NAME, [directoryParts componentsJoinedByString:@"/"]];
        }
        
        fullPath = [mainBundle pathForResource:filename ofType:@"" inDirectory:directoryStr];
#endif
        
        if (fullPath == nil) {
            fullPath = [[Shortcut applicationDocumentsDirectory] stringByAppendingPathComponent:resourcePath];
        }
    }
    
    return fullPath;
}

// Maps a url for a resource path
// "Naked" resource paths are assumed to be from the www folder as its base
+ (NSURL*) urlForResource:(NSString*)resourcePath {
	NSURL* resourceURL = nil;
    NSString* fullPath = nil;
	
    // first try to find HTTP:// or Documents:// resources
    if ([resourcePath hasPrefix:HTTP_SCHEME_PREFIX]){
        resourceURL = [NSURL URLWithString:resourcePath];
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:resourceURL];
        [request setHTTPMethod:@"HEAD"];
        NSURLResponse *response = nil;
        NSError *error = nil;
        
        NSData *data = [NSURLConnection sendSynchronousRequest:request returningResponse:&response error:&error];
        
        if(!(data != nil && response != nil && error == nil)) {
            resourceURL = nil;
        }
    } else if ([resourcePath hasPrefix:FILE_SCHEME_PREFIX]){
        resourceURL = [NSURL URLWithString:resourcePath];
    } else {
        fullPath = [DynamicAppPlugin pathForResource:resourcePath];
    }
    
    // check that file exists for all but HTTP_SHEME_PREFIX
    if(fullPath != nil) {
        // try to access file
        NSFileManager* fMgr = [[NSFileManager alloc] init];
        if (![fMgr fileExistsAtPath:fullPath]) {
            resourceURL = nil;
        } else {
            // it's a valid file url, use it
            resourceURL = [NSURL fileURLWithPath:fullPath];
        }
        [fMgr release];
    }
    
	return resourceURL;
}

@end
