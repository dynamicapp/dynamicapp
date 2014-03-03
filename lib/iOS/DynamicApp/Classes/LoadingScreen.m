//
//  LoadingScreen.m
//  DynamicApp
//
//  Created by ZYYX on 3/15/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "LoadingScreen.h"
#import "PluginResult.h"

@implementation LoadingScreen

@synthesize loadingView;
@synthesize activityIndicator;
@synthesize loadingLabel;

- (void)startLoad:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    PluginResult *result = nil;
    NSString *jsString = nil;
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    
    if(self.loadingView) {
        [self.activityIndicator removeFromSuperview];
        self.activityIndicator = nil;
        [self.loadingLabel removeFromSuperview];
        self.loadingLabel = nil;
        [self.loadingView removeFromSuperview];
        self.loadingView = nil;
    }
    
    CGRect frame = self.webView.frame;
    NSDictionary *argsFrame = [arguments objectForKey:@"frame"];
    if(argsFrame) {
        frame = CGRectMake([[argsFrame objectForKey:@"x"] floatValue], [[argsFrame objectForKey:@"y"] floatValue], [[argsFrame objectForKey:@"width"] floatValue], [[argsFrame objectForKey:@"height"] floatValue]);
    }
    
    UIColor *bgColor = [UIColor blackColor];
    if([arguments objectForKey:@"bgColor"] && [[arguments objectForKey:@"bgColor"] isEqualToString:@"clear"]) {
        bgColor = [UIColor clearColor];
    }
    
    self.loadingView = [[[UIView alloc] initWithFrame:frame] autorelease];
    self.loadingView.backgroundColor = bgColor;
    
    CGPoint point = self.loadingView.center;
    if(argsFrame) {
        point = CGPointMake(point.x - [[argsFrame objectForKey:@"x"] floatValue], 
                            point.y - [[argsFrame objectForKey:@"y"] floatValue]);
    }
    
    self.activityIndicator = [[[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:(UIActivityIndicatorViewStyle)[[arguments objectForKey:@"style"] intValue]] autorelease];
    self.activityIndicator.frame = CGRectMake(0, 0, 50.0, 50.0);
    self.activityIndicator.center = point;
    [self.loadingView addSubview:self.activityIndicator];
    
    self.loadingLabel = [[[UILabel alloc] initWithFrame:CGRectMake(0, 0, frame.size.width, 50.0)] autorelease];
	self.loadingLabel.text = [arguments objectForKey:@"label"];
	self.loadingLabel.textColor = [UIColor whiteColor];
	self.loadingLabel.backgroundColor = [UIColor clearColor];
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
	self.loadingLabel.textAlignment = NSTextAlignmentCenter;
#else
	self.loadingLabel.textAlignment = UITextAlignmentCenter;
#endif
	self.loadingLabel.font = [UIFont boldSystemFontOfSize:[UIFont labelFontSize]];
	[self.loadingView addSubview:self.loadingLabel];
    
    CGRect rect = self.loadingLabel.frame;
    rect.origin.y = point.y + 50;
    self.loadingLabel.frame = rect;
    
    if(argsFrame) {
        [[self.webView.subviews objectAtIndex:0] addSubview:self.loadingView];
    } else {
        [self.webView addSubview:self.loadingView];
    }
    
    //[UIApplication sharedApplication].networkActivityIndicatorVisible = YES;
    [self.activityIndicator startAnimating];
    
    if([self.activityIndicator isAnimating]) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
        jsString = [result toSuccessCallbackString:theCallbackId];
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        jsString = [result toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)stopLoad:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    PluginResult *result = nil;
    NSString *jsString = nil;
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    
    if(self.activityIndicator && [self.activityIndicator isAnimating]) {
        //[UIApplication sharedApplication].networkActivityIndicatorVisible = NO;
        [self.activityIndicator stopAnimating];
        
        if(![self.activityIndicator isAnimating]) {
            [self.activityIndicator removeFromSuperview];
            //[self.activityIndicator release];
            self.activityIndicator = nil;
            
            [self.loadingLabel removeFromSuperview];
            //[self.loadingLabel release];
            self.loadingLabel = nil;
            
            [self.loadingView removeFromSuperview];
            //[self.loadingView release];
            self.loadingView = nil;
            
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
            jsString = [result toSuccessCallbackString:theCallbackId];
        } else {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            jsString = [result toErrorCallbackString:theCallbackId];
        }
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) dealloc {
    self.activityIndicator = nil;
    self.loadingLabel = nil;
    self.loadingView = nil;
    
    [super dealloc];
}

@end
