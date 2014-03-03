//
//  ZXSliderViewController.h
//  ZyyxLibraries
//
//  Created by gotow on 10/12/20.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

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
