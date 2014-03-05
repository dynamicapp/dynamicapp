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
