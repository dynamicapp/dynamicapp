//
//  Queue.h
//  SocketConnectTest
//
//  Created by gotow on 10/08/31.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface ZXSimpleQueue : NSObject {
    NSMutableArray *storage;
}

@property (nonatomic, copy, readonly) NSMutableArray *storage;


- (id)initWithArray:(NSArray *)anArray;
+ (id)queue;
+ (id)queueWithArray:(NSArray *)anArray;

- (void)pushObject:(id)anObject;
- (void)unpopObject:(id)anObject;
- (BOOL)hasNext;
- (id)peekNext;
- (id)popObject;
- (void)removeAllObjects;

- (NSArray *)arrayByCopying;


@end
