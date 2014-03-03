//
//  Shortcut.h
//  DynamicApp
//
//  Created by ZYYX on 11/03/18.
//  Copyright 2012 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "DynamicAppDelegate.h"
#import "define.h"

@interface Shortcut : NSObject {

}

+ (DynamicAppDelegate *)appDelegate;
+ (void)alert:(NSString *)message;
+ (NSString *) wwwPath;
+ (NSString *) mediaResourcesPath;
+ (NSString*) applicationDocumentsDirectory;

@end
