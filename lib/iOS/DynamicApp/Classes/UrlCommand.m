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

#import "UrlCommand.h"

@implementation UrlCommand

@synthesize arguments;
@synthesize options;
@synthesize className;
@synthesize methodName;

+ (UrlCommand*) commandFromURL:(NSURL *)url {
    UrlCommand  *urlCommand = [[UrlCommand alloc] init];
    NSArray *hostComponents = [[url host] componentsSeparatedByString:@"."];
    urlCommand.className = [hostComponents objectAtIndex:0];
    urlCommand.methodName = [hostComponents objectAtIndex:1];
    urlCommand.options = [self parseQueryString:[url query]];

    NSString *jsonString = [[[url path] stringByReplacingCharactersInRange:NSMakeRange(0, 1) withString:@""]
                            stringByReplacingOccurrencesOfString:@"/\"" withString:@"\\\""];
    
    jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\%" withString:@"%%"];

    NSData *data = [jsonString dataUsingEncoding:NSUTF8StringEncoding];
    
    urlCommand.arguments = [NSJSONSerialization JSONObjectWithData:data
                                                           options:NSJSONReadingMutableContainers
                                                            error:nil];
    return [urlCommand autorelease];
}

+ (NSMutableDictionary *)parseQueryString:(NSString *)query 
{
    NSMutableDictionary *dict = [[[NSMutableDictionary alloc] initWithCapacity:6] autorelease];
    NSArray *pairs = [query componentsSeparatedByString:@"&"];
    
    for (NSString *pair in pairs) {
        NSArray *elements = [pair componentsSeparatedByString:@"="];
        NSString *key = [[elements objectAtIndex:0] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        NSString *val = [[elements objectAtIndex:1] stringByReplacingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
        
        [dict setObject:val forKey:key];
    }
    
    return dict;
}


- (void) dealloc
{
    self.arguments = nil;
    self.options = nil;
    self.className = nil;
    self.methodName = nil;
    
    [super dealloc];
}


@end
