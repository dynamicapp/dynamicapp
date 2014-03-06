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
#import "ZXCanvasLineWidthViewController.h"
#import "ZXColorTableViewController.h"
#import "ZXLabelledTableViewCell.h"


@interface ZXCanvasDrawSettingViewController : UITableViewController <ZXColorTableViewControllerDelegate> {
    ZXCanvasLineWidthViewController *lineWidthViewController;
    ZXColorTableViewController *colorTableViewController;

    ZXLabelledTableViewCell *titleCell;
    ZXLabelledTableViewCell *lineWidthCell;
    ZXLabelledTableViewCell *colorTableCell;

    NSArray *colorList;
}

@property (nonatomic, strong) ZXCanvasLineWidthViewController *lineWidthViewController;
@property (nonatomic, strong) ZXColorTableViewController *colorTableViewController;

@property (nonatomic, strong) IBOutlet ZXLabelledTableViewCell *titleCell;
@property (nonatomic, strong) IBOutlet ZXLabelledTableViewCell *lineWidthCell;
@property (nonatomic, strong) IBOutlet ZXLabelledTableViewCell *colorTableCell;

@property (nonatomic, strong) NSArray *colorList; // must contain UIColor.

@end
