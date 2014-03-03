//
//  KeychainHelper.h
//  DynamicApp
//
//  Created by zyyx on 2013/12/24.
//  Copyright (c) 2013年 zyyx. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface KeychainHelper : NSObject

+ (KeychainHelper *)defaultHelper;

// get/set as kSecClassGenericPassword.
// string value is converted between NSString and NSData for kSecValueData using NSUTF8StringEncoding.
// key is used directly as kSecAttrAccount.
- (NSString *)stringForKey:(NSString *)aKey;
- (void)setString:(NSString *)aString forKey:(NSString *)aKey;
- (void)removeObjectForKey:(NSString *)aKey;

@end
