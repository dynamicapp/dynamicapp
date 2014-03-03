//
//  UUIDHelper.h
//  DynamicApp
//
//  Created by zyyx on 2013/12/24.
//  Copyright (c) 2013年 zyyx. All rights reserved.
//

#import <Foundation/Foundation.h>

extern NSString * const UUIDHelperKeychainKey;

@interface UUIDHelper : NSObject

+ (NSString *)defaultUUID;

+ (NSString *)generateUUID;

// return newly generated UUID.
+ (NSString *)overwriteStoredByGenerateUUID;

@end
