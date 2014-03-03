//
//  ZXCoreDataRepresentationBase.h
//  ZyyxLibraries
//
//  Created by gotow on 11/06/24.
//  Copyright 2011 ZYYX Inc. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface ZXCoreDataRepresentationBase : NSObject {
    
}

@property (nonatomic, strong) NSPersistentStoreCoordinator *persistentStoreCoordinator;
@property (unsafe_unretained, nonatomic, readonly) NSManagedObjectModel *managedObjectModel; // shortcut of [self.persistentStoreCoordinator managedObjectModel]
@property (nonatomic, strong) NSManagedObjectContext *managedObjectContext; // create new from coordinator automatically if nil.

// override these on subclass if need to change from default behavior to new one.
+ (NSURL *)modelFileURL;    // "{ClassName}.mom(d)" in bundle for its class.
+ (NSURL *)defaultStoreURL; // "{ClassName}.sqlite" in cache directory.
+ (NSDictionary *)defaultPersistentStoreCoordinatorOptions; // simple automatic migration setting.
                                                            // NSMigratePersistentStoresAutomaticallyOption=YES
                                                            // NSInferMappingModelAutomaticallyOption=YES
+ (BOOL)shouldRemoveStoreFileAndCreateNewWhenError; // if return YES, remove store file and create new one automatically
                                                    // when error occured in 'persistentStoreCoordinatorWithStoreURL:'.
                                                    // default (in this class) is NO;


// utility
+ (NSManagedObjectModel *)managedObjectModel;
+ (NSPersistentStoreCoordinator *)persistentStoreCoordinatorWithStoreURL:(NSURL *)aStoreURL;

// initializer
- (id)initWithPersistentStoreURL:(NSURL *)aPersistentStoreURL;
- (id)initWithPersistentStoreCoordinator:(NSPersistentStoreCoordinator *)aCoordinator;

@end
