//
//  DynamicAppViewController.h
//  DynamicApp
//
//  Created by ZYYX on 12/01/16.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface DynamicAppViewController : UIViewController <UIWebViewDelegate>

@property (nonatomic, retain) UIWebView *webView;
@property (nonatomic, retain) NSArray *supportedOrientations;
@end
