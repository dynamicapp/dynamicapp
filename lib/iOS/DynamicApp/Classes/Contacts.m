//
//  Contacts.m
//  DynamicApp
//
//  Created by ZYYX on 9/11/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "Contacts.h"
#import "AddressBook.h"
#import "AddressBook/AddressBook.h"
#import "PluginResult.h"
#import "NSData+Base64.h"


@implementation Contacts

- (void) save:(NSDictionary *)arguments withOptions:(NSDictionary *)options {

    NSDictionary *propertyKeys = [NSDictionary dictionaryWithObjectsAndKeys: 
        [NSNumber numberWithInt:kABPersonFirstNameProperty], @"firstName",
        [NSNumber numberWithInt:kABPersonFirstNamePhoneticProperty], @"firstNamePhonetic",
        [NSNumber numberWithInt:kABPersonLastNameProperty], @"lastName",
        [NSNumber numberWithInt:kABPersonLastNamePhoneticProperty], @"lastNamePhonetic",
        [NSNumber numberWithInt:kABPersonMiddleNameProperty], @"middleName",
        [NSNumber numberWithInt:kABPersonMiddleNamePhoneticProperty], @"middleNamePhonetic",
        [NSNumber numberWithInt:kABPersonNicknameProperty], @"nickname",
        [NSNumber numberWithInt:kABPersonPrefixProperty], @"prefix",
        [NSNumber numberWithInt:kABPersonSuffixProperty], @"suffix",
        [NSNumber numberWithInt:kABPersonPhoneProperty], @"phoneNumbers",
        kABPersonPhoneHomeFAXLabel, @"phoneNumbersFax",
        kABPersonPhoneIPhoneLabel, @"phoneNumbersIPhone",
        kABPersonPhoneMainLabel, @"phoneNumbersMain",
        kABPersonPhoneMobileLabel, @"phoneNumbersMobile",
        kABPersonPhoneOtherFAXLabel, @"phoneNumbersOtherFax",
        kABPersonPhonePagerLabel, @"phoneNumbersPager",
        kABPersonPhoneWorkFAXLabel, @"phoneNumbersWorkFax",
        [NSNumber numberWithInt:kABPersonEmailProperty], @"emails",
        kABHomeLabel, @"homeLabel",
        kABWorkLabel, @"workLabel",
        kABOtherLabel, @"otherLabel",
        [NSNumber numberWithInt:kABPersonAddressProperty], @"addresses",
        kABPersonAddressCityKey, @"addressesCity",
        kABPersonAddressCountryCodeKey, @"addressesCountryCode",
        kABPersonAddressCountryKey, @"addressesCountry",
        kABPersonAddressStateKey, @"addressesState",
        kABPersonAddressStreetKey, @"addressesStreet",
        kABPersonAddressZIPKey, @"addressesZIP",
        [NSNumber numberWithInt:kABPersonInstantMessageProperty], @"ims",
        kABPersonInstantMessageServiceAIM, @"imsAim",
        kABPersonInstantMessageServiceFacebook, @"imsFacebook",
        kABPersonInstantMessageServiceGaduGadu, @"imsGaduGadu",
        kABPersonInstantMessageServiceGoogleTalk, @"imsGoogleTalk",
        kABPersonInstantMessageServiceICQ, @"imsICQ",
        kABPersonInstantMessageServiceJabber, @"imsJabber",
        kABPersonInstantMessageServiceKey, @"imsKey",
        kABPersonInstantMessageServiceMSN, @"imsMSN",
        kABPersonInstantMessageServiceQQ, @"imsQQ",
        kABPersonInstantMessageServiceSkype, @"imsSkype",
        kABPersonInstantMessageServiceYahoo, @"imsYahoo",
        kABPersonInstantMessageUsernameKey, @"imsUserName",
        [NSNumber numberWithInt:kABPersonOrganizationProperty], @"organizations",
        [NSNumber numberWithInt:kABPersonBirthdayProperty], @"birthday",
        [NSNumber numberWithInt:kABPersonNoteProperty], @"note",
        [NSNumber numberWithInt:kABPersonURLProperty], @"urls",
        kABPersonHomePageLabel, @"homeLabel",
        nil];
    
    NSDictionary *contact = [arguments objectForKey:@"contact"];
    NSLog(@"%@", contact);
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
    ABAddressBookRef iPhoneAddressBook  = ABAddressBookCreateWithOptions(nil, nil);
#else
    ABAddressBookRef iPhoneAddressBook = ABAddressBookCreate();
#endif
    ABRecordRef contactRecord = NULL;
    BOOL newFlag = NO;
    int recordId = [[contact objectForKey:@"id"] intValue];
    if(recordId > 0) {
        contactRecord = ABAddressBookGetPersonWithRecordID(iPhoneAddressBook, recordId);
    }
    
    if(contactRecord == NULL) {
        contactRecord = ABPersonCreate();
        newFlag = YES;
    }
        
    CFErrorRef error = NULL;
    //['id', 'displayName', 'name', 'nickname', 'phoneNumbers', 'emails', 'addresses', 'ims', 'organizations', 'birthday', 'note', 'photos', 'categories', 'urls'];
    
    // name
    NSDictionary *name = [contact objectForKey:@"name"];
    if(name) {
        for(id key in name) {
            NSNumber *k = [propertyKeys objectForKey:key];
            if(k) {
                NSLog(@"k is ok %@", k);
                //ABRecordSetValue(newPerson, [k intValue], (CFStringRef)[name objectForKey:key], nil);
                ABRecordSetValue(contactRecord, [k intValue], [name objectForKey:key], &error);
            }
        }
    }
    
    // single value properties (nickname, birthday, note, organization)
    NSArray *singleValueProp = [NSArray arrayWithObjects:
        @"nickname",
        @"note",
        @"organizations",
        nil];
    for(id key in singleValueProp) {
        NSString *singleValue = [contact objectForKey:key];
        if(singleValue) {
            NSLog(@"singleValue is ok %@", singleValue);
            //ABRecordSetValue(newPerson, [[propertyKeys objectForKey:key] intValue], (CFStringRef)val, nil);
            ABRecordSetValue(contactRecord, [[propertyKeys objectForKey:key] intValue], singleValue, &error);
        }
    }
    
    // birthday
    NSString *birthday = [contact objectForKey:@"birthday"];
    if(birthday) {
        NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
        [dateFormatter setDateFormat:@"yyyy-MM-dd"];
        NSDate *_birthday = [dateFormatter dateFromString:birthday];
        [dateFormatter release];
        
        ABRecordSetValue(contactRecord, kABPersonBirthdayProperty, (CFDateRef)_birthday, &error); 
    }
    
    // address
    NSArray *addresses = [contact objectForKey:@"addresses"];
    if(addresses) {
        ABMutableMultiValueRef multiAddress = ABMultiValueCreateMutable(kABDictionaryPropertyType);
        BOOL hasAddress = NO;
        for(NSDictionary *labelAddress in addresses) {
            NSMutableDictionary *addressDictionary = [[NSMutableDictionary alloc] init];
            BOOL hasData = NO;
            NSString *mValKey = [[labelAddress allKeys] objectAtIndex:0];
            
            NSDictionary *addressData = [labelAddress objectForKey:mValKey];
            for(id key in addressData) {
                NSString *k = [propertyKeys objectForKey:key];
                if(k) {
                    NSLog(@"k is ok %@", k);
                    hasData = YES;
                    [addressDictionary setObject:[addressData objectForKey:key] forKey:k];
                }                
            }
            
            if(hasData) {
                CFStringRef label = (CFStringRef)[propertyKeys objectForKey:mValKey];
                NSLog(@"label: %@ => %@", label, [addressDictionary description]);
                if(label == NULL) {
                    label = (CFStringRef)@"";
                }
                hasAddress = ABMultiValueAddValueAndLabel(multiAddress, addressDictionary, label, NULL);
            }
            [addressDictionary release];
        }
        if(hasAddress) {
            ABRecordSetValue(contactRecord, kABPersonAddressProperty, multiAddress, &error);
            CFRelease(multiAddress);
        }
    }
    
    // ims
    NSArray *imList = [contact objectForKey:@"ims"];
    ABMutableMultiValueRef imRef = ABMultiValueCreateMutable(kABMultiDictionaryPropertyType);
    BOOL hasIms = NO;
    for(NSDictionary *im in imList) {
        NSString *key = [[im allKeys] objectAtIndex:0];
        NSString *label = (NSString *)[propertyKeys objectForKey:key];
        if(label == nil) {
            label = @"";
        }
        NSMutableDictionary *imDict = [[NSMutableDictionary alloc] init];
        [imDict setObject:label forKey:(NSString*)kABPersonInstantMessageServiceKey];
        [imDict setObject:[im objectForKey:key] forKey:(NSString*)kABPersonInstantMessageUsernameKey];
        hasIms = ABMultiValueAddValueAndLabel(imRef, imDict, kABHomeLabel, NULL);
        [imDict release];
    }
    if(hasIms) {
        ABRecordSetValue(contactRecord, kABPersonInstantMessageProperty, imRef, NULL);
        CFRelease(imRef);
    }
    
    // multi value properties (phone number, email, url)
    NSArray *multiValueProp = [NSArray arrayWithObjects:
        @"phoneNumbers",
        @"emails",
        //@"ims",
        @"urls",
        nil];
    for(id key in multiValueProp) {
        NSArray *multiValueList = [contact objectForKey:key];
        NSLog(@"multiValueProp %@", multiValueList);
        NSNumber *k = [propertyKeys objectForKey:key];
        if(multiValueList && [multiValueList count] > 0 && k) {
            ABMutableMultiValueRef multiValueRef = ABMultiValueCreateMutable(kABMultiStringPropertyType);
            BOOL wasAdded = false;
            
            for(id multiValue in multiValueList) {
                NSString *mValKey = [[multiValue allKeys] objectAtIndex:0];
                CFStringRef label = (CFStringRef)[propertyKeys objectForKey:mValKey];
                NSLog(@"label: %@", label);
                if(label == NULL) {
                    label = (CFStringRef)@"";
                }
                wasAdded = true;NSLog(@"before add v & l");
                ABMultiValueAddValueAndLabel(multiValueRef, [multiValue objectForKey:mValKey], label, NULL);
                NSLog(@"after add v & l");
            }
            
            if(wasAdded) {
                NSLog(@"before ABRecordSetValue");
                ABRecordSetValue(contactRecord, [k intValue], multiValueRef, &error);
                NSLog(@"after ABRecordSetValue");
            }
                        
            CFRelease(multiValueRef);
        }
    }
    
    // image
    NSString *imgString = [contact objectForKey:@"photo"];
    if(imgString) {
        NSData *imgData = nil;
        if([imgString rangeOfString:@"data:image/jpeg;base64,"].location != NSNotFound) {
            imgString = [imgString stringByReplacingOccurrencesOfString:@"data:image/jpeg;base64," withString:@""];
            imgData = [NSData dataFromBase64String:imgString];
        } else if([imgString rangeOfString:@"file://"].location != NSNotFound) {
            imgString = [imgString stringByReplacingOccurrencesOfString:@"file://" withString:@""];
            UIImage *img = [UIImage imageWithContentsOfFile:imgString];
            imgData = UIImagePNGRepresentation(img);
        }
        NSLog(@"%@", imgData);
        if(imgData != nil) {
            ABPersonSetImageData(contactRecord, (CFDataRef)imgData, &error);
        }
    }
    
    BOOL saveResult = NO;
    if(error == NULL) {
        if(ABAddressBookAddRecord(iPhoneAddressBook, contactRecord, &error)) {
            saveResult = ABAddressBookSave(iPhoneAddressBook, &error);
        }
        NSLog(@"ABAddressBookAddRecord %@", [(NSError *)error description]);
    }
    NSLog(@"saveResult: %d", saveResult);
    if(contactRecord && newFlag) {
        CFRelease(contactRecord);
    }
    if(iPhoneAddressBook) {
        CFRelease(iPhoneAddressBook);
    }
    
    
    PluginResult *result = nil;
    NSString *jsString = nil;

    if(saveResult || error == NULL) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
        jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        jsString = [result toErrorCallbackString:[options objectForKey:@"callbackId"]];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) remove:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
    ABAddressBookRef addressBook  = ABAddressBookCreateWithOptions(nil, nil);
