//
//  ZXPDFPageIndicatorDetailViewController.h
//  ZyyxLibraries
//
//  Created by gotow on 11/03/08.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ZXPDFPageIndicatorViewController.h"


@interface ZXPDFPageIndicatorDetailViewController : UIViewController {
    ZXPDFPageIndicatorViewControllerPageButton *pageButton;
    UIImage *buttonImage;
    UIButton *button;

    ZXPDFPageIndicatorViewController *__unsafe_unretained target;
    SEL action_;
}

@property (nonatomic, strong) ZXPDFPageIndicatorViewControllerPageButton *pageButton;
@property (nonatomic, strong) UIImage *buttonImage;
@property (nonatomic, strong) IBOutlet UIButton *button;

@property (nonatomic, unsafe_unretained) ZXPDFPageIndicatorViewController *target;
@property (nonatomic) SEL action;

- (void)setUpTitleAndImage; // do nothing when view is not loaded. (send this automatically in viewDidLoad:)
- (void)setTarget:(ZXPDFPageIndicatorViewController *)aTarget action:(SEL)action;

@end
