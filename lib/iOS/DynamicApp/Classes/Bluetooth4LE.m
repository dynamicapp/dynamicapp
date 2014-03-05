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

#import "Bluetooth4LE.h"
#import "PluginResult.h"

@implementation Parameter
@synthesize peripheralId, serviceId, characteristicId, descriptorId, eProcType;
@synthesize valueString, eValueType, eWriteType;

- (id) init {
    self = [super init];
    if(self) {
        self.peripheralId = nil;
        self.serviceId = nil;
        self.characteristicId = nil;
        self.descriptorId = nil;
        self.valueString = nil;
    }
    
    return self;
}

- (void)dealloc {
    self.peripheralId = nil;
    self.serviceId = nil;
    self.characteristicId = nil;
    self.descriptorId = nil;
    self.valueString = nil;

    [super dealloc];
}
@end

@interface Bluetooth4LE() {
    CBCentralManager *manager;
    NSMutableDictionary *peripheralList;
    NSMutableArray *scanedServices;
    Parameter *param;
    
    NSTimer *checktimer;
    BOOL isProcessing;
}

@property(nonatomic, retain) CBCentralManager *manager;
@property (nonatomic, retain) NSMutableDictionary *peripheralList;
@property (nonatomic, retain) NSMutableArray *scanedServices;
@property (nonatomic, retain) Parameter *param;

@property (nonatomic, retain) NSTimer *checktimer;
@property (nonatomic) BOOL isProcessing;

- (NSData *)changeValue2WritingData:(NSString *)value valueType:(BLEValueType)eValueType;
- (void)startTimer;
- (void)stopTimer;
- (void)checkFunc;
- (NSString *)createReturnStringForRead:(NSMutableData *)data valueType:(BLEValueType) eValueType;
@end

@implementation Bluetooth4LE

@synthesize manager, peripheralList, scanedServices;
@synthesize param;
@synthesize checktimer, isProcessing;

- (void) scan:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    if(nil == self.manager) {
        self.manager = [[[CBCentralManager alloc] initWithDelegate:self queue:nil] autorelease];
        self.peripheralList = [[[NSMutableDictionary alloc] init] autorelease];
        self.scanedServices = [[[NSMutableArray alloc] init] autorelease];
        self.isProcessing = NO;
    }
    
    [self.scanedServices removeAllObjects];

    NSArray *serviceList = [arguments objectForKey:@"services"];
    
    for (NSString *aServiceId in serviceList) {
        [self.scanedServices addObject:[CBUUID UUIDWithString:aServiceId]];
    }

    NSDictionary *scanoptions = [NSDictionary dictionaryWithObject:[NSNumber numberWithBool:NO] 
                                                        forKey:CBCentralManagerScanOptionAllowDuplicatesKey];
    
    [self.manager scanForPeripheralsWithServices:self.scanedServices options:scanoptions];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
}

- (void) connect:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
 
    NSDictionary *peripheralData = [arguments objectForKey:@"peripheralData"];
    NSString *peripheralId = [peripheralData objectForKey:@"id"];
    
    CBPeripheral *peripheral = [self.peripheralList objectForKey:peripheralId];

#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    if(peripheral && !peripheral.state == CBPeripheralStateConnected) {
#else
    if(peripheral && !peripheral.isConnected) {
#endif
        [self.manager connectPeripheral:peripheral
                                options:[NSDictionary dictionaryWithObject:[NSNumber numberWithBool:YES] forKey:CBConnectPeripheralOptionNotifyOnDisconnectionKey]];
    } else {
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
    }
}

