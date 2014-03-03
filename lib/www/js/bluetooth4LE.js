var Bluetooth4LE = {
    peripheralList : [],
    onStateChanged : function(deviceName, id, state) {
        Bluetooth4LE.peripheralList = Bluetooth4LE.peripheralList.filter(function(obj) {return obj.id != id;});
        if(state == Bluetooth4LE.STATE_AVAILABLE || state == Bluetooth4LE.STATE_CONNECTED || state == Bluetooth4LE.STATE_CONNECTING) {
            Bluetooth4LE.peripheralList.push({
                deviceName: deviceName,
                id: id,
                state: state
            });
        }
    },
    scanDevices : function(services) {
        //Bluetooth4LE.peerList.length = 0;
        DynamicApp.exec(null, null, 'Bluetooth4LE', 'scan', [{services: services}]);
    },
    connect : function(peripheral, successCallback, errorCallback) {
        if(peripheral.state != Bluetooth4LE.STATE_AVAILABLE) {
            if(errorCallback) {
                errorCallback();
            }
            return;
        }
        var _successCallback, _errorCallback;
        if(successCallback) {
            _successCallback = function() {
                setTimeout(function(){successCallback();}, 100);
            }
        }
        if(errorCallback) {
            _errorCallback = function() {
                setTimeout(function(){errorCallback();}, 100);
            }
        }
        
        Bluetooth4LE.onStateChanged(peripheral.deviceName, peripheral.id, Bluetooth4LE.STATE_CONNECTING);
        DynamicApp.exec(_successCallback, _errorCallback, 'Bluetooth4LE', 'connect', [{peripheralData: peripheral}]);
    },
    disconnect : function(peripheral, successCallback, errorCallback) {
        if(peripheral.state != Bluetooth4LE.STATE_CONNECTED) {
            if(errorCallback) {
                errorCallback();
            }
            return;
        }
        var _successCallback, _errorCallback;
        if(successCallback) {
            _successCallback = function() {
                setTimeout(function(){successCallback();}, 100);
            }
        }
        if(errorCallback) {
            _errorCallback = function() {
                setTimeout(function(){errorCallback();}, 100);
            }
        }
        
        DynamicApp.exec(_successCallback, _errorCallback, 'Bluetooth4LE', 'disconnect', [{peripheralData: peripheral}]);
    }
};

Bluetooth4LE.STATE_UNKNOWN = -1;
Bluetooth4LE.STATE_AVAILABLE = 0;
Bluetooth4LE.STATE_UNAVAILABLE = 1;
Bluetooth4LE.STATE_CONNECTED = 2;
Bluetooth4LE.STATE_DISCONNECTED = 3;
Bluetooth4LE.STATE_CONNECTING = 4;

var Peripheral = function(_deviceName, _id) {
    this.deviceName = _deviceName;
    this.id = _id;
};

Peripheral.prototype = {
    writeValueForCharacteristic : function(value, valueType, service, characteristic, writeType, successCallback, errorCallback) {
        var options = {
            peripheralData: {id: this.id, deviceName: this.deviceName},
            value: value,
            valueType: valueType,
            service: service,
            characteristic: characteristic,
            writeType: writeType
        };
        var _successCallback, _errorCallback;
        if(successCallback) {
            _successCallback = function() {
                setTimeout(function(){successCallback();}, 100);
            }
        }
        if(errorCallback) {
            _errorCallback = function() {
                setTimeout(function(){errorCallback();}, 100);
            }
        }
        DynamicApp.exec(_successCallback, _errorCallback, 'Bluetooth4LE', 'writeValueForCharacteristic', [options]);
    },
    readValueForCharacteristic : function(service, characteristic, valueType, successCallback, errorCallback) {
		var options = {
            peripheralData: {id: this.id, deviceName: this.deviceName},
            service: service,
            characteristic: characteristic,
			valueType: valueType
        };
        var _successCallback, _errorCallback;
        if(successCallback) {
            _successCallback = function(value) {
                setTimeout(function(){successCallback(value);}, 100);
            }
        }
        if(errorCallback) {
            _errorCallback = function() {
                setTimeout(function(){errorCallback();}, 100);
            }
        }
        DynamicApp.exec(_successCallback, _errorCallback, 'Bluetooth4LE', 'readValueForCharacteristic', [options]);
    },
    writeValueForDescriptor : function(value, valueType, service, characteristic, descriptor, successCallback, errorCallback) {
        var options = {
            peripheralData: {id: this.id, deviceName: this.deviceName},
            value: value,
            valueType: valueType,
            service: service,
            characteristic: characteristic,
            descriptor: descriptor            
        };
        var _successCallback, _errorCallback;
        if(successCallback) {
            _successCallback = function() {
                setTimeout(function(){successCallback();}, 100);
            }
        }
        if(errorCallback) {
            _errorCallback = function() {
                setTimeout(function(){errorCallback();}, 100);
            }
        }
        DynamicApp.exec(_successCallback, _errorCallback, 'Bluetooth4LE', 'writeValueForDescriptor', [options]);
    },
    readValueForDescriptor : function(service, characteristic, descriptor, valueType, successCallback, errorCallback) {
        var options = {
            peripheralData: {id: this.id, deviceName: this.deviceName},
            service: service,
            characteristic: characteristic,
            descriptor: descriptor,
			valueType: valueType
        };

        var _successCallback, _errorCallback;
        if(successCallback) {
            _successCallback = function(value) {
                setTimeout(function(){successCallback(value);}, 100);
            }
        }
        if(errorCallback) {
            _errorCallback = function() {
                setTimeout(function(){errorCallback();}, 100);
            }
        }
        DynamicApp.exec(_successCallback, _errorCallback, 'Bluetooth4LE', 'readValueForDescriptor', [options]);
    }
};

