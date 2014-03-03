//
//  File.h
//  DynamicApp
//
//  Created by ZYYX on 2/7/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"

enum FileError {
    NO_ERR = 0,
	NOT_FOUND_ERR = 1,
    SECURITY_ERR = 2,
    ABORT_ERR = 3,
    NOT_READABLE_ERR = 4,
    ENCODING_ERR = 5,
    NO_MODIFICATION_ALLOWED_ERR = 6,
    INVALID_STATE_ERR = 7,
    SYNTAX_ERR = 8,
    INVALID_MODIFICATION_ERR = 9,
    QUOTA_EXCEEDED_ERR = 10,
    TYPE_MISMATCH_ERR = 11,
    PATH_EXISTS_ERR = 12
};
typedef int FileError;

@interface File : DynamicAppPlugin {
}

- (void) getMetadata:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) create:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) copy:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) move:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) remove:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (void) removeRecursively:(NSDictionary *)arguments withOptions:(NSDictionary *)options;

+ (FileError) doRemove:(NSDictionary *)arguments;

@end

@interface FileReader : File {
}

- (void) read:(NSDictionary *)arguments withOptions:(NSDictionary *)options;

@end

@interface FileWriter : File {
}

- (void) write:(NSDictionary *)arguments withOptions:(NSDictionary *)options;
- (BOOL) writeToFile:(NSString *)fullPath withData:(NSString *)data append:(BOOL)shouldAppend  atPosition:(UInt64 *)position;

@end