- (void) disconnect:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
    
    NSDictionary *peripheralData = [arguments objectForKey:@"peripheralData"];
    NSString *peripheralId = [peripheralData objectForKey:@"id"];
    
    CBPeripheral *peripheral = [self.peripheralList objectForKey:peripheralId];
    
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    if(peripheral && peripheral.state == CBPeripheralStateConnected) {
#else
    if(peripheral && peripheral.isConnected) {
#endif
        [self.manager cancelPeripheralConnection:peripheral];
    } else {
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
        [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
    }
}

- (void) readValueForCharacteristic:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
    PluginResult *result = nil;
    
    NSDictionary *peripheralData = [arguments objectForKey:@"peripheralData"];
    NSString *peripheralId = [peripheralData objectForKey:@"id"];
    NSString *serviceId = [arguments objectForKey:@"service"];

    self.param = nil;
    
    CBPeripheral *peripheral = [self.peripheralList objectForKey:peripheralId];
 
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    if(peripheral && peripheral.state == CBPeripheralStateConnected) {
#else
    if(peripheral && peripheral.isConnected) {
#endif
        CBService *targetService = nil;

        for(CBService *aService in peripheral.services) {
            if([aService.UUID isEqual:[CBUUID UUIDWithString:serviceId]]) {
                targetService = aService;
                break;
            }
        }
        
        if(targetService) {
            self.param = [[[Parameter alloc] init] autorelease];
            self.param.peripheralId = peripheralId;
            self.param.serviceId = serviceId;
            self.param.characteristicId = [arguments objectForKey:@"characteristic"];
            self.param.eProcType = BLEPeripheralProcTypeRead;
            self.param.eValueType = (BLEValueType)[[arguments objectForKey:@"valueType"] intValue];
            
            [peripheral discoverCharacteristics:[NSArray arrayWithObjects:[CBUUID UUIDWithString:self.param.characteristicId], nil] 
                                     forService:targetService];
        } else {
            [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
            if(self.callbackId) {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
                [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
            }
        }
    } else {
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        if(self.callbackId) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        }
    }
}

- (void) writeValueForCharacteristic:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
    
    PluginResult *result = nil;
    
    NSDictionary *peripheralData = [arguments objectForKey:@"peripheralData"];
    NSString *peripheralId = [peripheralData objectForKey:@"id"];
    NSString *serviceId = [arguments objectForKey:@"service"];
    
    self.param = nil;
    CBPeripheral *peripheral = [self.peripheralList objectForKey:peripheralId];
    
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    if(peripheral && peripheral.state == CBPeripheralStateConnected) {
#else
    if(peripheral && peripheral.isConnected) {
#endif
        CBService *targetService = nil;
        
        for(CBService *aService in peripheral.services) {
            if([aService.UUID isEqual:[CBUUID UUIDWithString:serviceId]]) {
                targetService = aService;
                break;
            }
        }
        
        if(targetService) {
            self.param = [[[Parameter alloc] init] autorelease];
            self.param.peripheralId = peripheralId;
            self.param.serviceId = serviceId;
            self.param.characteristicId = [arguments objectForKey:@"characteristic"];
            self.param.eProcType = BLEPeripheralProcTypeWrite;
            self.param.eWriteType = (CBCharacteristicWriteType)[[arguments objectForKey:@"writeType"] intValue];
            self.param.valueString = [arguments objectForKey:@"value"];
            self.param.eValueType = (BLEValueType)[[arguments objectForKey:@"valueType"] intValue];
            
            [peripheral discoverCharacteristics:[NSArray arrayWithObjects:[CBUUID UUIDWithString:self.param.characteristicId], nil] 
                                     forService:targetService];
        } else {
            [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
            if(self.callbackId) {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
                [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
            }
        }
    } else {
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        if(self.callbackId) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        }
    }
}

- (void) readValueForDescriptor:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
    PluginResult *result = nil;
    
    NSDictionary *peripheralData = [arguments objectForKey:@"peripheralData"];
    NSString *peripheralId = [peripheralData objectForKey:@"id"];
    NSString *serviceId = [arguments objectForKey:@"service"];
    
    self.param = nil;
    
    CBPeripheral *peripheral = [self.peripheralList objectForKey:peripheralId];
    
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    if(peripheral && peripheral.state == CBPeripheralStateConnected) {
#else
    if(peripheral && peripheral.isConnected) {
#endif
        CBService *targetService = nil;
        
        for(CBService *aService in peripheral.services) {
            if([aService.UUID isEqual:[CBUUID UUIDWithString:serviceId]]) {
                targetService = aService;
                break;
            }
        }
        
        if(targetService) {
            self.param = [[[Parameter alloc] init] autorelease];
            self.param.serviceId = serviceId;
            self.param.characteristicId = [arguments objectForKey:@"characteristic"];
            self.param.descriptorId = [arguments objectForKey:@"descriptor"];
            self.param.eProcType = BLEPeripheralProcTypeRead;
            self.param.eValueType = (BLEValueType)[[arguments objectForKey:@"valueType"] intValue];
            
            [peripheral discoverCharacteristics:[NSArray arrayWithObjects:[CBUUID UUIDWithString:self.param.characteristicId], nil] 
                                     forService:targetService];
        } else {
            [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
            if(self.callbackId) {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
                [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
            }
        }
    } else {
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        if(self.callbackId) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        }
    }
}

- (void) writeValueForDescriptor:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options {
    self.callbackId = [options objectForKey:@"callbackId"];
    
    PluginResult *result = nil;
    
    NSDictionary *peripheralData = [arguments objectForKey:@"peripheralData"];
    NSString *peripheralId = [peripheralData objectForKey:@"id"];
    NSString *serviceId = [arguments objectForKey:@"service"]; 
    
    self.param = nil;
    
    CBPeripheral *peripheral = [self.peripheralList objectForKey:peripheralId];
    
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    if(peripheral && peripheral.state == CBPeripheralStateConnected) {
#else
    if(peripheral && peripheral.isConnected) {
#endif
        CBService *targetService = nil;
        
        for(CBService *aService in peripheral.services) {
            if([aService.UUID isEqual:[CBUUID UUIDWithString:serviceId]]) {
                targetService = aService;
                break;
            }
        }
        
        if(targetService) {
            self.param = [[[Parameter alloc] init] autorelease];
            self.param.serviceId = serviceId;
            self.param.characteristicId = [arguments objectForKey:@"characteristic"];
            self.param.descriptorId = [arguments objectForKey:@"descriptor"];
            self.param.eProcType = BLEPeripheralProcTypeWrite;
            self.param.eWriteType = (CBCharacteristicWriteType)[[arguments objectForKey:@"writeType"] intValue];
            self.param.valueString = [arguments objectForKey:@"value"];
            self.param.eValueType = (BLEValueType)[[arguments objectForKey:@"valueType"] intValue];

            [peripheral discoverCharacteristics:[NSArray arrayWithObjects:[CBUUID UUIDWithString:self.param.characteristicId], nil] 
                                     forService:targetService];
        } else {
            [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
            if(self.callbackId) {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
                [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
            }
        }
    } else {
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        if(self.callbackId) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        }
    }
}

#pragma mark - CBCentralManager delegate methods
/*
 Invoked whenever the central manager's state is updated.
 */
- (void) centralManagerDidUpdateState:(CBCentralManager *)central {
    switch (central.state) {
        case CBCentralManagerStatePoweredOn:
            //NSLog(@"centralManagerDidUpdateState poweredOn");
            break;
            
        case CBCentralManagerStatePoweredOff:
            //NSLog(@"centralManagerDidUpdateState poweredOff");
            break;
            
        case CBCentralManagerStateResetting:
            //NSLog(@"centralManagerDidUpdateState resetting");
            break;
            
        case CBCentralManagerStateUnauthorized:
            //NSLog(@"centralManagerDidUpdateState unauthorized");
            break;
            
        case CBCentralManagerStateUnsupported:
            //NSLog(@"centralManagerDidUpdateState unsupported");
            break;
            
        case CBCentralManagerStateUnknown:
            //NSLog(@"centralManagerDidUpdateState unknown");
            break;
            
        default:
            break;
    }

}

/*
 Invoked when the central discovers a peripheral while scanning.
 */
- (void) centralManager:(CBCentralManager *)central didDiscoverPeripheral:(CBPeripheral *)aPeripheral advertisementData:(NSDictionary *)advertisementData RSSI:(NSNumber *)RSSI { 
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
    [self.peripheralList setObject:aPeripheral forKey:[aPeripheral.identifier UUIDString]];
     NSString *jsString = [NSString stringWithFormat:@"Bluetooth4LE.onStateChanged('%@', '%@', %d);", aPeripheral.name, [aPeripheral.identifier UUIDString], BLEPeripheralStateAvailable];
#else
    CFStringRef UUID = CFUUIDCreateString(NULL, aPeripheral.UUID);
    [self.peripheralList setObject:aPeripheral forKey:(NSString *)UUID];
    NSString *jsString = [NSString stringWithFormat:@"Bluetooth4LE.onStateChanged('%@', '%@', %d);", aPeripheral.name, UUID, BLEPeripheralStateAvailable];
#endif
    [self.webView stringByEvaluatingJavaScriptFromString: jsString];
#if __IPHONE_OS_VERSION_MIN_REQUIRED < 70000
   CFRelease(UUID);
#endif
}

/*
 Invoked whenever a connection is succesfully created with the peripheral. 
 Discover available services on the peripheral
 */
- (void) centralManager:(CBCentralManager *)central didConnectPeripheral:(CBPeripheral *)aPeripheral {
    aPeripheral.delegate = self;
    [aPeripheral discoverServices:self.scanedServices];
}

/*
 Invoked whenever the central manager fails to create a connection with the peripheral.
 */
- (void)centralManager:(CBCentralManager *)central didFailToConnectPeripheral:(CBPeripheral *)aPeripheral error:(NSError *)error {
    PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
}

/*
 Invoked whenever an existing connection with the peripheral is torn down. 
 Reset local variables
 */
- (void)centralManager:(CBCentralManager *)central didDisconnectPeripheral:(CBPeripheral *)aPeripheral error:(NSError *)error {
    if(error == nil) {
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
        NSString *jsString = [NSString stringWithFormat:@"Bluetooth4LE.onStateChanged('%@', '%@', %d);", aPeripheral.name, [aPeripheral.identifier UUIDString], BLEPeripheralStateAvailable];
#else
        CFStringRef UUID = CFUUIDCreateString(NULL, aPeripheral.UUID);
        NSString *jsString = [NSString stringWithFormat:@"Bluetooth4LE.onStateChanged('%@', '%@', %d);", aPeripheral.name, UUID, BLEPeripheralStateAvailable];
#endif
        [self.webView stringByEvaluatingJavaScriptFromString: jsString];
        
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
        if(self.callbackId) {
            PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
        }
#if __IPHONE_OS_VERSION_MIN_REQUIRED < 70000
        CFRelease(UUID);
#endif
    } else {
        if(self.callbackId) {
            PluginResult *result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        }
    }
}

#pragma mark - CBPeripheral delegate methods

/*
 Invoked upon completion of a -[discoverServices:] request.
 Discover available characteristics on interested services
 */
- (void)peripheral:(CBPeripheral *)aPeripheral didDiscoverServices:(NSError *)error {
    BOOL bOK = YES;
    if(error) {
        bOK = NO;
    }
    
    if(bOK) {
        if(aPeripheral.services.count == 0) {
            // call error callback
            bOK = NO;
        }
    }
    
    if(bOK) {
        bOK = NO;
        for(CBService *aService in aPeripheral.services) {
            for(CBUUID* uuid in self.scanedServices) {
                if ([aService.UUID isEqual:uuid]) {
                    bOK = YES;
                    break;
                }
            }
        }
    }

    PluginResult *result = nil;
    NSString *jsString = nil;
    if(bOK) {
#if __IPHONE_OS_VERSION_MIN_REQUIRED >= 70000
        jsString = [NSString stringWithFormat:@"Bluetooth4LE.onStateChanged('%@', '%@', %d);",
                    aPeripheral.name,
                    [aPeripheral.identifier UUIDString],
                    BLEPeripheralStateConnected];
#else
        CFStringRef UUID = CFUUIDCreateString(NULL, aPeripheral.UUID);
        jsString = [NSString stringWithFormat:@"Bluetooth4LE.onStateChanged('%@', '%@', %d);", 
                              aPeripheral.name, 
                              UUID, 
                              BLEPeripheralStateConnected];
#endif
        [self.webView stringByEvaluatingJavaScriptFromString: jsString];
    
        if(self.callbackId) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
            jsString = [result toSuccessCallbackString:self.callbackId];
        }
        
#if __IPHONE_OS_VERSION_MIN_REQUIRED < 70000
        CFRelease(UUID);
#endif
    } else {
        [self.manager cancelPeripheralConnection:aPeripheral];
        if(self.callbackId) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            jsString = [result toErrorCallbackString:self.callbackId];
        }
    }

    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    if(result) {
        [self.webView stringByEvaluatingJavaScriptFromString:jsString];
    } 
}

/*
 Invoked upon completion of a -[discoverCharacteristics:forService:] request.
 Perform appropriate operations on interested characteristics
 */
- (void)peripheral:(CBPeripheral *)aPeripheral didDiscoverCharacteristicsForService:(CBService *)service error:(NSError *)error {
    BOOL bOK = YES;
    if(error) {
        bOK = NO;
    }
    
    if(bOK && service.characteristics.count == 0) {
        bOK = NO;
    }
    
    PluginResult *result = nil;
    NSString *jsString = nil;
    if(bOK) {
        CBCharacteristic *aCharacteristic = nil;
        
        for(CBCharacteristic *aChar in service.characteristics) {
            if([aChar.UUID isEqual:[CBUUID UUIDWithString:self.param.characteristicId]]) {
                aCharacteristic = aChar;
                break;
            }
        }

        if(aCharacteristic) {
            if(self.param.descriptorId) {
                [aPeripheral discoverDescriptorsForCharacteristic:aCharacteristic];
            } else {
                switch(self.param.eProcType) {
                    case BLEPeripheralProcTypeWrite:
                        {
                            NSData *data = [self changeValue2WritingData:self.param.valueString valueType:self.param.eValueType];
                            [aPeripheral writeValue:data forCharacteristic:aCharacteristic type:self.param.eWriteType];
                        }
                        break;
                    case BLEPeripheralProcTypeRead:
                        [aPeripheral readValueForCharacteristic:aCharacteristic];
                        break;
                }
                [self startTimer];
            }
        } else {
            if(self.callbackId) {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
                jsString = [result toErrorCallbackString:self.callbackId];
            }
        }
    } else {
        if(self.callbackId) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            jsString = [result toErrorCallbackString:self.callbackId];
        }
    }
    
    if(result) {
        [self.webView stringByEvaluatingJavaScriptFromString:jsString];
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    }
}

/*
 Invoked upon completion of a -[discoverDescriptorsForCharacteristic:] request.
 */
- (void)peripheral:(CBPeripheral *)peripheral didDiscoverDescriptorsForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    BOOL bOK = YES;
    if(error) {
        bOK = NO;
    }
    
    if(bOK && characteristic.descriptors.count == 0) {
        bOK = NO;
    }
    
    PluginResult *result = nil;
    NSString *jsString = nil;
    if(bOK) {
        CBDescriptor *aDescriptor = nil;
        
        for(CBDescriptor *aDesc in characteristic.descriptors) {
            if([aDesc.UUID isEqual:[CBUUID UUIDWithString:self.param.descriptorId]]) {
                aDescriptor = aDesc;
                break;
            }
        }
        
        if(aDescriptor) {
            switch(self.param.eProcType) {
                case BLEPeripheralProcTypeWrite:
                    {
                        NSData *data = [self changeValue2WritingData:self.param.valueString valueType:self.param.eValueType];
                        [peripheral writeValue:data forDescriptor:aDescriptor];
                    }
                    break;
                case BLEPeripheralProcTypeRead:
                    [peripheral readValueForDescriptor:aDescriptor];
                    break;
            }

            [self startTimer];
        } else {
            if(self.callbackId) {
                result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
                jsString = [result toErrorCallbackString:self.callbackId];
            }
        }
    } else {
        if(self.callbackId){
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            jsString = [result toErrorCallbackString:self.callbackId];
        }
    }
    
    if(result) {
        [self.webView stringByEvaluatingJavaScriptFromString:jsString];
        [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    }
}

/*
 Invoked upon completion of a -[readValueForCharacteristic:] request or on the reception of a notification/indication.
 */
- (void)peripheral:(CBPeripheral *)aPeripheral didUpdateValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    PluginResult *result;

    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    if(self.callbackId) {
        if(error == nil) {
            NSMutableData *data = [NSMutableData dataWithData:characteristic.value];

            NSString *strValue = [self createReturnStringForRead:data valueType:self.param.eValueType]; 

            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsString:strValue];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
        } else {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        }
    }
    
    [self stopTimer];
}

/*
 Invoked upon completion of a -[writeValue:forCharacteristic:] request.
 */
- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForCharacteristic:(CBCharacteristic *)characteristic error:(NSError *)error {
    PluginResult *result;
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    if(self.callbackId) {
        if(error == nil) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
        } else {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        }
    }
    
    [self stopTimer];
}

