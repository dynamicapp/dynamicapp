//
//  AddressBook.h
//  DynamicApp
//
//  Created by ZYYX Inc on 8/13/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"
#import <AddressBookUI/AddressBookUI.h>

@interface AddressBook : DynamicAppPlugin <ABPeoplePickerNavigationControllerDelegate, ABPersonViewControllerDelegate, ABNewPersonViewControllerDelegate, UINavigationControllerDelegate> {
    ABPeoplePickerNavigationController *addressBookPicker;
    BOOL isSelect;
    ABRecordRef currentRecord;
}

@property (nonatomic, retain) ABPeoplePickerNavigationController *addressBookPicker;
@property (nonatomic) BOOL isSelect;
@property (nonatomic) ABRecordRef currentRecord;

- (void)show:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;

+ (NSDictionary *)getContactObject:(ABRecordRef)contact;

@end
