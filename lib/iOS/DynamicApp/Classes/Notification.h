//
//  Notification.h
//  DynamicApp
//
//  Created by ZYYX on 3/8/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"

@interface Notification : DynamicAppPlugin

- (void)notify:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void)cancelNotification:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
+ (void) handleReceivedNotification:(UILocalNotification*)thisNotification;

@end
