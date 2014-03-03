//
//  ZXLabelledTableViewCell.h
//  ZyyxLibraries
//
//  Created by gotow on 11/01/28.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface ZXLabelledTableViewCell : UITableViewCell {
    UILabel *label;
    UIView *contentHolder;
}

@property (nonatomic, strong) IBOutlet UILabel *label;
@property (nonatomic, strong) IBOutlet UIView *contentHolder;

- (void)setLabelWidth:(CGFloat)width; // adjust contentHolder's frame automatically.

@end
