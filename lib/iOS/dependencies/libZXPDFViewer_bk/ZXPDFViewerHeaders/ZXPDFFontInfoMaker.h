//
//  ZXPDFFontInfoMaker.h
//  ZyyxLibraries
//
//  Created by gotow on 11/07/13.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZXPDFFontParser.h"


@class ZXPDFFontInfoMaker;

extern NSString * const ZXPDFFontInfoMakerError;
extern const NSInteger ZXPDFFontInfoMakerErrorInvalidArgument;


@protocol ZXPDFFontInfoMakerDelegate <NSObject>
- (void)fontInfoMakerDidFinishProcess:(ZXPDFFontInfoMaker *)aMaker;
- (void)fontInfoMakerDidFailProcess:(ZXPDFFontInfoMaker *)aMaker withError:(NSError *)anError;
@end


@interface ZXPDFFontInfoMaker : NSObject <ZXPDFFontParserDelegate> {

}

@property (nonatomic, strong, readonly) ZXPDFFontParser *parser;
@property (nonatomic, copy, readonly) NSString *fontName;
@property (nonatomic, strong, readonly) NSManagedObjectContext *managedObjectContext;

@property (nonatomic, unsafe_unretained) id<ZXPDFFontInfoMakerDelegate> delegate;

- (id)initWithPDFFontParser:(ZXPDFFontParser *)aParser
                   fontName:(NSString *)aFontName // for ZXPDFFontInfo.name
       managedObjectContext:(NSManagedObjectContext *)aContext; // require ZXPDFCMapDefinition models. (display debug info if nil)

- (void)startProcess;

@end
