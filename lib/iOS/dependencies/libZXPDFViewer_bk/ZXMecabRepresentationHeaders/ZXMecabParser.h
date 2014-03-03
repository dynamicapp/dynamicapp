//
//  ZXMecabParser.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/14.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

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
