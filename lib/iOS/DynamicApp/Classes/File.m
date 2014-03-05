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

#import "File.h"
#import "Shortcut.h"
#import "PluginResult.h"
#import "NSData+Base64.h"
#import <MobileCoreServices/MobileCoreServices.h>
#import "define.h"

@interface File(private)
- (BOOL) canCopyMoveSrc:(NSString *)src ToDestination:(NSString *)dest;
- (void) doCopyMove:(NSDictionary *)arguments withDict:(NSDictionary *)options isCopy:(BOOL)bCopy;
- (NSString *) getMimeTypeFromPath:(NSString *)fullPath;
@end


@implementation File


- (void) getMetadata:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    BOOL isDirectory = NO;
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    
    NSString *filename = [arguments objectForKey:@"filename"] != [NSNull null] ? [arguments objectForKey:@"filename"] : @"";
    NSString *parentPath = [arguments objectForKey:@"parentPath"] != [NSNull null] ? [arguments objectForKey:@"parentPath"] : @"";
    parentPath = [DOCUMENTS_SCHEME_PREFIX stringByAppendingString:parentPath];
    
    NSString *fullPath = [DynamicAppPlugin pathForResource:[NSString stringWithFormat:@"%@/%@", parentPath, filename]];
    
    PluginResult *result = nil;
    NSString *jsString = nil;
    NSFileManager *fileMgr = [NSFileManager defaultManager];
    
    if([fileMgr fileExistsAtPath:fullPath isDirectory:&isDirectory]) {
        NSError *error = nil;
        NSNumber *size = 0;
        if(!isDirectory) {
            NSDictionary *fileAttributes = [fileMgr attributesOfItemAtPath:fullPath error:&error];
            size = [NSNumber numberWithUnsignedLongLong:[fileAttributes fileSize]];
        }
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:fullPath, @"fullPath", [NSString stringWithFormat:@"%d", isDirectory ? 0 : 1], @"kind", [self getMimeTypeFromPath:fullPath], @"type", size, @"size", nil]];
        jsString = [result toSuccessCallbackString:theCallbackId];
    } else {
		result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:NOT_FOUND_ERR]; 
		jsString = [result toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) create:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    NSString *filename = [arguments objectForKey:@"filename"] != [NSNull null] ? [arguments objectForKey:@"filename"] : @"";
    NSString *parentPath = [arguments objectForKey:@"parentPath"] != [NSNull null] ? [arguments objectForKey:@"parentPath"] : @"";
    parentPath = [DOCUMENTS_SCHEME_PREFIX stringByAppendingString:parentPath];
    
    BOOL isDirectory = NO;
    PluginResult *result = nil;
    NSString *jsString = nil;
    NSFileManager *fileMgr = [NSFileManager defaultManager];
    NSError *error = nil;
    
    NSString *fullPath = [DynamicAppPlugin pathForResource:[NSString stringWithFormat:@"%@/%@", parentPath, filename]];
    
    if(![fileMgr fileExistsAtPath:fullPath isDirectory:&isDirectory]) {
        
        if([fileMgr createDirectoryAtPath:[fullPath stringByDeletingLastPathComponent] withIntermediateDirectories:YES attributes:nil error:&error] ) {
            [fileMgr createFileAtPath:fullPath contents:nil attributes:nil];
            
            if([fileMgr fileExistsAtPath:fullPath isDirectory:&isDirectory]) {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:fullPath, @"fullPath", [NSString stringWithFormat:@"%d", isDirectory ? 0 : 1], @"kind", [self getMimeTypeFromPath:fullPath], @"type", [NSNumber numberWithInt:0], @"size", nil]];
                jsString = [result toSuccessCallbackString:theCallbackId];
            } else {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:ABORT_ERR]; 
                jsString = [result toErrorCallbackString:theCallbackId];
            }
        } else {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:INVALID_MODIFICATION_ERR]; 
            jsString = [result toErrorCallbackString:theCallbackId];
        }
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:PATH_EXISTS_ERR]; 
        jsString = [result toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}