/*
 Invoked upon completion of a -[readValueForDescriptor:] request.
 */
- (void)peripheral:(CBPeripheral *)peripheral didUpdateValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error {
    PluginResult *result;
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    if(error == nil) {
        NSMutableData *data = [NSMutableData dataWithData:descriptor.value];
        
        NSString *strValue = [self createReturnStringForRead:data valueType:self.param.eValueType]; 
        
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK messageAsString:strValue];
        [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
    } else {
        if(self.callbackId) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        }
    }

    [self stopTimer];
}

/*
 Invoked upon completion of a -[writeValue:forDescriptor:] request.
 */
- (void)peripheral:(CBPeripheral *)peripheral didWriteValueForDescriptor:(CBDescriptor *)descriptor error:(NSError *)error {
    PluginResult *result;
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];
    if(error == nil) {
        result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
        [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
    } else {
        if(self.callbackId) {
            result = [PluginResult resultWithStatus:DynamicAppCommandStatus_ERROR];
            [self.webView stringByEvaluatingJavaScriptFromString:[result toErrorCallbackString:self.callbackId]];
        }
    }

    [self stopTimer];
}

/*
 Reference to 
 */
- (NSData *)changeValue2WritingData:(NSString *)value valueType:(BLEValueType)eValueType {
    NSMutableData *data = nil;
    
    switch (eValueType) {
        case BLEValueTypeUUID:      // unsigned 16-bit integer or unsigned 128-bit integer
            break;
        case BLEValueTypeBoolean:   // unsigned 1-bit; 0 = false, 1 = true
            {
                BOOL bValue = [value boolValue];
                data = [NSMutableData dataWithBytes:&bValue length:sizeof(BOOL)];
            }
            break;
        case BLEValueType2Bit:      // 2-bit value
        case BLEValueTypeNibble:    // 4-bit value
        case BLEValueType8Bit:      // 8-bit value
            {
                Byte bytValue = (Byte)[value intValue];
                data = [NSMutableData dataWithBytes:&bytValue length:sizeof(Byte)];
            }
            break;
        case BLEValueType16Bit:     // 16-bit value
        case BLEValueType24Bit:     // 24-bit value
        case BLEValueType32Bit:     // 32-bit value
            {
                UInt32 uiValue = [value intValue];
                data = [NSMutableData dataWithBytes:&uiValue length:sizeof(UInt32)];
            }
            break;
        case BLEValueTypeUint8:     // unsigned 8-bit integer
            {
                UInt8 uiValue = (UInt8)[value intValue];
                data = [NSMutableData dataWithBytes:&uiValue length:sizeof(UInt8)];
            }
            break;
        case BLEValueTypeUint12:    // unsigned 12-bit integer
        case BLEValueTypeUint16:    // unsigned 16-bit integer
            {
                UInt16 uiValue = (UInt16)[value intValue];
                data = [NSMutableData dataWithBytes:&uiValue length:sizeof(UInt16)];
            }
            break;
        case BLEValueTypeUint24:    // unsigned 24-bit integer
        case BLEValueTypeUint32:    // unsigned 32-bit integer
            {
                UInt32 uiValue = (UInt32)[value intValue];
                data = [NSMutableData dataWithBytes:&uiValue length:sizeof(UInt32)];
            }
            break;
        case BLEValueTypeUint40:    // unsigned 40-bit integer
        case BLEValueTypeUint48:    // unsigned 48-bit integer
        case BLEValueTypeUint64:    // unsigned 64-bit integer
            {
                UInt64 uiValue = (UInt64)[value longLongValue];
                data = [NSMutableData dataWithBytes:&uiValue length:sizeof(UInt64)];
            }
            break;
        case BLEValueTypeUint128:   // unsigned 128-bit integer
            break;
        case BLEValueTypeSint8:     // signed 8-bit integer
            {
                SInt8 siValue = [value intValue];
                data = [NSMutableData dataWithBytes:&siValue length:sizeof(SInt8)];
            }
            break;
        case BLEValueTypeSint12:    // signed 12-bit integer
        case BLEValueTypeSint16:    // signed 16-bit integer
            {
                SInt16 siValue = [value intValue];
                data = [NSMutableData dataWithBytes:&siValue length:sizeof(SInt16)];
            }
            break;
        case BLEValueTypeSint24:    // signed 24-bit integer
        case BLEValueTypeSint32:    // signed 32-bit integer
            {
                SInt32 siValue = [value intValue];
                data = [NSMutableData dataWithBytes:&siValue length:sizeof(SInt32)];
            }
            break;
        case BLEValueTypeSint48:    // signed 48-bit integer
        case BLEValueTypeSint64:    // signed 64-bit integer
            {
                SInt64 siValue = [value longLongValue];
                data = [NSMutableData dataWithBytes:&siValue length:sizeof(SInt64)];
            }
            break;
        case BLEValueTypeSint128:   // signed 128-bit integer
            break;
        case BLEValueTypeFloat32:   // IEEE-754 32-bit floating point
            {
                Float32 f32Value = [value floatValue];
                data = [NSMutableData dataWithBytes:&f32Value length:sizeof(Float32)];
            }
            break;
        case BLEValueTypeFloat64:   // IEEE-754 64-bit floating point
            {
                Float64 f64Value = [value doubleValue];
                data = [NSMutableData dataWithBytes:&f64Value length:sizeof(Float64)];
            }
            break;
        case BLEValueTypeSfloat:    // IEEE-11073 16-bit SFLOAT
            break;
        case BLEValueTypeFloat:     // IEEE-11073 32-bit FLOAT
            break;
        case BLEValueTypeDunit16:   // double unsigned 16-bit integer
            break;
        case BLEValueTypeUTF8S:     // UTF-8 string
            {
                data = [NSMutableData dataWithData:[value dataUsingEncoding:NSUTF8StringEncoding]];
            }
            break;
        case BLEValueTypeUTF16S:    // UTF-16 string
            {
                data = [NSMutableData dataWithData:[value dataUsingEncoding:NSUTF16StringEncoding]];
            }
            break;
        default:
            break;
    }
    return data;
}

