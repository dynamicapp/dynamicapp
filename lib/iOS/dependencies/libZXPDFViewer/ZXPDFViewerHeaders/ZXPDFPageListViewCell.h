//
//  PDFPageListViewCell.h
//  ZyyxLibraries
//
//  Created by gotow on 10/04/28.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ZXPDFPageView.h"


@interface ZXPDFPageListViewCell : UITableViewCell {
    UIView *thumbnailHolder;
    UILabel *pageNumberLabel;

    NSOperation *workingOperation;
}

@property (nonatomic, strong) IBOutlet UIView *thumbnailHolder;
@property (nonatomic, strong) IBOutlet UILabel *pageNumberLabel;

@property (nonatomic, strong) NSOperation *workingOperation;

- (void)setUpWithPageView:(UIView *)view andLabelText:(NSString *)text;
- (void)replaceThumbnailView:(UIView *)view;

@end
