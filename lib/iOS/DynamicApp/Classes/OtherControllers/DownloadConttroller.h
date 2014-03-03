//
//  DownloadConttroller.h
//  DynamicApp
//
//  Created by ZYYX on 12/02/10.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>

@protocol DownloadControllerDelegate <NSObject>
- (void)downloadFailedWithError:(NSError *)anError;
- (void)downloadDidFinish;
- (void)downloadDidFinishWithNoNeed;

@optional
- (void)downloadProgressForList:(float)aProgress;
- (void)downloadProgressForFile:(float)aProgress;
@end


@interface DownloadConttroller : NSObject<NSURLConnectionDelegate> {
    NSURLConnection *urlConnection;
    NSFileHandle *fileHandle;
    id<DownloadControllerDelegate> delegate;
    NSString *filePath;
}

@property (assign) id<DownloadControllerDelegate> delegate;
@property (nonatomic, retain) NSURLConnection * urlConnection;
@property (nonatomic, retain) NSFileHandle * fileHandle;
@property (nonatomic, retain) NSString *filePath;

- (void)start;
- (id)initWithRequest:(NSURLRequest *)request;
- (void)cancel;
@end
