//
//  LazyLoadingImageView.h
//  ZyyxLibraries
//
//  Created by gotow on 10/10/12.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


@interface ZXLazyLoadingImageView : UIImageView {
    UIActivityIndicatorView *activityIndicator;
    NSInvocationOperation *operationForImage;
    NSOperation *operationForBackground;
}

@property (nonatomic, strong) UIActivityIndicatorView *activityIndicator;
@property (nonatomic, strong) NSInvocationOperation *operationForImage;
@property (nonatomic, strong) NSOperation *operationForBackground;

- (id)initWithFrame:(CGRect)aFrame lazyInvocationOperation:(NSInvocationOperation *)anOperationReturningImage;

- (void)startLoading;
- (void)cancelOperations;

@end
