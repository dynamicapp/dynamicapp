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
