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
#import "ZXPDFDocumentObject.h"
#import "ZXPDFDocumentView.h"


@class ZXPDFPageIndicatorViewController;
@class ZXPDFPageIndicatorViewControllerPageButton;
@class ZXPDFPageIndicatorViewControllerJumbBarView;

@protocol ZXPDFPageIndicatorViewControllerDelegate <NSObject>
- (void)pageIndicator:(ZXPDFPageIndicatorViewController *)aController didAppearAnimated:(BOOL)animated;
- (void)pageIndicator:(ZXPDFPageIndicatorViewController *)aController didDisappearAnimated:(BOOL)animated;
- (void)pageIndicator:(ZXPDFPageIndicatorViewController *)aController didSelectItemAtIndex:(NSInteger)index;
- (void)pageIndicator:(ZXPDFPageIndicatorViewController *)aController didTapDetailItemAtIndex:(NSInteger)index;
@end

@protocol ZXPDFPageIndicatorViewControllerJumbBarViewDelegate <NSObject>
- (void)jumpBarView:(ZXPDFPageIndicatorViewControllerJumbBarView *)aView willBeginDraggingWithMeasure:(CGFloat)measure;
- (void)jumpBarView:(ZXPDFPageIndicatorViewControllerJumbBarView *)aView didChangeMeasure:(CGFloat)measure;
- (void)jumpBarView:(ZXPDFPageIndicatorViewControllerJumbBarView *)aView didEndDraggingWithMeasure:(CGFloat)measure;
- (void)jumpBarViewDidCancelDragging:(ZXPDFPageIndicatorViewControllerJumbBarView *)aView;
@end



extern NSString * const ZXPDFPageIndicatorViewControllerPresentingAnimationID;
extern NSString * const ZXPDFPageIndicatorViewControllerDismissingAnimationID;



// NOTICE! This class is NOT kind of UIViewController.
@interface ZXPDFPageIndicatorViewController : NSObject
<UIScrollViewDelegate, UIPopoverControllerDelegate, ZXPDFPageIndicatorViewControllerJumbBarViewDelegate>
{
    NSInteger selectedIndex;
    ZXPDFDocumentObject *documentObject;
    ZXPDFDocumentViewPageOrder pageOrder;
    NSArray *dataSource;
    CGSize itemSize;
    UIView *view_;
    UIView *baseView;
    UIScrollView *listView;
    UIPopoverController *detailViewPopover;

    NSMutableSet *buttonPool;
    NSRange storedIndexRange;

    id<ZXPDFPageIndicatorViewControllerDelegate> __unsafe_unretained delegate;
    BOOL isVisible;
    BOOL isAnimating;
    BOOL shouldUseDetailView;
}

@property (nonatomic, readonly) NSInteger selectedIndex;
@property (nonatomic, strong) ZXPDFDocumentObject *documentObject; // init some properties when set.
@property (nonatomic) ZXPDFDocumentViewPageOrder pageOrder;
@property (nonatomic, strong, readonly) NSArray *dataSource; // set when documentObject did set.
@property (nonatomic, readonly) CGSize itemSize; // set when documentObject did set.
@property (nonatomic, strong) IBOutlet UIView *view;
@property (nonatomic, strong) IBOutlet UIView *baseView;
@property (nonatomic, strong) IBOutlet UIScrollView *listView;
@property (nonatomic, strong) IBOutlet ZXPDFPageIndicatorViewControllerJumbBarView *jumpBarView;
@property (nonatomic, strong) UIPopoverController *detailViewPopover;

@property (nonatomic, readonly) NSRange currentIndexRange;
@property (nonatomic, unsafe_unretained) id<ZXPDFPageIndicatorViewControllerDelegate> delegate;
@property (nonatomic, readonly) BOOL isVisible;
@property (nonatomic, readonly) BOOL isAnimating;
@property (nonatomic) BOOL shouldUseDetailView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil; // similar to UIViewController.

// add/remove self.view to/from aView.
- (void)presentInBaseView:(UIView *)aView animated:(BOOL)animated;
- (void)dismissAnimated:(BOOL)animated;

- (NSInteger)pageNumberFromIndex:(NSInteger)index;
- (NSInteger)indexFromPageNumber:(NSInteger)pageNumber;

- (void)setItemToCenterWithIndex:(NSInteger)index;
- (void)setItemToCenterWithPageNumber:(NSInteger)pageNumber;
- (ZXPDFPageIndicatorViewControllerPageButton *)pageButtonAtIndex:(NSInteger)index;
- (NSInteger)indexOfPageButton:(ZXPDFPageIndicatorViewControllerPageButton *)aPageButton;

// for override
- (void)setUpItem:(ZXPDFPageIndicatorViewControllerPageButton *)aPageButton withIndex:(NSInteger)index;

@end



@interface ZXPDFPageIndicatorViewControllerPageButton : UIView {
    UIButton *button;
    NSString *labelString;
    NSIndexPath *indexPath;
    BOOL highlighted_;

    ZXPDFPageIndicatorViewController *__unsafe_unretained target;
    SEL action_;
}

+ (id)buttonWithSize:(CGSize)size;

@property (nonatomic, strong) UIButton *button;
@property (nonatomic, strong) NSString *labelString;
@property (nonatomic, strong) NSIndexPath *indexPath;
@property (nonatomic) BOOL highlighted;

@property (nonatomic, unsafe_unretained) ZXPDFPageIndicatorViewController *target;
@property (nonatomic) SEL action;

- (void)setTarget:(ZXPDFPageIndicatorViewController *)aTarget action:(SEL)action;

@end



@interface ZXPDFPageIndicatorViewControllerJumbBarView : UIView {

}

@property (nonatomic, strong) UIView *knobView;
@property (nonatomic, strong) UILabel *measureLabel;
@property (nonatomic) NSInteger maximumPageNumber;
@property (nonatomic, unsafe_unretained) id<ZXPDFPageIndicatorViewControllerJumbBarViewDelegate> delegate;

@property (nonatomic) CGFloat currentMeasure;

- (void)setUpViewStyles;

@end

