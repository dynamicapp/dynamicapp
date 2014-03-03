//
//  PDFPageListViewController.h
//  ZyyxLibraries
//
//  Created by gotow on 10/04/27.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ZXPDFDocumentView.h"
#import "ZXPDFPageListViewCell.h"


@interface ZXPDFPageListViewController : UITableViewController {
    ZXPDFDocumentView *documentView;

    NSArray *data;
    NSArray *sectionTitles;

    BOOL shouldUsePageCache;
    NSString *cacheStorePath;

    ZXPDFPageListViewCell *myCell;
}

@property (nonatomic, strong) ZXPDFDocumentView *documentView;
@property (nonatomic, strong) NSArray *sectionTitles;
@property (nonatomic) BOOL shouldUsePageCache;
@property (nonatomic, copy) NSString *cacheStorePath;
@property (nonatomic, strong) IBOutlet ZXPDFPageListViewCell *myCell;


- (id)initWithDocumentView:(ZXPDFDocumentView *)view;

- (void)selectRowAtPage:(NSInteger)pageNumber doubleTruckState:(ZXPDFPageViewDoubleTruckState)state;

@end
