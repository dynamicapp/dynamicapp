//
//  Encryptor.h
//  DynamicApp
//
//  Created by ZYYX on 3/1/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"

@interface Encryptor : DynamicAppPlugin

- (void)encryptText:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void)decryptText:(NSDictionary *)arguments withOptions:(NSDictionary *)options;

@end
