//
//  Contacts.h
//  DynamicApp
//
//  Created by ZYYX on 9/11/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"

@interface Contacts : DynamicAppPlugin

- (void) save:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) remove:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) search:(NSDictionary *)arguments withOptions:(NSDictionary *)options;

@end
