//
//  Ad.h
//  DynamicApp
//
//  Created by ZYYX Inc on 11/27/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"
#import <iAd/iAd.h>

enum IAdPosition {
	POSITION_TOP = 0,
	POSITION_BOTTOM = 1
};
typedef NSInteger IAdPosition;

@interface Ad : DynamicAppPlugin <ADBannerViewDelegate>

@property (nonatomic, retain) ADBannerView *adBannerView;
@property (nonatomic) IAdPosition position;

- (void)create:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void)show:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void)hide:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;

@end
