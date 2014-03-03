//
//  AddressBook.m
//  DynamicApp
//
//  Created by ZYYX Inc on 8/13/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "AddressBook.h"
#import "PluginResult.h"
#import "NSData+Base64.h"

const int ERROR_NO_DATA = 1;

@implementation AddressBook

@synthesize addressBookPicker, isSelect, currentRecord;

- (void)show:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.addressBookPicker = [[[ABPeoplePickerNavigationController alloc] init] autorelease];
    self.isSelect = [[arguments objectForKey:@"isSelect"] boolValue];
    self.callbackId = [options objectForKey:@"callbackId"];
    
    self.addressBookPicker.peoplePickerDelegate = self;
    self.addressBookPicker.delegate = self;    
    
    [[self viewController] presentViewController:self.addressBookPicker animated:YES completion:nil];
    
    if(self.isSelect) {
        self.addressBookPicker.topViewController.navigationItem.leftBarButtonItem = nil;
    } else {        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];
    }
}

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker shouldContinueAfterSelectingPerson:(ABRecordRef)person {

    if(self.isSelect) {

        self.currentRecord = person;
        [self performSelector:@selector(selectPressed:)];

//        UIBarButtonItem *selectButton = personView.navigationItem.rightBarButtonItem;
//        selectButton.title = NSLocalizedString(@"Select", @"AddressBook select button");
//        selectButton.target = self;
//        selectButton.action = @selector(selectPressed:);
//        
//        personView.navigationItem.rightBarButtonItem = selectButton;
        
//        self.currentRecord = person;

    } else {
        ABPersonViewController *personView = [[ABPersonViewController alloc] init];
        personView.displayedPerson = person;
        personView.allowsEditing = YES;
        personView.allowsActions = YES;
        personView.personViewDelegate = self;
        
        [peoplePicker pushViewController:personView animated:YES];
    }
    return NO;

}

- (void)selectPressed:(id)sender {
    PluginResult *result = nil;
    NSString *jsString = nil;
    
    NSDictionary *contactDict = [AddressBook getContactObject:self.currentRecord];
    if(contactDict != nil) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:contactDict];
        jsString = [result toSuccessCallbackString:self.callbackId];
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR messageAsInt:ERROR_NO_DATA];
        jsString = [result toErrorCallbackString:self.callbackId];
    }
    
    [self.addressBookPicker dismissViewControllerAnimated:YES completion:nil];
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)addPressed:(id)sender {
    ABNewPersonViewController *newPersonView = [[ABNewPersonViewController alloc] init];
    newPersonView.newPersonViewDelegate = self;
    UINavigationController *nc = [[UINavigationController alloc] initWithRootViewController:newPersonView];
    [self.addressBookPicker presentViewController:nc animated:YES completion:nil];
    [newPersonView release];
    [nc release];
}

- (void)cancelPressed:(id)sender {
    [self peoplePickerNavigationControllerDidCancel:self.addressBookPicker];
}

