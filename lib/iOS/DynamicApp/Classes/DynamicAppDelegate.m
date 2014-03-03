//
//  DynamicAppDelegate.m
//  DynamicApp
//
//  Created by ZYYX on 12/01/16.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppDelegate.h"
#import "DynamicAppViewController.h"
#import "Shortcut.h"
#import "define.h"
#import "Notification.h"

@interface DynamicAppDelegate()
- (BOOL)copyWWWFolder2DocumentsFolder;
- (BOOL)copyMediaResourcesToDocumentsFolder;
- (void)receivedOrientationChange:(NSNotification *)notification;
@end

@implementation DynamicAppDelegate

@synthesize window;
@synthesize viewController;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    [[UIApplication sharedApplication] setStatusBarHidden:YES withAnimation:UIStatusBarAnimationFade];

    if(![self copyWWWFolder2DocumentsFolder]) {
        abort();
    }
    
    if(![self copyMediaResourcesToDocumentsFolder]) {
        abort();
    }

    UILocalNotification *localNotification = [launchOptions objectForKey:UIApplicationLaunchOptionsLocalNotificationKey];
	
    if (localNotification) {
		[Notification handleReceivedNotification:localNotification];
    }

    [[UIDevice currentDevice] beginGeneratingDeviceOrientationNotifications];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receivedOrientationChange:) name:UIDeviceOrientationDidChangeNotification
                                               object:nil];
    
    return YES;
}

- (void)applicationDidReceiveMemoryWarning:(UIApplication *)application {
    
}

- (void)applicationWillTerminate:(UIApplication *)application {
    
}

- (void)dealloc {
    self.viewController = nil;
    self.window = nil;
    
    [super dealloc];
}

- (BOOL)copyWWWFolder2DocumentsFolder {
    NSString *filePath = [Shortcut wwwPath];
    
    NSBundle * mainBundle = [NSBundle mainBundle];
    
    NSString *resourcePath = [mainBundle pathForResource:WWW_FOLDER_NAME ofType:@""];
    
    if(!resourcePath) {
        return NO;
    }
    
    NSString *appVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"];
    NSError *error;
    NSFileManager *manager = [NSFileManager defaultManager];
    
    if([manager fileExistsAtPath:filePath]) {
#ifndef DEBUG
        NSString *currentVersion = [[NSUserDefaults standardUserDefaults] stringForKey:CURRENT_VERSION_KEY];
        if(currentVersion != nil && [currentVersion compare:appVersion options:NSNumericSearch] == NSOrderedSame) {
            return YES;
        }
#endif
        [manager removeItemAtPath:filePath error:&error];
    }
    
    [manager copyItemAtPath:resourcePath toPath:filePath error:&error];
    [[NSUserDefaults standardUserDefaults] setObject:appVersion forKey:CURRENT_VERSION_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    return YES;
}

- (BOOL)copyMediaResourcesToDocumentsFolder {
    NSString *filePath = [Shortcut mediaResourcesPath];
    
    NSFileManager *manager = [NSFileManager defaultManager];
    if([manager fileExistsAtPath:filePath]) {
        [manager removeItemAtPath:filePath error:nil];
    }
    
    NSBundle * mainBundle = [NSBundle mainBundle];
    
    NSString *resourcePath = [mainBundle pathForResource:RESOURCES_FOLDER_NAME ofType:@""];
    
    if(!resourcePath) {
        return YES;
    }
    
    NSError *error;
    [manager copyItemAtPath:resourcePath toPath:filePath error:&error];
    
    return YES;
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
    [Notification handleReceivedNotification:notification];
}

- (void)receivedOrientationChange:(NSNotification *)notification {
    UIDeviceOrientation orientation = [[notification object] orientation];
    
    if(self.viewController != nil && [[self.viewController.view subviews] count] > 0) {
        UIView *aView = (UIView*)[[self.viewController.view subviews] objectAtIndex:0];
        if(aView != nil && [aView isKindOfClass:[UIWebView class]]) {
            if (orientation == UIDeviceOrientationLandscapeLeft) {
                
            } else if (orientation == UIDeviceOrientationLandscapeRight) {
                
            } else if (orientation == UIDeviceOrientationPortraitUpsideDown) {
            } else if (orientation == UIDeviceOrientationPortrait) {
            }    
        }
    }
}

@end
