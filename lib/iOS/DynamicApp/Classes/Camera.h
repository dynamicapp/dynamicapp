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
