//
//  CoWorker.h
//  ZyyxLibraries
//
//  Created by gotow on 10/05/10.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface ZXCoWorker : NSThread {
    NSOperationQueue *operationQueue;
}

@property (strong) NSOperationQueue *operationQueue;


+ (ZXCoWorker *)worker;
+ (void)stopWorker;

+ (void)addOperation:(NSOperation *)operation;
+ (void)cancelAllOperations;

@end
