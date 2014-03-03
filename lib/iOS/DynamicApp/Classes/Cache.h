//
//  Cache.h
//  DynamicApp
//
//  Created by ZYYX on 3/16/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"

@interface ResourceCache : DynamicAppPlugin
- (void) add:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) remove:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
@end