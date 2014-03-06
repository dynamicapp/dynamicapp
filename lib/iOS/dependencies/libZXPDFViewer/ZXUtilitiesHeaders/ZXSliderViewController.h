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

#import <UIKit/UIKit.h>


@class ZXSliderViewController;

@protocol ZXSliderViewControllerDelegate <NSObject>
- (void)slider:(ZXSliderViewController *)aController didChangeValue:(float)value;
@end



@interface ZXSliderViewController : UIViewController {
    float value;
    float minimumValue;
    float maximumValue;
    UISlider *slider;
    UITextField *valueField;
    NSString *valueFieldFormat;
    id<ZXSliderViewControllerDelegate> __unsafe_unretained delegate;
}

@property (nonatomic, strong) IBOutlet UISlider *slider;
@property (nonatomic, strong) IBOutlet UITextField *valueField;
@property (nonatomic, copy) NSString *valueFieldFormat; // format string for valueField.text using slider.value. default is @"%.1f"
@property (nonatomic, unsafe_unretained) IBOutlet id<ZXSliderViewControllerDelegate> delegate;

// forwarding to slider when setting.
@property (nonatomic) float value;
@property (nonatomic) float minimumValue;
@property (nonatomic) float maximumValue;

- (void)synchronizeValue;

@end