/* Copy/move a file or directory to a new location
 *
 */
- (void) doCopyMove:(NSDictionary *)arguments withDict:(NSDictionary *)options isCopy:(BOOL)bCopy
{
	NSString *theCallbackId = [options objectForKey:@"callbackId"];
        
	// arguments
    NSString *srcFullPath = [arguments objectForKey:@"fullPath"];
    NSString *newName = [arguments objectForKey:@"newName"];
    
	PluginResult *result = nil;
	NSString *jsString = nil;
	FileError errCode = 0;  // !! Currently 0 is not defined, use this to signal error !!
   
    NSString *targetDirectory = [DynamicAppPlugin pathForResource:[NSString stringWithFormat:@"%@/", [DOCUMENTS_SCHEME_PREFIX stringByAppendingString:[arguments objectForKey:@"targetDirectory"]]]];
    
	if (!targetDirectory) {
		// no destination provided
		errCode = NOT_FOUND_ERR;
	} else if ([newName rangeOfString: @":"].location != NSNotFound) {
		// invalid chars in new name
		errCode = ENCODING_ERR;
	} else {
		NSString *newFullPath = [targetDirectory stringByAppendingPathComponent:newName];
		if ( [newFullPath isEqualToString:srcFullPath] ){
			// source and destination can not be the same 
			errCode = INVALID_MODIFICATION_ERR;
		} else {
			NSFileManager *fileMgr = [NSFileManager defaultManager];
            NSError *error = nil;
			
			BOOL bSrcIsDir = NO;
			BOOL bDestIsDir = NO;
			BOOL bNewIsDir = NO;
			BOOL bSrcExists = [fileMgr fileExistsAtPath:srcFullPath isDirectory: &bSrcIsDir];
			BOOL bDestExists= [fileMgr fileExistsAtPath:targetDirectory isDirectory: &bDestIsDir];
			BOOL bNewExists = [fileMgr fileExistsAtPath:newFullPath isDirectory: &bNewIsDir];
            
            if(!bDestExists) {
                if([fileMgr createDirectoryAtPath:targetDirectory withIntermediateDirectories:YES attributes:nil error:&error]) {
                    bDestExists = YES;
                } else {
                    bDestExists = NO;
                }
            }
            
			if (!bSrcExists || !bDestExists) {
				// the source or the destination root does not exist
				errCode = NOT_FOUND_ERR;
			} else if (bSrcIsDir && (bNewExists && !bNewIsDir)) {
				// can't copy/move dir to file 
				errCode = INVALID_MODIFICATION_ERR;
			} else { // no errors yet
				BOOL bSuccess = NO;
				if (bCopy){
					if (bSrcIsDir && ![self canCopyMoveSrc:srcFullPath ToDestination:newFullPath]) {
						// can't copy dir into self
						errCode = INVALID_MODIFICATION_ERR;
					} else if (bNewExists) {
						// the full destination should NOT already exist if a copy
						errCode = PATH_EXISTS_ERR;
					}  else {
						bSuccess = [fileMgr copyItemAtPath:srcFullPath toPath:newFullPath error:&error];
					}
				} else { // move 
					// iOS requires that destination must not exist before calling moveTo
					// is W3C INVALID_MODIFICATION_ERR error if destination dir exists and has contents
					// 
					if (!bSrcIsDir && (bNewExists && bNewIsDir)){
						// can't move a file to directory
						errCode = INVALID_MODIFICATION_ERR;
					} else if (bSrcIsDir && ![self canCopyMoveSrc:srcFullPath ToDestination:newFullPath] ) { 
                        // can't move a dir into itself
						errCode = INVALID_MODIFICATION_ERR;	
					} else if (bNewExists) {
						if (bNewIsDir && [[fileMgr contentsOfDirectoryAtPath:newFullPath error: NULL] count] != 0){
							// can't move dir to a dir that is not empty
							errCode = INVALID_MODIFICATION_ERR;
							newFullPath = nil;  // so we won't try to move
						} else {
							// remove destination so can perform the moveItemAtPath
							bSuccess = [fileMgr removeItemAtPath:newFullPath error: NULL];
							if (!bSuccess) {
								errCode = INVALID_MODIFICATION_ERR; // is this the correct error?
								newFullPath = nil;
							}
						}
					} else if (bNewIsDir && [newFullPath hasPrefix:srcFullPath]) {
						// can't move a directory inside itself or to any child at any depth;
						errCode = INVALID_MODIFICATION_ERR;
						newFullPath = nil;
					}
                    
					if (newFullPath != nil) {
						bSuccess = [fileMgr moveItemAtPath:srcFullPath toPath:newFullPath error:&error];
					}
				}
				if (bSuccess) {
					result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:targetDirectory, @"newParentPath", newName, @"newFilename", newFullPath, @"newFullPath", nil]];
					jsString = [result toSuccessCallbackString:theCallbackId];
				}
				else {
                    if(errCode == 0) {
                        errCode = INVALID_MODIFICATION_ERR; // catch all
                    }
					if (error) {
						if ([error code] == NSFileReadUnknownError || [error code] == NSFileReadTooLargeError) {
							errCode = NOT_READABLE_ERR;
						} else if ([error code] == NSFileWriteOutOfSpaceError){
							errCode = QUOTA_EXCEEDED_ERR;
						} else if ([error code] == NSFileWriteNoPermissionError){
							errCode = NO_MODIFICATION_ALLOWED_ERR;
						}
					}
				}			
			}
		}
	}
    
	if (errCode > 0) {
		result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt:errCode];
        
		jsString = [result toErrorCallbackString:theCallbackId];
	}
	
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
	if (jsString){
		[self.webView stringByEvaluatingJavaScriptFromString:jsString];
	}
}