// write type
Bluetooth4LE.WRITETYPE_WRITEWITHRESPONSE = 0;
Bluetooth4LE.WRITETYPE_WRITEWITHOUTRESPONSE = 1;

// services
Bluetooth4LE.SERVICE_ALERT_NOTIFICATION = '1811';
Bluetooth4LE.SERVICE_BATTERY_SERVICE = '180F';
Bluetooth4LE.SERVICE_BLOOD_PRESSURE = '1810';
Bluetooth4LE.SERVICE_CURRENT_TIME = '1805';
Bluetooth4LE.SERVICE_CYCLINGSPEED_AND_CADENCE = '1816';
Bluetooth4LE.SERVICE_DEVICE_INFORMATION = '180A';
Bluetooth4LE.SERVICE_GENERIC_ACCESS = '1800';
Bluetooth4LE.SERVICE_GENERIC_ATTRIBUTE = '1801';
Bluetooth4LE.SERVICE_GLUCOSE = '1808';
Bluetooth4LE.SERVICE_HEALTH_THERMOMETER = '1809';
Bluetooth4LE.SERVICE_HEART_RATE = '180D';
Bluetooth4LE.SERVICE_HUMAN_INTERFACE_DEVICE = '1812';
Bluetooth4LE.SERVICE_IMMEDIATE_ALERT = '1802';
Bluetooth4LE.SERVICE_HUMAN_LINK_LOSS = '1803';
Bluetooth4LE.SERVICE_NEXT_DST_CHANGE = '1807';
Bluetooth4LE.SERVICE_PHONE_ALERT_STATUS = '180E';
Bluetooth4LE.SERVICE_REFERENCE_TIME_UPDATE = '1806';
Bluetooth4LE.SERVICE_SCAN_PARAMETERS = '1813';
Bluetooth4LE.SERVICE_TX_POWER = '1804';

