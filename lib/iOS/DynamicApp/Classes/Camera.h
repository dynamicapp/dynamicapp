//
//  Camera.h
//  DynamicApp
//
//  Created by ZYYX on 1/17/12.
//  Copyright 2012 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "DynamicAppPlugin.h"

typedef enum DestinationType {
	DestinationTypeDataUrl = 0,
	DestinationTypeFileUri
} DestinationType;


typedef enum EncodingType {
    EncodingTypeJPEG = 0,
    EncodingTypePNG
} EncodingType;

@interface Camera : DynamicAppPlugin <UINavigationControllerDelegate, UIImagePickerControllerDelegate> {
    UIImagePickerController *imagePicker;
    
    float quality;
    DestinationType destinationType;
    UIImagePickerControllerSourceType sourceType;
    EncodingType encodingType;
    CGSize targetSize;
}

@property (nonatomic, retain) UIImagePickerController *imagePicker;
@property (nonatomic) float quality;
@property (nonatomic) DestinationType destinationType;
@property (nonatomic) UIImagePickerControllerSourceType sourceType;
@property (nonatomic) EncodingType encodingType;
@property (nonatomic) CGSize targetSize;


- (void)getPicture:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void)recordVideo:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;

- (UIImage*)imageByScalingAndCroppingForSize:(UIImage*)anImage toSize:(CGSize)targetSize;

@end