- (void) copy:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    [self doCopyMove:arguments withDict:options isCopy:YES];
}

- (void) move:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    [self doCopyMove:arguments withDict:options isCopy:NO];
}

/* remove the file or directory (recursively)
 *
 */
+ (FileError) doRemove:(NSDictionary *)arguments {
	NSError *pError = nil;
	NSFileManager *fileMgr = [NSFileManager defaultManager];
    FileError errorCode = 0;
    
	@try {
		if (![fileMgr removeItemAtPath:[arguments objectForKey:@"fullPath"] error:&pError]) {
			// see if we can give a useful error
			errorCode = ABORT_ERR;
			if ([pError code] == NSFileNoSuchFileError) {
				errorCode = NOT_FOUND_ERR;
			} else if ([pError code] == NSFileWriteNoPermissionError) {
				errorCode = NO_MODIFICATION_ALLOWED_ERR;
			}
		}
	} @catch (NSException* e) { // NSInvalidArgumentException if path is . or ..
        errorCode = SYNTAX_ERR;
    } @finally {
		return errorCode;
	}
}

/* removes the directory or file entry
 *
 */
- (void) remove:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
	NSString *theCallbackId = [options objectForKey:@"callbackId"];
    // arguments
	NSString *fullPath = [arguments objectForKey:@"fullPath"];
	
	PluginResult *result = nil;
	NSString *jsString = nil;
	FileError errorCode = 0;
    
    NSString *appDocsPath = [Shortcut applicationDocumentsDirectory];
    NSString *appTempPath = [NSTemporaryDirectory() stringByStandardizingPath];
    
	// error if try to remove top level (documents or tmp) dir
	if ([fullPath isEqualToString:appDocsPath] || [fullPath isEqualToString:appTempPath]) {
		errorCode = NO_MODIFICATION_ALLOWED_ERR;
	} else {
		NSFileManager* fileMgr = [NSFileManager defaultManager];
		BOOL bIsDir = NO;
		if (![fileMgr fileExistsAtPath:fullPath isDirectory:&bIsDir]){
			errorCode = NOT_FOUND_ERR;
		}
		if (bIsDir && [[fileMgr contentsOfDirectoryAtPath:fullPath error: nil] count] != 0) {
			// dir is not empty
			errorCode = INVALID_MODIFICATION_ERR;
		}
	}
	if (errorCode > 0) {
		result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt: errorCode];
		jsString = [result toErrorCallbackString:theCallbackId];
	} else {
		// perform actual remove
		errorCode = [File doRemove:arguments];
        
        if(!errorCode) {
			result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
			jsString = [result toSuccessCallbackString:theCallbackId];
        } else {
			result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR messageAsInt:errorCode];
			jsString = [result toErrorCallbackString:theCallbackId];
        }
	}
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
	[self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

/* recurvsively removes the directory 
 *
 */
- (void) removeRecursively:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
	NSString* theCallbackId = [options objectForKey:@"callbackId"];
    
    // arguments
	NSString* fullPath = [arguments objectForKey:@"fullPath"];
	
	PluginResult* result = nil;
	NSString* jsString = nil;
	FileError errorCode = 0;
    
    NSString *appDocsPath = [Shortcut applicationDocumentsDirectory];
    NSString *appTempPath = [NSTemporaryDirectory() stringByStandardizingPath];
    
	// error if try to remove top level (documents or tmp) dir
	if ([fullPath isEqualToString:appDocsPath] || [fullPath isEqualToString:appTempPath]) {
		result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt: NO_MODIFICATION_ALLOWED_ERR];// cast:@"window.localFileSystem._castError"];
		jsString = [result toErrorCallbackString:theCallbackId];
	} else {
		// perform actual remove
		errorCode = [File doRemove:arguments];
        
        if(!errorCode) {
			result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
			jsString = [result toSuccessCallbackString:theCallbackId];
        } else {
			result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR messageAsInt:errorCode];
			jsString = [result toErrorCallbackString:theCallbackId];
        }
	}
	
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
	[self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

/* helper function to check to see if the user attempted to copy an entry into its parent without changing its name, 
 * or attempted to copy a directory into a directory that it contains directly or indirectly.
 *
 */	
-(BOOL) canCopyMoveSrc: (NSString*) src ToDestination: (NSString*) dest {
    // This weird test is to determine if we are copying or moving a directory into itself.  
    // Copy /Documents/myDir to /Documents/myDir-backup is okay but
    // Copy /Documents/myDir to /Documents/myDir/backup not okay
    BOOL copyOK = YES;
    NSRange range = [dest rangeOfString:src];
    
    if (range.location != NSNotFound) {
        NSRange testRange = {range.length-1, ([dest length] - range.length)};
        NSRange resultRange = [dest rangeOfString: @"/" options: 0 range: testRange];
        if (resultRange.location != NSNotFound){
            copyOK = NO;
        }
    }
    return copyOK;
}

/* Helper function to get the mimeType from the file extension
 *
 */
-(NSString *) getMimeTypeFromPath:(NSString *)fullPath {	
	
	NSString *mimeType = nil;
	if(fullPath) {
		CFStringRef typeId = UTTypeCreatePreferredIdentifierForTag(kUTTagClassFilenameExtension, (CFStringRef)[fullPath pathExtension], NULL);
		if (typeId) {
			mimeType = (NSString *)UTTypeCopyPreferredTagWithClass(typeId,kUTTagClassMIMEType);
			if (mimeType) {
				[mimeType autorelease];
			} else {
                // special case for m4a
                if ([(NSString*)typeId rangeOfString: @"m4a-audio"].location != NSNotFound){
                    mimeType = @"audio/mp4";
                } else if ([[fullPath pathExtension] rangeOfString:@"wav"].location != NSNotFound){
                    mimeType = @"audio/wav";
                }
            }
			CFRelease(typeId);
		}
	}
	return mimeType;
}

- (void) dealloc {
	[super dealloc];
}

@end

@implementation FileReader

- (void) read:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
	NSString *fullPath = [arguments objectForKey:@"fullPath"];
    
    NSFileManager *fileMgr = [NSFileManager defaultManager];
    PluginResult *result = nil;
	NSString *jsString = nil;
    NSError *error = nil;
    BOOL bIsDir;
    
    if([fileMgr fileExistsAtPath:fullPath isDirectory:&bIsDir]) {
        if(!bIsDir) {
            NSString *mimeType = [self getMimeTypeFromPath:fullPath];
            NSString *fileContents = nil;
            if([mimeType rangeOfString:@"text"].location != NSNotFound) {
                NSStringEncoding encoding;
                
                fileContents = [NSString stringWithContentsOfFile:fullPath usedEncoding:&encoding error:&error];
                if(error) {
                    result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt: NOT_READABLE_ERR];
                    jsString = [result toErrorCallbackString:theCallbackId];
                }
            } else {
                NSFileHandle *file = [NSFileHandle fileHandleForReadingAtPath:fullPath];
                NSData *readData = [file readDataToEndOfFile];
                [file closeFile];
                
                if (readData) {
                    fileContents = [NSString stringWithFormat:@"data:%@;base64,%@", mimeType, [readData base64EncodedString]];
                } else {
                    fileContents = @"";
                }
            }
            
            if(fileContents != nil) {
                result = [PluginResult resultWithStatus: DynamicAppCommandStatus_OK messageAsString:[fileContents stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
                jsString = [result toSuccessCallbackString:theCallbackId];
            }
        } else {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt: TYPE_MISMATCH_ERR];
            jsString = [result toErrorCallbackString:theCallbackId];
        }
    } else {
		result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsInt: NOT_FOUND_ERR];
		jsString = [result toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) dealloc {
	[super dealloc];
}

@end


@interface FileWriter(private)
- (UInt64) truncateFile:(NSString *)fullPath atPosition:(UInt64)position;
@end

@implementation FileWriter

- (void) write:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
	NSString *fullPath = [arguments objectForKey:@"fullPath"];
    NSString *data = [arguments objectForKey:@"data"];
    UInt64 position = (UInt64)[[arguments objectForKey:@"position"] unsignedLongLongValue];
    
    PluginResult *result = nil;
    NSString *jsString = nil;
    	
    NSFileManager *fileMgr = [NSFileManager defaultManager];
    
    if([fileMgr isWritableFileAtPath:fullPath]) {
        [self truncateFile:fullPath atPosition:position];
        if([self writeToFile:fullPath withData:data append:YES atPosition:&position]) {            
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:[NSNumber numberWithUnsignedLongLong:position], @"size", nil]];
            jsString = [result toSuccessCallbackString:theCallbackId];
        }
    }
    
    if(!jsString) {
		result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR messageAsInt: INVALID_MODIFICATION_ERR];
		jsString = [result toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (UInt64) truncateFile:(NSString *)fullPath atPosition:(UInt64)position {
    UInt64 newPosition = 0UL;
	
	NSFileHandle *file = [NSFileHandle fileHandleForWritingAtPath:fullPath];
	if(file) {
		[file truncateFileAtOffset:(UInt64)position];
		newPosition = [file offsetInFile];
		[file synchronizeFile];
		[file closeFile];
	}
	return newPosition;
}

- (BOOL) writeToFile:(NSString *)fullPath withData:(NSString *)data append:(BOOL)shouldAppend atPosition:(UInt64 *)position {
	int bytesWritten = 0;
    data = [data stringByReplacingOccurrencesOfString:@"/n" withString:@"\n"];
	NSData* encData = [data dataUsingEncoding:NSUTF8StringEncoding allowLossyConversion:NO];
    BOOL retVal = NO;
    
	if (fullPath) {
		NSOutputStream* fileStream = [NSOutputStream outputStreamToFileAtPath:fullPath append:shouldAppend];
		if (fileStream) {
			[fileStream open];
			bytesWritten = [fileStream write:[encData bytes] maxLength:[encData length]];
			[fileStream close]; 
            
			if (bytesWritten > 0) {
                *position += bytesWritten;
                retVal = YES;
			}
		}
	}
    return  retVal;
}

- (void) dealloc {
	[super dealloc];
}

@end