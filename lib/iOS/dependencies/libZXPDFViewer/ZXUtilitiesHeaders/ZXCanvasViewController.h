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
#import "ZXCanvasView.h"
#import "ZXDrawGestureRecognizer.h"


extern NSString * const ZXCanvasViewControllerPreferenceDrawWidthKey;
extern NSString * const ZXCanvasViewControllerPreferenceEraseWidthKey;
extern NSString * const ZXCanvasViewControllerPreferenceDrawColorKey;



@interface ZXCanvasViewController : UIViewController
<UIPopoverControllerDelegate, UIAlertViewDelegate, UIScrollViewDelegate, UIGestureRecognizerDelegate>
{
    UIScrollView *baseView;
    UIView *contentView;
    NSMutableArray *canvasData;
    ZXCanvasView *canvas;
    CGPoint startingPoint;
    ZXDrawGestureRecognizer *drawRecognizer;
    BOOL isDrawMode;
    BOOL isScrollMode;
    NSMutableArray *canvasDataForRedo;
    UITapGestureRecognizer *doubleTapRecognizer;
    UIPanGestureRecognizer *twoFingeredDragRecognizer;
    CGPoint baseOffset;

    CGFloat drawWidth;
    CGFloat eraseWidth;
    UIColor *drawColor;
    NSArray *colorList;

    UIToolbar *toolbarTop;
    UIToolbar *toolbarBottom;
    UIPopoverController *popoverController;
    UIBarButtonItem *undoButton;
    UIBarButtonItem *redoButton;
    UISegmentedControl *drawModeSelector;
    UIBarButtonItem *settingButton;
}

@property (nonatomic, strong) IBOutlet UIScrollView *baseView;
@property (nonatomic, strong) IBOutlet UIView *contentView;
@property (nonatomic, strong) NSMutableArray *canvasData; // alias self.canvas.plotLines
@property (nonatomic, strong) IBOutlet ZXCanvasView *canvas;
@property (nonatomic) CGPoint startingPoint;
@property (nonatomic, strong) ZXDrawGestureRecognizer *drawRecognizer;
@property (nonatomic) BOOL isDrawMode;
@property (nonatomic, strong) NSMutableArray *canvasDataForRedo;
@property (nonatomic, strong) UITapGestureRecognizer *doubleTapRecognizer DEPRECATED_ATTRIBUTE;
@property (nonatomic, strong) UIPanGestureRecognizer *twoFingeredDragRecognizer;
@property (nonatomic) CGPoint baseOffset;

@property (nonatomic) CGFloat drawWidth;
@property (nonatomic) CGFloat eraseWidth;
@property (nonatomic, strong) UIColor *drawColor;
@property (nonatomic, strong) NSArray *colorList; // must contain UIColor.

@property (nonatomic, strong) IBOutlet UIToolbar *toolbarTop;
@property (nonatomic, strong) IBOutlet UIToolbar *toolbarBottom;
@property (nonatomic, strong) UIPopoverController *popoverController;
@property (nonatomic, strong) IBOutlet UIBarButtonItem *undoButton;
@property (nonatomic, strong) IBOutlet UIBarButtonItem *redoButton;
@property (nonatomic, strong) IBOutlet UISegmentedControl *drawModeSelector;
@property (nonatomic, strong) IBOutlet UIBarButtonItem *settingButton;

@property (nonatomic, strong) UIAlertView *clearAllAlert;


- (void)drawAction:(ZXDrawGestureRecognizer *)aRecognizer;
- (void)doubleTapAction:(UITapGestureRecognizer *)aRecognizer DEPRECATED_ATTRIBUTE;
- (void)twoFingeredDragAction:(UIPanGestureRecognizer *)aRecognizer;

- (IBAction)changeDrawMode:(UISegmentedControl *)sender;
- (IBAction)presentDrawSettingInPopover:(UIBarButtonItem *)sender;
- (IBAction)setUpDrawMode:(id)sender;
- (IBAction)setUpEraseMode:(id)sender;
- (IBAction)clearAll:(id)sender;
- (void)clearAllLines;

- (IBAction)dismissModalSelf:(id)sender;

- (IBAction)undoDraw:(id)sender;
- (IBAction)redoDraw:(id)sender;
- (void)adjustUndoStatus;

- (void)storeSettingsToPreference;
- (void)restoreSettingsFromPreference;

@end

