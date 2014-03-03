//
//  DynamicAppDelegate.h
//  DynamicApp
//
//  Created by ZYYX on 12/01/16.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@class DynamicAppViewController;

@interface DynamicAppDelegate : NSObject <UIApplicationDelegate>

@property (nonatomic, retain) IBOutlet UIWindow *window;
@property (nonatomic, retain) DynamicAppViewController *viewController;
@end