+ (NSDictionary *)getContactObject:(ABRecordRef)contact {    
    NSMutableDictionary *contactDict = [[NSMutableDictionary alloc] init];
    
    // id
    [contactDict setValue:[NSNumber numberWithInt:ABRecordGetRecordID(contact)] forKey:@"id"];
    
    CFStringRef temp;
    ABMutableMultiValueRef multiValue;
    int multiCount = 0;
    
    // displayName
    temp = ABRecordCopyCompositeName(contact);
    if(temp) {
        [contactDict setValue:(NSString*)temp forKey:@"displayName"];
        CFRelease(temp);
    }
    
    // name
    NSMutableDictionary *nameDict = [[NSMutableDictionary alloc] init];
    ABPropertyID nameProperty[] = {
        kABPersonFirstNameProperty, 
        kABPersonLastNameProperty,
        kABPersonMiddleNameProperty,
        kABPersonPrefixProperty,
        kABPersonSuffixProperty,
        kABPersonNicknameProperty,
        kABPersonFirstNamePhoneticProperty,
        kABPersonLastNamePhoneticProperty,
        kABPersonMiddleNamePhoneticProperty
    };
    NSArray *namePropertyKey = [NSArray arrayWithObjects:
        @"firstName",
        @"lastName",
        @"middleName",
        @"prefix",
        @"suffix",
        @"nickName",
        @"firstNamePhonetic", 
        @"lastNamePhonetic", 
        @"middleNamePhonetic",
        nil];
    
    int arrayLen = sizeof(nameProperty) / sizeof(nameProperty[0]);
    for(int i=0; i<arrayLen; i++) {
        temp = ABRecordCopyValue(contact, nameProperty[i]);
        
        if(temp) {
            [nameDict setValue:(NSString *)temp forKey:[namePropertyKey objectAtIndex:i]];
            CFRelease(temp);
        }
    }
    [contactDict setValue:nameDict forKey:@"name"];
    [nameDict release];
    
    // single value properties (nickname, birthday, note, organization)
    ABPropertyID singleValuePropKeys[] = {
        kABPersonNicknameProperty,
        kABPersonNoteProperty,
        kABPersonOrganizationProperty
    };
    NSArray *singleValuePropNames = [NSArray arrayWithObjects:
        @"nickName",
        @"note",
        @"organization",
        nil];
    arrayLen = sizeof(singleValuePropKeys) / sizeof(singleValuePropKeys[0]);
    for(int i=0; i<arrayLen; i++) {
        temp = ABRecordCopyValue(contact, singleValuePropKeys[i]);
        if(temp) {
            [contactDict setValue:(NSString *)temp forKey:[singleValuePropNames objectAtIndex:i]];
            CFRelease(temp);
        }
    }
    
    // birthday
    NSDate *tempDate = (NSDate *)ABRecordCopyValue(contact, kABPersonBirthdayProperty);
    if(tempDate) {
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy-MM-dd"];
        [contactDict setValue:[dateFormatter stringFromDate:tempDate]  forKey:@"birthday"];
        CFRelease(tempDate);
        [dateFormatter release];
    }
    
    // multi value properties (phone number, email, address, im, url)
    ABPropertyID multiValuePropKeys[] = {
        kABPersonPhoneProperty,
        kABPersonEmailProperty,
        kABPersonAddressProperty,
        kABPersonInstantMessageProperty,
        kABPersonURLProperty
    };
    NSArray *multiValuePropNames = [NSArray arrayWithObjects:
        @"phoneNumbers",
        @"emails",
        @"addresses",
        @"ims",
        @"urls",
        nil];
    arrayLen = sizeof(multiValuePropKeys) / sizeof(multiValuePropKeys[0]);
    for(int i=0; i<arrayLen; i++) {
        NSMutableArray *array = [[NSMutableArray alloc] init];
        multiValue = ABRecordCopyValue(contact, multiValuePropKeys[i]);
        multiCount = ABMultiValueGetCount(multiValue);
        
        for(int j=0; j<multiCount; j++) {
            [array addObject:(NSString *)ABMultiValueCopyValueAtIndex(multiValue, j)];
        }
        
        [contactDict setValue:array forKey:[multiValuePropNames objectAtIndex:i]];
        [array release];
        
        if(multiValue) {
            CFRelease(multiValue);
        }
    }
    
    // image
    if(ABPersonHasImageData(contact)) {
        CFDataRef tempData = ABPersonCopyImageData(contact);
        if(tempData) {
            NSData *imageData = UIImageJPEGRepresentation([UIImage imageWithData:(NSData*)tempData], 1.0);
            [contactDict setValue:[NSString stringWithFormat: @"data:image/jpeg;base64,%@", [imageData base64EncodedString]] forKey:@"images"];
            CFRelease(tempData);
        }
    }
    
    NSDictionary *contactDictionary = [NSDictionary dictionaryWithDictionary:contactDict];
    [contactDict release];
    
    return contactDictionary;
}

- (BOOL)peoplePickerNavigationController:(ABPeoplePickerNavigationController *)peoplePicker 
      shouldContinueAfterSelectingPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifier {
    return YES;    
}

- (void)peoplePickerNavigationControllerDidCancel:(ABPeoplePickerNavigationController *)peoplePicker {
    if(!self.isSelect) {
        [[NSNotificationCenter defaultCenter] removeObserver:self];
    }
    
    [peoplePicker dismissViewControllerAnimated:YES completion:nil];
    
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

- (BOOL)personViewController:(ABPersonViewController *)personViewController shouldPerformDefaultActionForPerson:(ABRecordRef)person property:(ABPropertyID)property identifier:(ABMultiValueIdentifier)identifierForValue {
    return YES;
}

- (void)newPersonViewController:(ABNewPersonViewController *)newPersonViewController didCompleteWithNewPerson:(ABRecordRef)person {
    [newPersonViewController dismissViewControllerAnimated:YES completion:nil];
}

- (void)keyboardWillHide:(NSNotification *)notification {
    if([self.addressBookPicker.viewControllers count] < 3) {
        UIBarButtonItem *addPersonButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPressed:)];
        self.addressBookPicker.topViewController.navigationItem.rightBarButtonItem = addPersonButton;
        [addPersonButton release];
    }
}

- (void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)_viewController animated:(BOOL)animated {
    if(!([_viewController isKindOfClass:[ABPersonViewController class]] || self.isSelect)) {//ABMembersViewController
        UIBarButtonItem *addPersonButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPressed:)];
        self.addressBookPicker.topViewController.navigationItem.rightBarButtonItem = addPersonButton;
        [addPersonButton release];
        
        UIBarButtonItem *cancelButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelPressed:)];
        self.addressBookPicker.topViewController.navigationItem.leftBarButtonItem = cancelButton;
        [cancelButton release];
    }
}


- (void)dealloc {
    self.addressBookPicker = nil;
    self.currentRecord = nil;
    
    [super dealloc];
}

@end