// characteristics
Bluetooth4LE.CHARACTERISTICS_ALERT_CATEGORY_ID = '2A43';
Bluetooth4LE.CHARACTERISTICS_ALERT_CATEGORY_ID_BIT_MASK = '2A42';
Bluetooth4LE.CHARACTERISTICS_ALERT_LEVEL = '2A06';
Bluetooth4LE.CHARACTERISTICS_ALERT_NOTIFICATION_CONTROL_POINT = '2A44';
Bluetooth4LE.CHARACTERISTICS_ALERT_STATUS = '2A3F';
Bluetooth4LE.CHARACTERISTICS_APPEARANCE = '2A01';
Bluetooth4LE.CHARACTERISTICS_BATTERY_LEVEL = '2A19';
Bluetooth4LE.CHARACTERISTICS_BLOOD_PRESSURE_FEATURE = '2A49';
Bluetooth4LE.CHARACTERISTICS_BLOOD_PRESSURE_MEASUREMENT = '2A35';
Bluetooth4LE.CHARACTERISTICS_BODY_SENSOR_LOCATION = '2A38';
Bluetooth4LE.CHARACTERISTICS_BOOT_KEYBOARD_INPUT_REPORT = '2A22';
Bluetooth4LE.CHARACTERISTICS_BOOT_KEYBOARD_OUTPUT_REPORT = '2A32';
Bluetooth4LE.CHARACTERISTICS_BOOT_MOUSE_INPUT_REPORT = '2A33';
Bluetooth4LE.CHARACTERISTICS_CURRENT_TIME = '2A2B';
Bluetooth4LE.CHARACTERISTICS_DATE_TIME = '2A08';
Bluetooth4LE.CHARACTERISTICS_DAY_DATE_TIME = '2A0A';
Bluetooth4LE.CHARACTERISTICS_DAY_OF_WEEK = '2A09';
Bluetooth4LE.CHARACTERISTICS_DEVICE_NAME = '2A00';
Bluetooth4LE.CHARACTERISTICS_DST_OFFSET = '2A0D';
Bluetooth4LE.CHARACTERISTICS_EXACT_TIME_256 = '2A0C';
Bluetooth4LE.CHARACTERISTICS_FIRMWARE_REVISION_STRING = '2A26';
Bluetooth4LE.CHARACTERISTICS_GLUCOSE_FEATURE = '2A51';
Bluetooth4LE.CHARACTERISTICS_GLUCOSE_MEASUREMENT = '2A18';
Bluetooth4LE.CHARACTERISTICS_GLUCOSE_MEASUREMENT_CONTEXT = '2A18';
Bluetooth4LE.CHARACTERISTICS_HARDWARE_REVISION_STRING = '2A27';
Bluetooth4LE.CHARACTERISTICS_HEART_RATE_CONTROL_POINT = '2A39';
Bluetooth4LE.CHARACTERISTICS_HEART_RATE_MEASUREMENT = '2A37';
Bluetooth4LE.CHARACTERISTICS_HID_CONTROL_POINT = '2A4C';
Bluetooth4LE.CHARACTERISTICS_HID_INFORMATION = '2A4A';
Bluetooth4LE.CHARACTERISTICS_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST = '2A2A';
Bluetooth4LE.CHARACTERISTICS_INTERMEDIATE_BLOOD_PRESSURE = '2A36';
Bluetooth4LE.CHARACTERISTICS_INTERMEDIATE_TEMPERATURE = '2A1E';
Bluetooth4LE.CHARACTERISTICS_LOCAL_TIME_INFORMATION = '2A0F';
Bluetooth4LE.CHARACTERISTICS_MANUFACTURER_NAME_STRING = '2A29';
Bluetooth4LE.CHARACTERISTICS_MEASUREMENT_INTERVAL = '2A21';
Bluetooth4LE.CHARACTERISTICS_MODEL_NUMBER_STRING = '2A24';
Bluetooth4LE.CHARACTERISTICS_NEW_ALERT = '2A46';
Bluetooth4LE.CHARACTERISTICS_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = '2A04';
Bluetooth4LE.CHARACTERISTICS_PERIPHERAL_PRIVACY_FLAG = '2A02';
Bluetooth4LE.CHARACTERISTICS_PNP_ID = '2A50';
Bluetooth4LE.CHARACTERISTICS_PROTOCOL_MODE = '2A4E';
Bluetooth4LE.CHARACTERISTICS_RECONNECTION_ADDRESS = '2A03';
Bluetooth4LE.CHARACTERISTICS_RECORD_ACCESS_CONTROL_POINT = '2A52';
Bluetooth4LE.CHARACTERISTICS_REFERENCE_TIME_INFORMATION = '2A14';
Bluetooth4LE.CHARACTERISTICS_REPORT = '2A4D';
Bluetooth4LE.CHARACTERISTICS_REPORT_MAP = '2A4B';
Bluetooth4LE.CHARACTERISTICS_RINGER_CONTROL_POINT = '2A40';
Bluetooth4LE.CHARACTERISTICS_RINGER_SETTING = '2A41';
Bluetooth4LE.CHARACTERISTICS_SCAN_INTERVAL_WINDOW = '2A4F';
Bluetooth4LE.CHARACTERISTICS_SCAN_REFRESH = '2A31';
Bluetooth4LE.CHARACTERISTICS_SERIAL_NUMBER_STRING = '2A25';
Bluetooth4LE.CHARACTERISTICS_SERVICE_CHANGED = '2A05';
Bluetooth4LE.CHARACTERISTICS_SOFTWARE_REVISION_STRING = '2A28';
Bluetooth4LE.CHARACTERISTICS_SUPPORTED_NEW_ALERT_CATEGORY = '2A47';
Bluetooth4LE.CHARACTERISTICS_SUPPORTED_UNREAD_ALERT_CATEGORY = '2A48';
Bluetooth4LE.CHARACTERISTICS_SYSTEM_ID = '2A23';
Bluetooth4LE.CHARACTERISTICS_TEMPERATURE_MEASUREMENT = '2A1C';
Bluetooth4LE.CHARACTERISTICS_TEMPERATURE_TYPE = '2A1D';
Bluetooth4LE.CHARACTERISTICS_TIME_ACCURACY = '2A12';
Bluetooth4LE.CHARACTERISTICS_TIME_SOURCE = '2A13';
Bluetooth4LE.CHARACTERISTICS_TIME_UPDATE_CONTROL_POINT = '2A16';
Bluetooth4LE.CHARACTERISTICS_TIME_UPDATE_STATE = '2A17';
Bluetooth4LE.CHARACTERISTICS_TIME_WITH_DST = '2A11';
Bluetooth4LE.CHARACTERISTICS_TIME_ZONE = '2A0E';
Bluetooth4LE.CHARACTERISTICS_TX_POWER_LEVEL = '2A07';
Bluetooth4LE.CHARACTERISTICS_UNREAD_ALERT_STATUS = '2A45';

