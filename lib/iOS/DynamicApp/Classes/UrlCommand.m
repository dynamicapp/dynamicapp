//
//  UrlCommand.m
//  DynamicApp
//
//  Created by ZYYX on 1/26/12.
//  Copyright 2012 ZYYX Inc. All rights reserved.
//

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
