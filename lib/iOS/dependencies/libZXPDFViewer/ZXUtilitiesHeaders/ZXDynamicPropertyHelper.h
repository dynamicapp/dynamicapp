//
//  DynamicPropertyHelper.h
//  ZyyxLibraries
//
//  Created by gotow on 10/11/26.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

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
