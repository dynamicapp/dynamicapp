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

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class ZXPDFSearchResultDocument, ZXPDFSearchResultPage;

@interface ZXPDFSearchWord : NSManagedObject {
@private
}
@property (nonatomic, strong) NSArray *mecabNodes;
@property (nonatomic, strong) NSNumber * maxHits;
@property (nonatomic, strong) NSDate * timestamp;
@property (nonatomic, strong) NSNumber * completed;
@property (nonatomic, strong) NSString * word;
@property (nonatomic, strong) NSSet* resultPages;
@property (nonatomic, strong) NSSet* resultDocuments;

@end
