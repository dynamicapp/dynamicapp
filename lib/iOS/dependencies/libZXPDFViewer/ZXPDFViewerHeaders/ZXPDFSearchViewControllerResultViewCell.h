//
//  ZXPDFSearchViewControllerResultViewCell.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/29.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "ZXPDFSearchCoreDataModels.h"


@class ZXPDFSearchViewControllerResultViewCellHitCountBar;


@interface ZXPDFSearchViewControllerResultViewCell : UITableViewCell {

}

@property (nonatomic, strong) IBOutlet ZXPDFSearchViewControllerResultViewCellHitCountBar *hitCountBar;
@property (nonatomic, strong) IBOutlet UILabel *hitCountLabel;
@property (nonatomic, strong) IBOutlet UILabel *messageLabel;
@property (nonatomic, strong) IBOutlet UIView *thumbnailHolder;
@property (nonatomic, strong) IBOutlet UIImageView *thumbnailView;
@property (nonatomic, strong) IBOutlet UILabel *noThumbnailLabel;

- (void)setUpWithResultPage:(ZXPDFSearchResultPage *)aResultPage searchWord:(ZXPDFSearchWord *)aSearchWord;
- (void)setThumbnailImage:(UIImage *)anImage; // if image is nil, show noThumbnailLabel.

@end



@interface ZXPDFSearchViewControllerResultViewCellHitCountBar : UIView {

}

@property (nonatomic, strong) UIView *innerView;
@property (nonatomic, readonly) CGFloat maxWidthOfInnerView;

- (void)setUp;
- (void)setCount:(NSInteger)count forMaxCount:(NSInteger)maxCount;

@end