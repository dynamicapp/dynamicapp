//
//  ZXCanvasDrawSettingViewController.h
//  ZyyxLibraries
//
//  Created by gotow on 11/01/28.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

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