- (void)startTimer {
    self.checktimer = [NSTimer scheduledTimerWithTimeInterval:1.0
                                                  target:self
                                                    selector:@selector(checkFunc)
                                                    userInfo:nil
                                                    repeats:YES];
    self.isProcessing = YES;
    
}

- (void)stopTimer {
    [self.checktimer invalidate];
    self.checktimer = nil;
    
    self.isProcessing = NO;
    self.param = nil;
}

- (void)checkFunc {
    if(!self.isProcessing) return;
    
    PluginResult *result;
    
    [self.webView stringByEvaluatingJavaScriptFromString:@"DynamicApp.processing = false;"];

    result = [PluginResult resultWithStatus:DynamicAppCommandStatus_OK];
    [self.webView stringByEvaluatingJavaScriptFromString:[result toSuccessCallbackString:self.callbackId]];
    
    [self stopTimer];
}

- (NSString *)createReturnStringForRead:(NSMutableData *)data valueType:(BLEValueType) eValueType {
    NSString *strValue = nil;

/*    UInt8            8-bit unsigned integer 
    SInt8            8-bit signed integer
    UInt16          16-bit unsigned integer 
    SInt16          16-bit signed integer           
    UInt32          32-bit unsigned integer 
    SInt32          32-bit signed integer   
    UInt64          64-bit unsigned integer 
    SInt64          64-bit signed integer   */
    
    switch (eValueType) {
        case BLEValueTypeUUID:  // unsigned 16-bit integer or unsigned 128-bit integer
            {
            
            }
            break;
        case BLEValueTypeBoolean:   // unsigned 1-bit; 0 = false, 1 = true
            {
                BOOL bValue = false;

                [data getBytes:&bValue length:sizeof(bValue)];
                strValue = [[NSNumber numberWithBool:bValue] stringValue]; 
            }
            break;
        case BLEValueType2Bit:      // 2-bit value
        case BLEValueTypeNibble:    // 4-bit value
        case BLEValueType8Bit:      // 8-bit value
            {
                Byte bytValue = 0;

                [data getBytes:&bytValue length:sizeof(bytValue)];
                strValue = [[NSNumber numberWithUnsignedChar:bytValue] stringValue]; 
            
            }            
            break;
        case BLEValueType16Bit:     // 16-bit value
            {
                short sValue = 0;
                
                [data getBytes:&sValue length:sizeof(sValue)];
                strValue = [[NSNumber numberWithUnsignedShort:sValue] stringValue]; 
            
            }            
            break;
        case BLEValueType24Bit:     // 24-bit value
        case BLEValueType32Bit:     // 32-bit value
            {
                UInt32 uiValue = 0;
                [data getBytes:&uiValue length:sizeof(uiValue)];
                strValue = [[NSNumber numberWithUnsignedLong:uiValue] stringValue]; 
            }
            break;
        case BLEValueTypeUint8:     // unsigned 8-bit integer
            {
                UInt8 uiVakue = 0;

                [data getBytes:&uiVakue length:sizeof(uiVakue)];
                strValue = [[NSNumber numberWithUnsignedChar:uiVakue] stringValue]; 
            
            }
            break;
        case BLEValueTypeUint12:    // unsigned 12-bit integer
        case BLEValueTypeUint16:    // unsigned 16-bit integer
            {
                UInt16 uiValue = 0;
                
                [data getBytes:&uiValue length:sizeof(uiValue)];
                strValue = [[NSNumber numberWithUnsignedShort:uiValue] stringValue]; 
            
            }
            break;
        case BLEValueTypeUint24:    // unsigned 24-bit integer
        case BLEValueTypeUint32:    // unsigned 32-bit integer
            {
                UInt32 uiValue = 0;

                [data getBytes:&uiValue length:sizeof(uiValue)];
                strValue = [[NSNumber numberWithUnsignedLong:uiValue] stringValue]; 
            }
            break;
        case BLEValueTypeUint40:    // unsigned 40-bit integer
        case BLEValueTypeUint48:    // unsigned 48-bit integer
        case BLEValueTypeUint64:
            {
                UInt64 uiValue = 0;
                
                [data getBytes:&uiValue length:sizeof(uiValue)];
                strValue = [[NSNumber numberWithUnsignedLongLong:uiValue] stringValue]; 
            }
           break;
        case BLEValueTypeUint128:   // unsigned 128-bit integer
            break;
        case BLEValueTypeSint8:     // signed 8-bit integer
            {
                SInt8 siValue = 0;
                
                [data getBytes:&siValue length:sizeof(siValue)];
                strValue = [[NSNumber numberWithShort:siValue] stringValue]; 
            }
            break;
        case BLEValueTypeSint12:    // signed 12-bit integer
        case BLEValueTypeSint16:    // signed 16-bit integer
            {
                SInt16 siValue = 0;

                [data getBytes:&siValue length:sizeof(siValue)];
                strValue = [[NSNumber numberWithShort:siValue] stringValue]; 
            }
            break;
        case BLEValueTypeSint24:    // signed 24-bit integer
        case BLEValueTypeSint32:    // signed 32-bit integer
            {
                SInt32 siValue = 0;

                [data getBytes:&siValue length:sizeof(siValue)];
                strValue = [[NSNumber numberWithLong:siValue] stringValue]; 
            }
            break;
        case BLEValueTypeSint48:    // signed 48-bit integer
        case BLEValueTypeSint64:
            {
                SInt64 siValue = 0;

                [data getBytes:&siValue length:sizeof(siValue)];
                strValue = [[NSNumber numberWithLongLong:siValue] stringValue]; 
            }
            break;
        case BLEValueTypeSint128:   // signed 128-bit integer
            break;
        case BLEValueTypeFloat32:   // IEEE-754 32-bit floating point
            {
                Float32 f32Value = 0;
                [data getBytes:&f32Value length:sizeof(f32Value)];
                strValue = [[NSNumber numberWithFloat:f32Value] stringValue];
            }
            break;
        case BLEValueTypeFloat64:   // IEEE-754 64-bit floating point
            {
                Float64 f64Value = 0;
                [data getBytes:&f64Value length:sizeof(f64Value)];
                strValue = [[NSNumber numberWithDouble:f64Value] stringValue];
            }
            break;
        case BLEValueTypeSfloat:    // IEEE-11073 16-bit SFLOAT
            break;
        case BLEValueTypeFloat:     // IEEE-11073 32-bit FLOAT
            break;
        case BLEValueTypeDunit16:   // double unsigned 16-bit integer
            break;
        case BLEValueTypeUTF8S:     // UTF-8 string
            {
                strValue = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
                [strValue autorelease];
            }
            break;
        case BLEValueTypeUTF16S:    // UTF-16 string
            {
                strValue = [[NSString alloc] initWithData:data encoding:NSUTF16StringEncoding];
                [strValue autorelease];
            }
            break;
        default:
            break;
    }
    
    return strValue;
}

- (void)dealloc {
    self.manager = nil;
    self.peripheralList = nil;
    self.scanedServices = nil;
    self.param = nil;
    
    if(self.checktimer) {
        [self.checktimer invalidate];
        self.checktimer = nil;
    }

    [super dealloc];
}

@end
