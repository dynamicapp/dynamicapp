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

#import "DownloadConttroller.h"
#import "Shortcut.h"
#import "define.h"

NSString * const DownloadErrorDomain = @"DownloadErrorDomain";
const NSInteger DownloadErrorCodeResponseIsNotSuccess   = 1;
const NSInteger DownloadErrorCodeFailedToMakeConnection = 2;
const NSInteger DownloadErrorCodeFailedAuthentification = 3;
const NSInteger DownloadErrorCodeFailedReadLastModifiedFile = 4;

NSString * const DownloadErrorInfoStatusCodeKey = @"StatusCode";

@interface DownloadConttroller()
- (void)invokeDelegationWithError:(NSError *)aError;
@end

@implementation DownloadConttroller

@synthesize delegate;
@synthesize urlConnection;
@synthesize fileHandle;
@synthesize filePath;

- (id)initWithRequest:(NSURLRequest *)request {
    self = [super init];
    if(self != nil) {
        self.urlConnection = [[[NSURLConnection alloc] initWithRequest:request delegate:self] autorelease];
    }
    
    return self;
}

- (void)dealloc {
    self.delegate = nil;
    self.urlConnection = nil;
    if (self.fileHandle) {
        [self.fileHandle closeFile];
    }
    
    self.fileHandle = nil;
    self.filePath = nil;
    
    [super dealloc];
}

#pragma mark -
#pragma mark NSURLConnectionDelegate
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    [self invokeDelegationWithError:error];
}

- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    NSInteger aStatusCode = [(NSHTTPURLResponse *)response statusCode];
    if (aStatusCode / 100 != 2) {
        if(304 == aStatusCode) {
            // Not Modified
            [(id)self.delegate performSelectorOnMainThread:@selector(downloadDidFinishWithNoNeed)
                                                withObject:nil
                                             waitUntilDone:NO];
        } else {
            NSDictionary *anInfo = [NSDictionary dictionaryWithObjectsAndKeys:
                                NSLocalizedString(@"Response status is not SUCCESS", @"DownloadController"), NSLocalizedDescriptionKey,
                                [NSNumber numberWithInteger:aStatusCode], DownloadErrorInfoStatusCodeKey,
                                nil];
            NSError *anError = [NSError errorWithDomain:DownloadErrorDomain
                                               code:DownloadErrorCodeResponseIsNotSuccess
                                           userInfo:anInfo];
            [self invokeDelegationWithError:anError];
        }
        return;
    }
    
    NSDateFormatter *df = [[NSDateFormatter alloc] init];  
    df.dateFormat = @"EEE',' dd MMM yyyy HH':'mm':'ss 'GMT'";  
    df.timeZone = [NSTimeZone timeZoneWithAbbreviation:@"GMT"];
    [df release];
 
    NSFileManager *manager = [NSFileManager defaultManager];
    
    NSString *lastModifiedDateString = [[(NSHTTPURLResponse *)response allHeaderFields] objectForKey:@"Last-Modified"];

    [[NSUserDefaults standardUserDefaults] setObject:lastModifiedDateString forKey:LAST_MODIFIED_DATE_KEY];
    [[NSUserDefaults standardUserDefaults] synchronize];
    
    self.filePath = [[Shortcut applicationDocumentsDirectory] stringByAppendingPathComponent:[response suggestedFilename]];
    
    if([manager fileExistsAtPath:self.filePath]) {
        NSError *del_error = nil;
        [manager removeItemAtPath:self.filePath error:&del_error];
        if(del_error != nil) {
            [self invokeDelegationWithError:del_error];
            return;
        }
    }
    [manager createFileAtPath:self.filePath contents:[NSData data] attributes:nil];
    
    self.fileHandle = [NSFileHandle fileHandleForWritingAtPath:self.filePath];
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    if (self.fileHandle) {
        @try {
            [self.fileHandle writeData:data];
        } @catch (NSException *exception) {
        }
    }
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    self.urlConnection = nil;
    if (self.fileHandle) {
        [self.fileHandle closeFile];
        self.fileHandle = nil;
        
        // download done.
        [(id)self.delegate performSelectorOnMainThread:@selector(downloadDidFinish)
                                            withObject:nil
                                         waitUntilDone:NO];
    }
    
}

- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge {
    if ([challenge proposedCredential]) {
        NSError *anError = [challenge error];
        if(!anError) { 
            anError = [NSError errorWithDomain:DownloadErrorDomain
                                               code:DownloadErrorCodeFailedAuthentification
                                           userInfo:nil];
        }
        [self invokeDelegationWithError:anError];
    } else {
        NSString *UserID = [[NSUserDefaults standardUserDefaults] stringForKey:USER_ID_KEY];

        NSString *Password = [[NSUserDefaults standardUserDefaults] stringForKey:PASSWORD_KEY];

        NSURLCredential *credential = [NSURLCredential credentialWithUser:UserID password:Password persistence:NSURLCredentialPersistenceNone];
        [[challenge sender] useCredential:credential forAuthenticationChallenge:challenge];
    }    
}

- (void)invokeDelegationWithError:(NSError *)aError {
	[self cancel];
    [(id)self.delegate performSelectorOnMainThread:@selector(downloadFailedWithError:)
                                        withObject:aError
                                     waitUntilDone:NO];
}

- (void)start {
    NSString *ServerAddress = [[NSUserDefaults standardUserDefaults] stringForKey:SERVER_ADDRESS_KEY];
    if(!ServerAddress || NSOrderedSame == [ServerAddress compare:@""]) {
        [self invokeDelegationWithError:nil];
        return;
    }
    
    NSString *UserID = [[NSUserDefaults standardUserDefaults] stringForKey:USER_ID_KEY];
    if(!UserID || NSOrderedSame == [UserID compare:@""]) {
        [self invokeDelegationWithError:nil];
        return;
    }
    
    NSString *urlString = [NSString stringWithFormat:@"http://%@/~%@/%@", ServerAddress, UserID, ZIP_FILE_NAME];
    NSURL * url = [[NSURL alloc] initWithString:urlString];
    
    NSString *savedLastModified = [[NSUserDefaults standardUserDefaults] stringForKey:LAST_MODIFIED_DATE_KEY];
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
    [request addValue:savedLastModified forHTTPHeaderField: @"If-Modified-Since"];
    
    self.urlConnection = [[[NSURLConnection alloc] initWithRequest:request delegate:self] autorelease];
    
    [url release];
    [request release];
}

- (void)cancel {
    [self.urlConnection cancel];
    self.urlConnection = nil;
    
}
@end
