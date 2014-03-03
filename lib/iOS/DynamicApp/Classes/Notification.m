//
//  Notification.m
//  DynamicApp
//
//  Created by ZYYX on 3/8/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "Notification.h"
#import "PluginResult.h"

@implementation Notification

- (void)notify:(NSDictionary *)arguments withOptions:(NSDictionary *)options {    
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSDate *date = [dateFormatter dateFromString:[arguments objectForKey:@"date"]];
    [dateFormatter release];
    PluginResult *pluginResult = nil;
    NSString *jsString = nil;
    NSString *theCallbackId = [options objectForKey:@"callbackId"];

    
    NSArray *localNotifications = [[UIApplication sharedApplication] scheduledLocalNotifications];
    for (UILocalNotification *notification in localNotifications) {
        if([notification.fireDate isEqualToDate:date]) {
            [[UIApplication sharedApplication] cancelLocalNotification:notification];
            break;
        }
    }
    
    UILocalNotification *localNotification = [[UILocalNotification alloc] init];
    localNotification.fireDate = date;
    localNotification.timeZone = [NSTimeZone defaultTimeZone];
    localNotification.alertAction = [arguments objectForKey:@"action"];
    localNotification.alertBody = [[arguments objectForKey:@"message"] stringByReplacingOccurrencesOfString:@"/n" withString:@"\n"];
    localNotification.soundName = UILocalNotificationDefaultSoundName;
    localNotification.applicationIconBadgeNumber = [[arguments objectForKey:@"badge"] intValue];
    localNotification.hasAction = [[arguments objectForKey:@"hasAction"] boolValue];
    [[UIApplication sharedApplication] scheduleLocalNotification:localNotification];
    [localNotification release];
    
    localNotifications = [[UIApplication sharedApplication] scheduledLocalNotifications];
    for (UILocalNotification *notification in localNotifications) {
        if([notification.fireDate isEqualToDate:date]) {
            pluginResult = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
            jsString = [pluginResult toSuccessCallbackString:theCallbackId];
            break;
        }
    }
    
    if(!jsString) {
        pluginResult = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        jsString = [pluginResult toErrorCallbackString:theCallbackId];
    }
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)cancelNotification:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    NSDate *date = [dateFormatter dateFromString:[arguments objectForKey:@"date"]];
    [dateFormatter release];
    
    PluginResult *pluginResult = nil;
    NSString *jsString = nil;
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    
    NSArray *localNotifications = [[UIApplication sharedApplication] scheduledLocalNotifications];
    for (UILocalNotification *notification in localNotifications) {
        if([notification.fireDate isEqualToDate:date]) {
            [[UIApplication sharedApplication] cancelLocalNotification:notification];
            pluginResult = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
            jsString = [pluginResult toSuccessCallbackString:theCallbackId];
            break;
        }
    }
    
    if(!jsString) {
        pluginResult = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        jsString = [pluginResult toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) dealloc {
    [super dealloc];
}

+ (void) handleReceivedNotification:(UILocalNotification*)thisNotification {
    thisNotification.applicationIconBadgeNumber = thisNotification.applicationIconBadgeNumber-1;
}

@end
