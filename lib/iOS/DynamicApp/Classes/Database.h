//
//  Database.h
//  DynamicApp
//
//  Created by ZYYX on 6/19/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"
#import <sqlite3.h>

@interface Database : DynamicAppPlugin {
    sqlite3 *databaseHandle;
}

@property (nonatomic, assign) sqlite3 *databaseHandle;

- (void)init:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void)executeSQL:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
@end
