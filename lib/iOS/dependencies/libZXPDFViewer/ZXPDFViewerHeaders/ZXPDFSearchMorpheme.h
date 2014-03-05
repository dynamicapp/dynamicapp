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

@class ZXPDFSearchPage;

@interface ZXPDFSearchMorpheme : NSManagedObject {
@private
}
@property (nonatomic, strong) NSNumber * index;
@property (nonatomic, strong) NSString * feature;
@property (nonatomic, strong) NSString * surface;
@property (nonatomic, strong) NSNumber * charType;
@property (nonatomic, strong) NSString * rect;
@property (nonatomic, strong) NSSet *rects; // contains NSString(NSStringFromCGRect)
@property (nonatomic, strong) ZXPDFSearchPage * page;

@end