// descriptors
Bluetooth4LE.DESCRIPTORS_CHARACTERISTIC_AGGREGATE_FORMAT = '2905';
Bluetooth4LE.DESCRIPTORS_CHARACTERISTIC_EXTENDED_PROPERTIES = '2900';
Bluetooth4LE.DESCRIPTORS_CHARACTERISTIC_PRESENTATION_FORMAT = '2904';
Bluetooth4LE.DESCRIPTORS_CHARACTERISTIC_USER_DESCRIPTION = '2901';
Bluetooth4LE.DESCRIPTORS_CLIENT_CHARACTERISTIC_CONFIGURATION = '2902';
Bluetooth4LE.DESCRIPTORS_EXTERNAL_REPORT_REFERENCE = '2907';
Bluetooth4LE.DESCRIPTORS_REPORT_REFERENCE = '2908';
Bluetooth4LE.DESCRIPTORS_SERVER_CHARACTERISTIC_CONFIGURATION = '2903';
Bluetooth4LE.DESCRIPTORS_VALID_RANGE = '2906';

// value type
Bluetooth4LE.VALUE_TYPE_UUID = 0;
Bluetooth4LE.VALUE_TYPE_BOOLEAN = 1;
Bluetooth4LE.VALUE_TYPE_2BIT = 2;
Bluetooth4LE.VALUE_TYPE_NIBBLE = 3;
Bluetooth4LE.VALUE_TYPE_8BIT = 4;
Bluetooth4LE.VALUE_TYPE_16BIT = 5;
Bluetooth4LE.VALUE_TYPE_24BIT = 6;
Bluetooth4LE.VALUE_TYPE_32BIT = 7;
Bluetooth4LE.VALUE_TYPE_UINT8 = 8;
Bluetooth4LE.VALUE_TYPE_UINT12 = 9;
Bluetooth4LE.VALUE_TYPE_UINT16 = 10;
Bluetooth4LE.VALUE_TYPE_UINT24 = 11;
Bluetooth4LE.VALUE_TYPE_UINT32 = 12;
Bluetooth4LE.VALUE_TYPE_UINT40 = 13;
Bluetooth4LE.VALUE_TYPE_UINT48 = 14;
Bluetooth4LE.VALUE_TYPE_UINT64 = 15;
Bluetooth4LE.VALUE_TYPE_UINT128 = 16;
Bluetooth4LE.VALUE_TYPE_SINT8 = 17;
Bluetooth4LE.VALUE_TYPE_SINT12 = 18;
Bluetooth4LE.VALUE_TYPE_SINT16 = 19;
Bluetooth4LE.VALUE_TYPE_SINT24 = 20;
Bluetooth4LE.VALUE_TYPE_SINT32 = 21;
Bluetooth4LE.VALUE_TYPE_SINT48 = 22;
Bluetooth4LE.VALUE_TYPE_SINT64 = 23;
Bluetooth4LE.VALUE_TYPE_SINT128 = 24;
Bluetooth4LE.VALUE_TYPE_FLOAT32 = 25;
Bluetooth4LE.VALUE_TYPE_FLOAT64 = 26;
Bluetooth4LE.VALUE_TYPE_SFLOAT = 27;
Bluetooth4LE.VALUE_TYPE_FLOAT = 28;
Bluetooth4LE.VALUE_TYPE_DUNIT16 = 29;
Bluetooth4LE.VALUE_TYPE_UTF8S = 30;
Bluetooth4LE.VALUE_TYPE_UTF16S = 31;