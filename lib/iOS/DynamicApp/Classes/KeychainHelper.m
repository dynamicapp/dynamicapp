//
//  KeychainHelper.m
//  DynamicApp
//
//  Created by zyyx on 2013/12/24.
//  Copyright (c) 2013年 zyyx. All rights reserved.
//

#import "KeychainHelper.h"

@interface KeychainHelper()
+ (NSMutableDictionary *)baseQuery;
+ (NSMutableDictionary *)baseDeleteQuery;
+ (NSMutableDictionary *)baseAttributes;
@end


@implementation KeychainHelper

+ (KeychainHelper *)defaultHelper {
    static KeychainHelper *obj = nil;
    if (!obj) {
        obj = [[[KeychainHelper alloc] init] autorelease];
    }
    return obj;
}

+ (NSMutableDictionary *)baseQuery {
    NSMutableDictionary *aBase = [self baseAttributes];
    [aBase setObject:(__bridge id)kSecMatchLimitOne forKey:(__bridge id)kSecMatchLimit];
    [aBase setObject:(__bridge id)kCFBooleanTrue forKey:(__bridge id)kSecReturnData];
    return aBase;
}

+ (NSMutableDictionary *)baseDeleteQuery {
    return [self baseAttributes];
}

+ (NSMutableDictionary *)baseAttributes {
    return [[[NSMutableDictionary alloc] initWithObjectsAndKeys:
            (__bridge id)kSecClassGenericPassword, (__bridge id)kSecClass,
            (__bridge id)kSecAttrAccessibleAlwaysThisDeviceOnly, (__bridge id)kSecAttrAccessible,
            nil] autorelease];
}

- (NSString *)stringForKey:(NSString *)aKey {
    @synchronized([self class]) {
        NSMutableDictionary *q = [[self class] baseQuery];
        [q setObject:aKey forKey:(__bridge id)kSecAttrAccount];
        CFTypeRef value = NULL;
        OSStatus r = SecItemCopyMatching((__bridge CFDictionaryRef)q, &value);
        if (r == errSecSuccess) {
            NSData *aData = (NSData *)value;
            return [[[NSString alloc] initWithData:aData encoding:NSUTF8StringEncoding] autorelease];
        } else if (r == errSecItemNotFound) {
            return nil;
        } else {
            return nil;
        }
    }
}

- (void)setString:(NSString *)aString forKey:(NSString *)aKey {
    @synchronized([self class]) {
        [self removeObjectForKey:aKey];
        if (aString) {
            NSMutableDictionary *q = [[self class] baseAttributes];
            [q setObject:aKey forKey:(__bridge id)kSecAttrAccount];
            [q setObject:[aString dataUsingEncoding:NSUTF8StringEncoding]
                  forKey:(__bridge id)kSecValueData];
            OSStatus r = SecItemAdd((__bridge CFDictionaryRef)q, NULL);
            if (r != errSecSuccess) {
            }
        }
    }
}

- (void)removeObjectForKey:(NSString *)aKey {
    @synchronized([self class]) {
        NSMutableDictionary *q = [[self class] baseDeleteQuery];
        [q setObject:aKey forKey:(__bridge id)kSecAttrAccount];
        OSStatus r = SecItemDelete((__bridge CFDictionaryRef)q);
        if (r != errSecSuccess && r != errSecItemNotFound) {
        }
    }
}

@end
