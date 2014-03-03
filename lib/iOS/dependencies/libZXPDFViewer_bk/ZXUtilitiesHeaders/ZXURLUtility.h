//
//  URLUtility.h
//  GenericContentFrame
//
//  Created by gotow on 10/05/25.
//  Copyright 2010 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface ZXURLUtility : NSObject {

}

+ (NSURL *)coordinatedURLWithPathString:(NSString *)path;
+ (NSURL *)coordinatedURLWithURL:(NSURL *)url;

+ (NSString *)relativePathOfURL:(NSURL *)aFileURL fromBundleRoot:(NSBundle *)aBundle; // used main bundle if aBundle is nil.

@end
