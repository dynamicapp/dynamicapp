//
//  ZXMecabNode.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/14.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface ZXMecabNode : NSObject <NSCoding> {

}

@property (nonatomic, strong, readonly) NSString *surface;
@property (nonatomic, strong, readonly) NSString *feature;
@property (nonatomic, strong, readonly) NSNumber *charType;

- (id)initWithMecabNode:(const void *)mecabNode;
- (id)initWithSurface:(NSString *)aSurface
              feature:(NSString *)aFeature
             charType:(NSNumber *)aCharType;

- (NSString *)description;
- (BOOL)isEqual:(id)object;

@end
