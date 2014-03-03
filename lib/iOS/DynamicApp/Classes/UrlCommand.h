//
//  UrlCommand.h
//  DynamicApp
//
//  Created by ZYYX on 1/26/12.
//  Copyright 2012 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface UrlCommand : NSObject {
	NSString* className;
	NSString* methodName;
	NSMutableDictionary* arguments;
	NSMutableDictionary* options;
}

@property(retain) NSMutableDictionary* arguments;
@property(retain) NSMutableDictionary* options;
@property(copy) NSString* className;
@property(copy) NSString* methodName;

+ (UrlCommand*) commandFromURL:(NSURL *)url;
+ (NSMutableDictionary *)parseQueryString:(NSString *)query; 
- (void) dealloc;

@end
