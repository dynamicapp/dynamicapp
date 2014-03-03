//
//  Ad.m
//  DynamicApp
//
//  Created by Zyyx on 11/27/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "Ad.h"
#import "PluginResult.h"
#import "ZXUtility.h"

@interface Ad()
@property (nonatomic) BOOL isLoading;
- (void)receivedOrientationChange:(NSNotification *)notification;
@end

@implementation Ad

@synthesize adBannerView, position;
@synthesize isLoading;

- (void)create:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    if(self.adBannerView == nil) {
        CGRect adBannerFrame = CGRectZero;
        UIInterfaceOrientation orientation = [[UIDevice currentDevice] orientation];
        if(UIInterfaceOrientationIsLandscape(orientation)) {
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
            if ([ZXUtility deviceIs_iPad]) {
                adBannerFrame.size = CGSizeMake(1024, 66);
            } else {
                adBannerFrame.size = CGSizeMake(480, 32);
            }
#else
            adBannerFrame.size = [ADBannerView sizeFromBannerContentSizeIdentifier:ADBannerContentSizeIdentifierLandscape];
#endif
        } else {
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
            if ([ZXUtility deviceIs_iPad]) {
                adBannerFrame.size = CGSizeMake(768, 66);
            } else {
                adBannerFrame.size = CGSizeMake(320, 50);
            }
#else
            adBannerFrame.size = [ADBannerView sizeFromBannerContentSizeIdentifier:ADBannerContentSizeIdentifierPortrait];
#endif
        }

        self.position = [[arguments objectForKey:@"position"] intValue];
        self.adBannerView = [[[ADBannerView alloc] initWithFrame:adBannerFrame] autorelease];
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
        self.adBannerView.autoresizingMask = UIViewAutoresizingFlexibleWidth;
#else
        self.adBannerView.requiredContentSizeIdentifiers = [NSSet setWithObjects: ADBannerContentSizeIdentifierPortrait, ADBannerContentSizeIdentifierLandscape, nil];
        self.adBannerView.currentContentSizeIdentifier = ADBannerContentSizeIdentifierPortrait;
#endif

        self.adBannerView.delegate = self;
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(receivedOrientationChange:) name:UIDeviceOrientationDidChangeNotification
                                               object:nil];
        
        self.callbackId = [options objectForKey:@"callbackId"];
            
        [self setPosition];
        [self.viewController.view addSubview:self.adBannerView];
        
        self.isLoading = YES;
        
#if 0
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
        result.keepCallback = [NSNumber numberWithInt:1];
        
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
#endif
    }
}

- (void)show:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    NSString *jsString = nil;
    if(self.adBannerView != nil && self.adBannerView.isHidden) {
        [self.adBannerView setHidden:NO];
        
        [self setPosition];
        
        jsString = [[PluginResult resultWithStatus:DynamicAppCommandStatus_OK] toSuccessCallbackString:[options objectForKey:@"callbackId"]];
    } else {
        jsString = [[PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR] toErrorCallbackString:[options objectForKey:@"callbackId"]];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)hide:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    NSString *jsString = nil;
    
    if(self.adBannerView != nil && !self.adBannerView.isHidden) {
        [self.adBannerView setHidden:YES];
        
        [self setPosition];    
        
        jsString = [[PluginResult resultWithStatus:DynamicAppCommandStatus_OK] toSuccessCallbackString:[options objectForKey:@"callbackId"]];
    } else {
        jsString = [[PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR] toErrorCallbackString:[options objectForKey:@"callbackId"]];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)bannerViewDidLoadAd:(ADBannerView *)banner {
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
    result.keepCallback = [NSNumber numberWithInt:1];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
}

- (void)bannerView:(ADBannerView *)banner didFailToReceiveAdWithError:(NSError *)error {
    if(self.isLoading) {
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        result.keepCallback = [NSNumber numberWithInt:1];
        [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        self.isLoading = NO;
    }
}
                                               
- (void)receivedOrientationChange:(NSNotification *)notification {
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
#else
    UIDeviceOrientation orientation = [[notification object] orientation];
    
    if(UIDeviceOrientationIsLandscape(orientation)) {
        self.adBannerView.currentContentSizeIdentifier = ADBannerContentSizeIdentifierLandscape;
    } else if(UIDeviceOrientationIsPortrait(orientation)) {
        self.adBannerView.currentContentSizeIdentifier = ADBannerContentSizeIdentifierPortrait;
    }
#endif
    [self setPosition];
}

- (void)setPosition {
    CGRect webViewFrame = [self.viewController.view bounds];
    CGRect adBannerFrame = self.adBannerView.frame;
    if(!self.adBannerView.isHidden) {
        if(self.position == POSITION_TOP) {
            adBannerFrame.origin = CGPointZero;
            webViewFrame.origin.y = self.adBannerView.frame.size.height;
            webViewFrame.size.height -= self.adBannerView.frame.size.height;
        } else {
            webViewFrame.size.height -= self.adBannerView.frame.size.height;
            adBannerFrame.origin = CGPointMake(0.0, webViewFrame.size.height);
        }
    }
    self.webView.frame = webViewFrame;
    self.adBannerView.frame = adBannerFrame;
}

- (void)dealloc {
    self.adBannerView = nil;
    
    [[NSNotificationCenter defaultCenter] removeObserver:self];
    
    [super dealloc];
}

@end
