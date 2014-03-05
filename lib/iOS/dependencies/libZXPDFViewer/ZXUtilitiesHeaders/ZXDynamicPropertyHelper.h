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


/**
 * DynamicPropertyHelper support only id (or specific class) type.
 * USAGE:
 * 1. make sub-class of this class. (helper class)
 * 2. override helper class method 'propertyAccessors',
 *    it may return names of getter and setter if needed.
 *    (for setter name, use helper class's method 'setterNameForProperty:')
 * 3. in target class, declare property of helper class,
 *    and declare 'forwardingTargetForSelector:' with returning it if acceptable.
 *    (for checking acceptable, use helper class's 'acceptableForSelector:'
 * 4. in target class, declare 'valueForUndefinedKey:' and 'setValue:forUndefinedKey:'.
 *    (typically, forwarding to valueForKey or setValue:forKey:)
 *
 * example for sub-class of DynamicPropertyHelper:
 *
 * + (NSArray *)propertyAccessors {
 *     return [NSArray arrayWithObjects:
 *             @"foo",
 *             [SubClass setterNameForProperty:@"foo"],
 *             @"bar",
 *             nil];
 * }
 *
 *
 * example for target class:
 *  self.helper has instance of DynamicPropertyHelper's sub-class.
 *
 * - (id)forwardingTargetForSelector:(SEL)aSelector {
 *     if ([[self.helper class] acceptableForSelector:aSelector]) {
 *         return self.helper;
 *     } else {
 *         return nil;
 *     }
 * }
 *
 * - (id)valueForUndefinedKey:(NSString *)key {
 *     return [self.helper valueForKey:key];
 * }
 *
 * - (void)setValue:(id)value forUndefinedKey:(NSString *)key {
 *     [self.helper setValue:value forKey:key];
 * }
 */


@interface ZXDynamicPropertyHelper : NSObject {
    NSMutableDictionary *storage;
}

// should not touch this property typically.
@property (nonatomic, strong) NSMutableDictionary *storage;

+ (id)helper;

+ (NSArray *)propertyAccessors; // abstruct, must be override.
+ (NSString *)setterNameForProperty:(NSString *)aPropertyName;
+ (BOOL)acceptableForSelector:(SEL)aSelectorOfProperty;

@end
