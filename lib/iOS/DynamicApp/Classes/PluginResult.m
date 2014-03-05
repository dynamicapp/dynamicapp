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

#import "PluginResult.h"


@interface PluginResult(Private)
-(id)initWithStatus:(DynamicAppCommandStatus)statusOrdinal message:(id)theMessage cast:(NSString*)theCast;
@end


@implementation PluginResult

@synthesize status, message, keepCallback, cast;

static NSArray* com_dynamicApp_CommandStatusMsgs;


+(void) initialize {
	com_dynamicApp_CommandStatusMsgs = [[NSArray alloc] initWithObjects: @"No result",
									  @"OK",
									  @"Class not found",
									  @"Illegal access",
									  @"Instantiation error",
									  @"Malformed url",
									  @"IO error",
									  @"Invalid action",
									  @"JSON error",
									  @"Error",
									  nil];
}

- (id)init {
    return [self initWithStatus:DynamicAppCommandStatus_NO_RESULT message:nil cast:nil];
}

- (id) initWithStatus:(DynamicAppCommandStatus)statusOrdinal message:(id)theMessage cast:(NSString*)theCast {
	if([super init]) {
		status = [NSNumber numberWithInt:statusOrdinal];
        [status retain];
		message = theMessage;
        [message retain];
		cast = theCast;
        [cast retain];
		keepCallback = [NSNumber numberWithBool:NO];
        [keepCallback retain];
	}
	return self;
}

+ (void) releaseStatus {
	if (com_dynamicApp_CommandStatusMsgs != nil){
		[com_dynamicApp_CommandStatusMsgs release];
		com_dynamicApp_CommandStatusMsgs = nil;
	}
}

+ (PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal {
	return [[[self alloc] initWithStatus:statusOrdinal message:[com_dynamicApp_CommandStatusMsgs objectAtIndex:statusOrdinal] cast:nil] autorelease];
}

+ (PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsString:(NSString*)theMessage {
	return [[[self alloc] initWithStatus:statusOrdinal message:theMessage cast:@""] autorelease];
}

+(PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsInt:(int)theMessage {
	return [[[self alloc] initWithStatus: statusOrdinal message: [NSNumber numberWithInt: theMessage] cast:nil] autorelease];
}

+(PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsDouble:(double)theMessage {
	return [[[self alloc] initWithStatus: statusOrdinal message: [NSNumber numberWithDouble: theMessage] cast:nil] autorelease];
}

+(PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsDictionary:(NSDictionary*)theMessage {
	return [[[self alloc] initWithStatus: statusOrdinal message: theMessage cast:nil] autorelease];
}

+(PluginResult*) resultWithStatus:(DynamicAppCommandStatus)statusOrdinal messageAsArray:(NSArray*)theMessage {
	return [[[self alloc] initWithStatus: statusOrdinal message: theMessage cast:nil] autorelease];
}

-(NSString*) toJSONString {
    NSError *error = nil;
    NSDictionary *jsonObject = [NSDictionary dictionaryWithObjectsAndKeys:
                                    self.status, @"status",
                                    self.message ? self.message : [NSNull null], @"message",
                                    self.keepCallback, @"keepCallback",
                                    nil];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonObject options:0 error:&error];
    NSString* resultString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
	return [resultString autorelease];
}

- (NSString*) toSuccessCallbackString:(NSString*)callbackId {
	NSString* successCB;
	
	if ([[self cast] length] > 0) {
		successCB = [NSString stringWithFormat: @"var temp = %@(%@);\nDynamicApp.callbackSuccess('%@',temp);", self.cast, [self toJSONString], callbackId];
	}
	else {
		successCB = [NSString stringWithFormat:@"DynamicApp.callbackSuccess('%@',%@);", callbackId, [self toJSONString]];
	}
    
	return successCB;
}

- (NSString*) toErrorCallbackString:(NSString*)callbackId {
    {
        NSString* errorCB = nil;
        
        if ([self cast] != nil) {
            errorCB = [NSString stringWithFormat: @"var temp = %@(%@);\nDynamicApp.callbackError('%@',temp);", self.cast, [self toJSONString], callbackId];
        }
        else {
            errorCB = [NSString stringWithFormat:@"DynamicApp.callbackError('%@',%@);", callbackId, [self toJSONString]];
        }
        
        return errorCB;
    }
}

- (void) dealloc {
	[status release];
	[message release];
	[keepCallback release];
	[cast release];
	
	[super dealloc];    
}


@end
