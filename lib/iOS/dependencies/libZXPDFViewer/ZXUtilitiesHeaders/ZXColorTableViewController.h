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


@class ZXColorTableViewController;

@protocol ZXColorTableViewControllerDelegate <NSObject>
- (void)colorTable:(ZXColorTableViewController *)aController didSelectColor:(UIColor *)aColor colorImage:(UIImage *)anImage;
@end



@interface ZXColorTableViewController : UITableViewController {
    NSArray *indexedColors;
    NSInteger numberOfColorsPerRow;
    id<ZXColorTableViewControllerDelegate> __unsafe_unretained delegate;
}

@property (nonatomic, strong, readonly) NSArray *indexedColors; // contain arrays of colors for row.
@property (nonatomic, readonly) NSInteger numberOfColorsPerRow;
@property (nonatomic, unsafe_unretained) id<ZXColorTableViewControllerDelegate> delegate;
@property (nonatomic, readonly) CGFloat tableWidth;
@property (nonatomic, readonly) CGFloat tableHeight;

- (id)init; // shortcut 'initWithStyle:UITableViewStylePlain'

- (void)setUpWithColors:(NSArray *)aColorList numberPerRow:(NSInteger)numberPerRow;

@end



@interface ZXColorTableViewCell : UITableViewCell {
    ZXColorTableViewController *__unsafe_unretained controller;
    NSArray *colors;
}

@property (nonatomic, unsafe_unretained) ZXColorTableViewController *controller;
@property (nonatomic, copy, readonly) NSArray *colors;

+ (CGFloat)cellHeight;
+ (CGFloat)cellHeightMargin;
+ (CGFloat)cellWidthWithColorCount:(NSInteger)colorCount;
+ (UIImage *)imageWithColor:(UIColor *)aColor alpha:(CGFloat)alpha;
+ (UIButton *)buttonWithColor:(UIColor *)aColor;

- (void)setUpWithColors:(NSArray *)aColorList;
- (void)buttonTapped:(UIButton *)sender;

@end
