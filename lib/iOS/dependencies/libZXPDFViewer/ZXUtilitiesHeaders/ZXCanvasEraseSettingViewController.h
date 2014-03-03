//
//  ZXCanvasEraseSettingViewController.h
//  ZyyxLibraries
//
//  Created by gotow on 11/01/28.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ZXCanvasLineWidthViewController.h"
#import "ZXLabelledTableViewCell.h"


@interface ZXCanvasEraseSettingViewController : UITableViewController {
    ZXCanvasLineWidthViewController *lineWidthViewController;

    ZXLabelledTableViewCell *titleCell;
    ZXLabelledTableViewCell *lineWidthCell;
    ZXLabelledTableViewCell *clearButtonCell;

    UIButton *clearButton;
}

@property (nonatomic, strong) ZXCanvasLineWidthViewController *lineWidthViewController;

@property (nonatomic, strong) IBOutlet ZXLabelledTableViewCell *titleCell;
@property (nonatomic, strong) IBOutlet ZXLabelledTableViewCell *lineWidthCell;
@property (nonatomic, strong) IBOutlet ZXLabelledTableViewCell *clearButtonCell;

@property (nonatomic, strong) IBOutlet UIButton *clearButton;

@end
