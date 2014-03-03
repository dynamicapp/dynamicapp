//
//  PluginResult.h
//  DynamicApp
//
//  Created by ZYYX on 1/27/12.
//  Copyright 2012 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

typedef enum {
	DynamicAppCommandStatus_NO_RESULT = 0,
	DynamicAppCommandStatus_OK,
	DynamicAppCommandStatus_CLASS_NOT_FOUND_EXCEPTION,
	DynamicAppCommandStatus_ILLEGAL_ACCESS_EXCEPTION,
	DynamicAppCommandStatus_INSTANTIATION_EXCEPTION,
	DynamicAppCommandStatus_MALFORMED_URL_EXCEPTION,
	DynamicAppCommandStatus_IO_EXCEPTION,
	DynamicAppCommandStatus_INVALID_ACTION,
	DynamicAppCommandStatus_JSON_EXCEPTION,
	DynamicAppCommandStatus_ERROR
} DynamicAppCommandStatus;

@interface PluginResult : NSObject {
	NSNumber* status;
	id message;
	NSNumber* keepCallback;
	NSString* cast;
	
}

@property (nonatomic, retain, readonly) NSNumber* status;
@property (nonatomic, retain, readonly) id message;
@property (nonatomic, retain)			NSNumber* keepCallback;
@property (nonatomic, retain, readonly) NSString* cast;


- (id)init;
+ (void)releaseStatus;
+ (PluginResult*)resultWithStatus:(DynamicAppCommandStatus)statusOrdinal;
+ (PluginResult*)resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsString:(NSString*)theMessage;
+(PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsInt:(int)theMessage;
+(PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsDouble:(double)theMessage;
+(PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsDictionary:(NSDictionary*)theMessage;
+(PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsArray:(NSArray*)theMessage;

- (NSString*)toSuccessCallbackString:(NSString*)callbackId;
- (NSString*)toErrorCallbackString:(NSString*)callbackId;

- (void) dealloc;



@end
