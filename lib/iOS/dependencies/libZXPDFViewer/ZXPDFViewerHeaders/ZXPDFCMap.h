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

@class ZXPDFCMapCIDMap, ZXPDFCMapCIDRangeMap, ZXPDFCMapCodespaceRange, ZXPDFCMapUnicodeMap, ZXPDFCMapUnicodeRangeMap;

@interface ZXPDFCMap : NSManagedObject {
@private
}
@property (nonatomic, strong) NSString * Ordering;
@property (nonatomic, strong) NSString * baseEncoding;
@property (nonatomic, strong) ZXPDFCMap *parentCMap;
@property (nonatomic, strong) NSString * Supplement;
@property (nonatomic, strong) NSString * Registry;
@property (nonatomic, strong) NSNumber * encodingForSimpleFont;
@property (nonatomic, strong) NSString * name;
@property (nonatomic, strong) NSString * useCMap;
@property (nonatomic, strong) NSNumber * wMode;
@property (nonatomic, strong) NSSet* unicodeRangeMaps;
@property (nonatomic, strong) NSSet* codespaceRanges;
@property (nonatomic, strong) NSSet* cidRangeMaps;
@property (nonatomic, strong) NSSet* unicodeMaps;
@property (nonatomic, strong) NSSet* cidMaps;

@end
