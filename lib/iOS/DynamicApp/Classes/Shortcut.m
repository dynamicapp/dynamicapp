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

#import "Shortcut.h"

@implementation Shortcut

+ (DynamicAppDelegate *)appDelegate {
    return (DynamicAppDelegate *)[UIApplication sharedApplication].delegate;
}

+ (void)alert:(NSString *)message {    
    UIAlertView *alert = [[UIAlertView alloc] init];
    [alert addButtonWithTitle:@"OK"];
    alert.message = message;
    [alert show];
    [alert release];
}

+ (NSString *) applicationDocumentsDirectory {
    
    NSArray *paths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *basePath = ([paths count] > 0) ? [paths objectAtIndex:0] : nil;
    return basePath;
}

+ (NSString *) wwwPath {
    return [[Shortcut applicationDocumentsDirectory] stringByAppendingPathComponent:WWW_FOLDER_NAME];
}

+ (NSString *) mediaResourcesPath {
    return [[Shortcut applicationDocumentsDirectory] stringByAppendingPathComponent:RESOURCES_FOLDER_NAME];
}

@end
