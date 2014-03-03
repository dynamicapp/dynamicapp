//
//  ZXPDFCMapDefinition.h
//  ZyyxLibraries
//
//  Created by gotow on 11/05/30.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>
#import "ZXCoreDataRepresentationBase.h"
#import "ZXPDFCMap.h"
#import "ZXPDFFontInfo.h"


@interface ZXPDFCMapDefinition : ZXCoreDataRepresentationBase {

}

+ (id)definitionWithStoreURL:(NSURL *)aStoreFileURL; // NOTICE: would delete the file and create new one
                                                     // if an error occured in creating PersistentStoreCoordinator.
                                                     // e.g. the file has invalid model version hash.
                                                     // to avoid this behavior, use 'definitionWithPersistentStoreCoordinator:'.
+ (id)definitionWithPersistentStoreCoordinator:(NSPersistentStoreCoordinator *)aCoordinator;
+ (id)predefinedDefinition; // persistentStoreCoordinator is readonly.

- (ZXPDFCMap *)cMapWithName:(NSString *)aCMapName;
- (ZXPDFFontInfo *)fontInfoWithName:(NSString *)aFontName;

@end
