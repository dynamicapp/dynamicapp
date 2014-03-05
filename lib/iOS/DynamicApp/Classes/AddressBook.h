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