#else
    ABAddressBookRef addressBook = ABAddressBookCreate();
#endif
    NSDictionary *contact = [arguments objectForKey:@"contact"];
    NSString *recordId = [contact objectForKey:@"id"];
    ABRecordRef contactRecord = NULL;
    CFErrorRef error = NULL;
    
    BOOL removeResult = NO;
    if(recordId != nil) {
        contactRecord = ABAddressBookGetPersonWithRecordID(addressBook, [recordId intValue]);
        if(contactRecord != NULL) {
            if(ABAddressBookRemoveRecord(addressBook, contactRecord, &error)) {
                removeResult = ABAddressBookSave(addressBook, &error);
            }
        }
    }
    
    PluginResult *result = nil;
    NSString *jsString = nil;

    if(error == nil && contactRecord != NULL && removeResult) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
        jsString = [result toSuccessCallbackString:[options objectForKey:@"callbackId"]];
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        jsString = [result toErrorCallbackString:[options objectForKey:@"callbackId"]];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) search:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSArray *conditions = [arguments objectForKey:@"conditions"];
#if __IPHONE_OS_VERSION_MIN_REQUIRED > 50100
    ABAddressBookRef addressBook  = ABAddressBookCreateWithOptions(nil, nil);
#else
    ABAddressBookRef addressBook = ABAddressBookCreate();
