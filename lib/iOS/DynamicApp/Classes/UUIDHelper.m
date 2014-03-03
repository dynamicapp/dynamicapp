//
//  UUIDHelper.m
//  DynamicApp
//
//  Created by zyyx on 2013/12/24.
//  Copyright (c) 2013年 zyyx. All rights reserved.
//

#import "UUIDHelper.h"
#import "KeychainHelper.h"


NSString * const UUIDHelperKeychainKey = @"UUIDHelperKeychainKey";


@implementation UUIDHelper

static NSString *CurrentUUID = nil;

+ (NSString *)defaultUUID {
    if (!CurrentUUID) {
        KeychainHelper *h = [KeychainHelper defaultHelper];
        NSString *s = [h stringForKey:UUIDHelperKeychainKey];
        if (!s) {
            s = [self overwriteStoredByGenerateUUID];
        }
        CurrentUUID = s;
    }
    return CurrentUUID;
}

+ (NSString *)generateUUID {
    CFUUIDRef uuid = CFUUIDCreate(NULL);
    CFStringRef uuidString = CFUUIDCreateString(NULL, uuid);
    NSString *aResult = [[[NSString alloc] initWithString:(__bridge NSString *)uuidString] autorelease];
    CFRelease(uuidString);
    CFRelease(uuid);
    return aResult;
}

+ (NSString *)overwriteStoredByGenerateUUID {
    KeychainHelper *h = [KeychainHelper defaultHelper];
    NSString *s = [self generateUUID];
    [h setString:s forKey:UUIDHelperKeychainKey];
    CurrentUUID = s;
    return s;
}

@end
