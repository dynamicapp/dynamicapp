//
//  Database.m
//  DynamicApp
//
//  Created by ZYYX on 6/19/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//
#define _GNU_SOURCE

#import "Database.h"
#import "Shortcut.h"
#import "PluginResult.h"


@implementation Database

@synthesize databaseHandle;

- (void)init:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    PluginResult *result = nil;
	NSString* jsString = nil;
    
    NSString *dbName = [arguments objectForKey:@"dbName"];
    NSString *dbPath = [[Shortcut applicationDocumentsDirectory] stringByAppendingPathComponent:@"db"];
    
    NSFileManager *fileMgr = [NSFileManager defaultManager];
    if(![fileMgr fileExistsAtPath:dbPath]) {
        [fileMgr createDirectoryAtPath:dbPath withIntermediateDirectories:YES attributes:nil error:nil];
    }
    
    dbPath = [dbPath stringByAppendingPathComponent:dbName];
    
    if(self.databaseHandle != NULL) {
        sqlite3_close(self.databaseHandle);
    }
    
    if (sqlite3_open([dbPath UTF8String], &databaseHandle) == SQLITE_OK) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
        jsString = [result toSuccessCallbackString:theCallbackId];
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR messageAsInt:sqlite3_errcode(self.databaseHandle)];
        jsString = [result toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)executeSQL:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    PluginResult *result = nil;
	NSString* jsString = nil;
    
    const char *sqlQuery = [[arguments objectForKey:@"sql"] UTF8String];
    sqlite3_stmt *sqlStatement;
    int numResultColumns = 0;
    
    @try {
        if(self.databaseHandle != nil) {
            NSMutableArray *resultSet = [[NSMutableArray alloc] init];
            if(sqlite3_prepare_v2(self.databaseHandle, sqlQuery, -1, &sqlStatement, NULL) == SQLITE_OK) {
                numResultColumns = sqlite3_column_count(sqlStatement);
                NSMutableArray *keys = [[NSMutableArray alloc] init];
                
                if([[arguments objectForKey:@"isSelectQuery"] boolValue]) {
                    for(int i=0;i<numResultColumns;i++) {
                        [keys addObject:[NSString stringWithFormat:@"%s", sqlite3_column_name(sqlStatement, i)]];
                    }
                }
                
                while (sqlite3_step(sqlStatement) == SQLITE_ROW) {                
                    NSMutableArray* row = [[NSMutableArray alloc] init];
                    for(int i=0;i<numResultColumns;i++) {
                        int type = sqlite3_column_type(sqlStatement, i);
                        switch(type) {
                            case SQLITE_INTEGER:
                                [row addObject: [NSNumber numberWithInt:sqlite3_column_int(sqlStatement, i)]];
                                break;
                            case SQLITE_FLOAT:
                                [row addObject:[NSNumber numberWithFloat:sqlite3_column_double(sqlStatement, i)]];
                                break;
                            case SQLITE_TEXT:
                                [row addObject:[NSString stringWithUTF8String:(char *)sqlite3_column_text(sqlStatement, i)]];
                                break;
                            case SQLITE_BLOB:
                                [row addObject:[NSData dataWithBytes:sqlite3_column_blob(sqlStatement, i) length:sqlite3_column_bytes(sqlStatement, i)]];
                                break;
                            default:  // SQLITE_NULL
                                [row addObject: @"null"];
                                break;
                        }
                    }
                    
                    [resultSet addObject:[NSDictionary dictionaryWithObjects:row forKeys:keys]];
                    [row release];
                }
                [keys release];
                
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsArray:resultSet];
                jsString = [result toSuccessCallbackString:theCallbackId];
            }
            sqlite3_finalize(sqlStatement);
            [resultSet release];
        } 
    }
    @catch (NSException *exception) { }
    
    if(jsString == nil) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR messageAsInt:sqlite3_errcode(self.databaseHandle)];
        jsString = [result toErrorCallbackString:theCallbackId];
    }
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)dealloc {
    sqlite3_close(self.databaseHandle);
    [super dealloc];
}

@end
