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
