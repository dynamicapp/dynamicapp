//
//  Shortcut.m
//  DynamicApp
//
//  Created by ZYYX on 11/03/18.
//  Copyright 2012 ZYYX Inc. All rights reserved.
//

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
