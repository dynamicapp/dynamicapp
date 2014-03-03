//
//  BluetoothLE.h
//  DynamicApp
//
//  Created by ZYYX Inc on 9/5/12.
//  Copyright (c) 2012 ZYYX Inc. All rights reserved.
//

#import "DynamicAppPlugin.h"

#import <CoreBluetooth/CoreBluetooth.h>

typedef enum {
	BLEPeripheralStateAvailable,
	BLEPeripheralStateUnavailable,
	BLEPeripheralStateConnected,
	BLEPeripheralStateDisconnected,
	BLEPeripheralStateConnecting,
}  BLEPeripheralConnectionState;

typedef enum {
    BLEPeripheralProcTypeWrite,
    BLEPeripheralProcTypeRead,
} BLEPeripheralProcType;

typedef enum {
    BLEValueTypeUUID,
    BLEValueTypeBoolean,
    BLEValueType2Bit,
    BLEValueTypeNibble,
    BLEValueType8Bit,
    BLEValueType16Bit,
    BLEValueType24Bit,
    BLEValueType32Bit,
    BLEValueTypeUint8,
    BLEValueTypeUint12,
    BLEValueTypeUint16,
    BLEValueTypeUint24,
    BLEValueTypeUint32,
    BLEValueTypeUint40,
    BLEValueTypeUint48,
    BLEValueTypeUint64,
    BLEValueTypeUint128,
    BLEValueTypeSint8,
    BLEValueTypeSint12,
    BLEValueTypeSint16,
    BLEValueTypeSint24,
    BLEValueTypeSint32,
    BLEValueTypeSint48,
    BLEValueTypeSint64,
    BLEValueTypeSint128,
    BLEValueTypeFloat32,
    BLEValueTypeFloat64,
    BLEValueTypeSfloat,
    BLEValueTypeFloat,
    BLEValueTypeDunit16,
    BLEValueTypeUTF8S,
    BLEValueTypeUTF16S,
} BLEValueType;

@interface Parameter : NSObject {
    NSString *peripheralId;
    NSString *serviceId;
    NSString *characteristicId;
    NSString *descriptorId;
    BLEPeripheralProcType eProcType;
    BLEValueType eValueType;
    NSString* valueString;
    CBCharacteristicWriteType eWriteType;
}
@property (nonatomic, retain) NSString *peripheralId;
@property (nonatomic, retain) NSString *serviceId;
@property (nonatomic, retain) NSString *characteristicId;
@property (nonatomic, retain) NSString *descriptorId;
@property (nonatomic) BLEPeripheralProcType eProcType;
@property (nonatomic, retain) NSString* valueString;
@property (nonatomic) BLEValueType eValueType;
@property (nonatomic) CBCharacteristicWriteType eWriteType;

@end

@interface Bluetooth4LE : DynamicAppPlugin<CBCentralManagerDelegate, CBPeripheralDelegate> {
}


- (void) scan:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) connect:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) disconnect:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) readValueForCharacteristic:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) writeValueForCharacteristic:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) readValueForDescriptor:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;
- (void) writeValueForDescriptor:(NSMutableDictionary *)arguments withOptions:(NSMutableDictionary *)options;

@end