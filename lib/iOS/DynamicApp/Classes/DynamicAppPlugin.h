//
//  DynamicAppPlugin.h
//  DynamicApp
//
//  Created by ZYYX on 1/18/12.
//  Copyright 2012 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>


@interface DynamicAppPlugin : NSObject {
    UIWebView *webView;
    NSDictionary *settings;
    UIViewController *viewController;
    NSString *callbackId;
}

@property (nonatomic, retain) UIWebView *webView;
@property (nonatomic, retain) NSDictionary *settings;
@property (nonatomic, retain) UIViewController *viewController;
@property (nonatomic, retain) NSString *callbackId;

- (id)initWithWebView:(UIWebView *)theWebView withSettings:(NSDictionary *)theSettings withViewController:(UIViewController *)theViewController;

- (id)initWithWebView:(UIWebView *)theWebView withViewController:(UIViewController *)theViewController;

+ (NSString*) pathForResource:(NSString*)resourcepath;
+ (NSURL*) urlForResource:(NSString*)resourcePath;

@end
