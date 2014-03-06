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