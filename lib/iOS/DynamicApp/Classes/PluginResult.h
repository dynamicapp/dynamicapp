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
