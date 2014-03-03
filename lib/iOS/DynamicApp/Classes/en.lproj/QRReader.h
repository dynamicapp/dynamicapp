//
//  QRReader.h
//  DynamicApp
//
//  Created by ZYYX on 6/8/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"
#import <ZXingWidgetController.h>

@interface QRReader : DynamicAppPlugin <ZXingDelegate, UINavigationControllerDelegate, UIImagePickerControllerDelegate, UIActionSheetDelegate> {
    ZXingWidgetController *zXingWidgetController;
    ParsedResult *parsedResult;
}

@property (nonatomic, retain) ZXingWidgetController *zXingWidgetController;
@property (nonatomic, assign) ParsedResult *parsedResult;

- (void)zxingController:(ZXingWidgetController*)controller didScanResult:(NSString *)result;
- (void)zxingController:(ZXingWidgetController*)controller didScanFail:(NSString *)result;
- (void)zxingControllerDidCancel:(ZXingWidgetController*)controller;

- (void)scan:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void)executeQRCodeResult:(NSDictionary *)arguments withOptions:(NSDictionary *)options;

@end