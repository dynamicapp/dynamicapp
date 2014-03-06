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