#endif
    NSArray *records = (NSArray*)ABAddressBookCopyArrayOfAllPeople(addressBook);
    int recordsCount = [records count];
    
    NSMutableArray *matches = [[NSMutableArray alloc] init];
    
    for (int i=0; i<recordsCount; i++) {
        ABRecordRef record = [records objectAtIndex:i];
        if([self _checkIfMatch:record conditions:conditions]) {
            [matches addObject:[AddressBook getContactObject:record]];
        }
    }
    [records release];
    
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsArray:matches];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:[options objectForKey:@"callbackId"]]];
}

- (BOOL) _checkIfMatch:(ABRecordRef)record conditions:(NSArray *)conditions {

    NSDictionary *propertyKeys = [NSDictionary dictionaryWithObjectsAndKeys: 
        [NSNumber numberWithInt:kABPersonFirstNameProperty], @"firstName",
        [NSNumber numberWithInt:kABPersonFirstNamePhoneticProperty], @"firstNamePhonetic",
        [NSNumber numberWithInt:kABPersonLastNameProperty], @"lastName",
        [NSNumber numberWithInt:kABPersonLastNamePhoneticProperty], @"lastNamePhonetic",
        [NSNumber numberWithInt:kABPersonMiddleNameProperty], @"middleName",
        [NSNumber numberWithInt:kABPersonMiddleNamePhoneticProperty], @"middleNamePhonetic",
        [NSNumber numberWithInt:kABPersonNicknameProperty], @"nickname",
        [NSNumber numberWithInt:kABPersonPrefixProperty], @"prefix",
        [NSNumber numberWithInt:kABPersonSuffixProperty], @"suffix",
        [NSNumber numberWithInt:kABPersonPhoneProperty], @"phoneNumbers",
        kABPersonPhoneHomeFAXLabel, @"phoneNumbersFax",
        kABPersonPhoneIPhoneLabel, @"phoneNumbersIPhone",
        kABPersonPhoneMainLabel, @"phoneNumbersMain",
        kABPersonPhoneMobileLabel, @"phoneNumbersMobile",
        kABPersonPhoneOtherFAXLabel, @"phoneNumbersOtherFax",
        kABPersonPhonePagerLabel, @"phoneNumbersPager",
        kABPersonPhoneWorkFAXLabel, @"phoneNumbersWorkFax",
        [NSNumber numberWithInt:kABPersonEmailProperty], @"emails",
        kABHomeLabel, @"homeLabel",
        kABWorkLabel, @"workLabel",
        kABOtherLabel, @"otherLabel",
        [NSNumber numberWithInt:kABPersonAddressProperty], @"addresses",
        kABPersonAddressCityKey, @"addressesCity",
        kABPersonAddressCountryCodeKey, @"addressesCountryCode",
        kABPersonAddressCountryKey, @"addressesCountry",
        kABPersonAddressStateKey, @"addressesState",
        kABPersonAddressStreetKey, @"addressesStreet",
        kABPersonAddressZIPKey, @"addressesZIP",
        [NSNumber numberWithInt:kABPersonInstantMessageProperty], @"ims",
        kABPersonInstantMessageServiceAIM, @"imsAim",
        kABPersonInstantMessageServiceFacebook, @"imsFacebook",
        kABPersonInstantMessageServiceGaduGadu, @"imsGaduGadu",
        kABPersonInstantMessageServiceGoogleTalk, @"imsGoogleTalk",
        kABPersonInstantMessageServiceICQ, @"imsICQ",
        kABPersonInstantMessageServiceJabber, @"imsJabber",
        kABPersonInstantMessageServiceKey, @"imsKey",
        kABPersonInstantMessageServiceMSN, @"imsMSN",
        kABPersonInstantMessageServiceQQ, @"imsQQ",
        kABPersonInstantMessageServiceSkype, @"imsSkype",
        kABPersonInstantMessageServiceYahoo, @"imsYahoo",
        kABPersonInstantMessageUsernameKey, @"imsUserName",
        [NSNumber numberWithInt:kABPersonOrganizationProperty], @"organizations",
        [NSNumber numberWithInt:kABPersonBirthdayProperty], @"birthday",
        [NSNumber numberWithInt:kABPersonNoteProperty], @"note",
        [NSNumber numberWithInt:kABPersonURLProperty], @"urls",
        kABPersonHomePageLabel, @"homeLabel",
        nil];
    BOOL result = NO;
    
    // search record id
    {
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF contains[cd] %@", @"id"];
        NSArray *filteredConditions = [conditions filteredArrayUsingPredicate:predicate];
        int recordId = ABRecordGetRecordID(record);
        for(NSString *condition in filteredConditions) {
            NSDictionary *conditionDict = [self _getConditionParts:condition];
            if(conditionDict == nil) {
                continue;
            }
            
            if ((recordId == [[conditionDict objectForKey:@"value"] intValue]) == [[conditionDict objectForKey:@"equal"] boolValue]){
                return YES;
            }
        }
    }
    
    // displayName
    {
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF contains[cd] %@", @"displayName"];
        NSArray *filteredConditions = [conditions filteredArrayUsingPredicate:predicate];
        NSString *propValue = [(NSString*)ABRecordCopyCompositeName(record) autorelease];
        for(NSString *condition in filteredConditions) {
            if(propValue) {
                NSDictionary *conditionDict = [self _getConditionParts:condition];
                if(conditionDict == nil) {
                    continue;
                }
                NSPredicate *containPred = [NSPredicate predicateWithFormat:@"SELF contains[cd] %@", [conditionDict objectForKey:@"value"]];
                BOOL match = [containPred evaluateWithObject:propValue];
                if(match == [[conditionDict objectForKey:@"equal"] boolValue]) {
                    result = YES;
                    break;
                }
            }
        }
    }

        
    // search single value properties (name, nickname, note, organizations)
    NSArray *singleValueProp = [NSArray arrayWithObjects:
        @"nickname",
        @"note",
        @"organizations",
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
    for(id key in singleValueProp) {
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF contains[cd] %@", key];
        NSArray *filteredConditions = [conditions filteredArrayUsingPredicate:predicate];
        NSLog(@"key: %@", key);
        for(NSString *condition in filteredConditions) {
            NSLog(@"condition: %@", condition);
            NSDictionary *conditionDict = [self _getConditionParts:condition];
            NSLog(@"conditionDict: %@", [conditionDict description]);
            if(conditionDict == nil) {
                continue;
            }
            NSString* propValue = [(NSString*)ABRecordCopyValue(record, [[propertyKeys objectForKey:[conditionDict objectForKey:@"key"]] intValue]) autorelease];
            NSLog(@"proVal: %@", propValue);
            if (propValue != nil && [propValue length] > 0) {
                NSPredicate *containPred = [NSPredicate predicateWithFormat:@"SELF contains[cd] %@", [conditionDict objectForKey:@"value"]];
                BOOL match = [containPred evaluateWithObject:propValue];
                NSLog(@"match: %d", match);
                if(match == [[conditionDict objectForKey:@"equal"] boolValue]) {
                    result = YES;
                    break;
                } 
            }
        }
        
        if(result) {
            break;
        }
    }

    if(result) {
        return YES;
    }
    
    // search multi value properties (phone number, email, address, im, url)
    NSArray *multiValueProp = [NSArray arrayWithObjects:
        @"phoneNumbers",
        @"emails",
        @"addresses",
        @"ims",
        @"urls",
        nil];
    for(id key in multiValueProp) {
        NSPredicate *predicate = [NSPredicate predicateWithFormat:@"SELF contains[cd] %@", key];
        NSArray *filteredConditions = [conditions filteredArrayUsingPredicate:predicate];
        
        for(NSString *condition in filteredConditions) {
            NSDictionary *conditionDict = [self _getConditionParts:condition];
            
            if(conditionDict == nil) {
                continue;
            }
            ABMutableMultiValueRef multiValue = ABRecordCopyValue(record, [[propertyKeys objectForKey:key] intValue]);
            int multiCount = ABMultiValueGetCount(multiValue);
            
            NSMutableArray *multiValueArray = [[[NSMutableArray alloc] init] autorelease];
            for(int j=0; j<multiCount; j++) {
                [multiValueArray addObject:(NSString *)ABMultiValueCopyValueAtIndex(multiValue, j)];
            }
            
            if(multiCount > 0) {
                NSPredicate *containPred = [NSPredicate predicateWithFormat:@"SELF contains[cd] %@", [conditionDict objectForKey:@"value"]];
                BOOL match = [containPred evaluateWithObject:[multiValueArray componentsJoinedByString:@" "]];
                if(match == [[conditionDict objectForKey:@"equal"] boolValue]) {
                    result = YES;
                    break;
                }
            }
            
            CFRelease(multiValue);
        }

        if(result) {
            break;
        }
    }

    if(result) {
        return YES;
    }
        
    return result;
}

- (NSDictionary *) _getConditionParts:(NSString *)condition {
    NSArray *conditionParts = [condition componentsSeparatedByString:@"!="];
    BOOL equalFlag = NO;
    NSLog(@"conditionParts count: %d", [conditionParts count]);
    if([conditionParts count] < 2) {
        conditionParts = [condition componentsSeparatedByString:@"="];
        equalFlag = YES;
    }
    NSLog(@"conditionParts count: %d", [conditionParts count]);
    NSLog(@"conditionParts0: %@", [conditionParts description]);
    if([conditionParts count] != 2) {
        return nil;
    }
    
    return [NSDictionary dictionaryWithObjectsAndKeys:[[conditionParts objectAtIndex:0] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]], @"key",
        [NSNumber numberWithBool:equalFlag], @"equal", [[conditionParts objectAtIndex:1] stringByTrimmingCharactersInSet:[NSCharacterSet whitespaceAndNewlineCharacterSet]], @"value", nil];
}

@end
