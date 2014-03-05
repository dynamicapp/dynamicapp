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
