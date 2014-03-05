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

#import "Encryptor.h"
#import "NSData+Base64.h"
#import "PluginResult.h"
#import <CommonCrypto/CommonDigest.h>
#import "define.h"

@interface Encryptor(private)
- (unsigned char *)md5HexDigestEncryptArray:(NSString*)input;
@end


@implementation Encryptor

- (void)encryptText:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theText = [arguments objectForKey:@"text"];
    PluginResult *result = nil;
    NSString *jsString = nil;
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    
    const char *chars = [[theText stringByReplacingOccurrencesOfString:@"/n" withString:@"\n"] UTF8String];
    int length = strlen(chars);
    
    NSMutableString *encryptedText = [[NSMutableString alloc] init];
    unsigned char *encryptArray = [self md5HexDigestEncryptArray:TEXT_ENCRYPT_MD5KEY];
    
    for(int i=0; i<length; i++) {
        [encryptedText appendFormat:@"%02x", (chars[i] & 0x00FF) ^ encryptArray[i%1024]];
    }
    free(encryptArray);
    
    if(encryptedText) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:encryptedText, @"encText", nil]];
        jsString = [result toSuccessCallbackString:theCallbackId];
    } else {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:@"Encryption error", @"msg", [NSNumber numberWithInt:0], @"code", nil]];
        jsString = [result toErrorCallbackString:theCallbackId];
    }
    [encryptedText release];   
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void)decryptText:(NSDictionary *)arguments withOptions:(NSDictionary *)options {
    NSString *theText = [arguments objectForKey:@"text"];
    PluginResult *result = nil;
    NSString *jsString = nil;
    NSString *theCallbackId = [options objectForKey:@"callbackId"];
    
    NSMutableData *stringData = [[NSMutableData alloc] init];
    char byte_chars[3] = {'\0','\0','\0'};    
    unsigned char *encryptArray = [self md5HexDigestEncryptArray:TEXT_ENCRYPT_MD5KEY];
    
    for (int i=0; i < [theText length] / 2; i++) {
        byte_chars[0] = [theText characterAtIndex:i*2];
        byte_chars[1] = [theText characterAtIndex:i*2+1];
        char whole_byte = strtol(byte_chars , NULL, 16);
        whole_byte ^= encryptArray[i%1024];
        [stringData appendBytes:&whole_byte length:1]; 
    }
    free(encryptArray);
    
    NSString *decryptedText = [[NSString alloc] initWithData:stringData encoding:NSUTF8StringEncoding];
    
    if(decryptedText) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:decryptedText, @"decText", nil]];
        jsString = [result toSuccessCallbackString:theCallbackId];
    } else  {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsDictionary:[NSDictionary dictionaryWithObjectsAndKeys:@"Decryption error", @"msg", [NSNumber numberWithInt:1], @"code", nil]];
        jsString = [result toErrorCallbackString:theCallbackId];
    }
    [stringData release];
    [decryptedText release];

    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:jsString];
}

- (void) dealloc {
    [super dealloc];
}

- (unsigned char *)md5HexDigestEncryptArray:(NSString*)input {
    const char* str = [input UTF8String];
    unsigned char digest[CC_MD5_DIGEST_LENGTH];
    CC_MD5(str, strlen(str), digest);
    
    unsigned int var[4];
    for(int i=0; i<4; i++) {
        var[i] = 0;
        for(int j=0; j<4; j++) {
            var[i] += (digest[15 - (4*i+j)] << 8*(3-j));
        }
    }
    unsigned int seed = 0;
    for(int i=0; i<4; i++) {
        seed += var[i];
    }
    
    unsigned char *encryptArray = (unsigned char *)calloc(1024, sizeof(unsigned char));
    srand(seed);
    for(int i=0; i<1024; i++) {
        encryptArray[i] = (unsigned char)rand();
    }
    
    return encryptArray;
}

@end
