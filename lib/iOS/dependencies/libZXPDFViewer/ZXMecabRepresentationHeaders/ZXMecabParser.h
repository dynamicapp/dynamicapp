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


extern NSString * const ZXMecabParserError;
extern const NSInteger ZXMecabParserErrorCodeNoMecabObject;
extern const NSInteger ZXMecabParserErrorCodeMecabParseFailed;
extern const NSInteger ZXMecabParserErrorCodeErrorOnDelegate; // contains an error from delegate in NSUnderlyingErrorKey of userInfo.


@class ZXMecabParser;
@class ZXMecabNode;

@protocol ZXMecabParserDelegate <NSObject>
- (NSError *)mecabParser:(ZXMecabParser *)aParser didGetNode:(ZXMecabNode *)aNode; // typically return nil.
                                                                                   // stop parsing if return an error object,
                                                                                   // its error used by parseString:returningErrorTo:
@end



@interface ZXMecabParser : NSObject {

}

@property (nonatomic, unsafe_unretained) id<ZXMecabParserDelegate> delegate;

// default is '/usr/lib/dic/ja/tok' (this is directory).
// LIMITATION: should set same (UTF-16LE) encoding dictionary.
+ (NSString *)mecabDictionaryPath;
+ (void)setMecabDictionaryPath:(NSString *)aPath; // reset to default if set nil.

+ (NSString *)mecabVersion; // represent mecab_version().

+ (id)parser;


/**
 * parseString:returningErrorTo:
 * return NO and set error object to errorPlace if any error occuered.
 * if aString is nil or empty, return YES without processing.
 */
- (BOOL)parseString:(NSString *)aString returningErrorTo:(NSError **)errorPlace;

@end
