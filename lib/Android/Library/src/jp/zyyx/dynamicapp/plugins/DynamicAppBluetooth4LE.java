package jp.zyyx.dynamicapp.plugins;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.broadcom.bt.le.api.BleAdapter;

import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.Peripheral;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.PeripheralManager;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile.*;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientService.BtLE4ClientServices;
import jp.zyyx.dynamicapp.core.Plugin;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;
import jp.zyyx.dynamicapp.wrappers.JSONObjectWrapper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.os.Parcelable;

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
public class DynamicAppBluetooth4LE extends Plugin {
	private static final String TAG = "DynamicAppBluetooth4LE";

	private static DynamicAppBluetooth4LE instance = null;
	private BluetoothAdapter mBtAdapter = null;
	private ArrayList<BluetoothDevice> deviceList;
	
	private ArrayList<String> serviceList;
	private boolean isUnregistered = false;
	BluetoothDevice btDevice = null;
	
	private static final String METHOD_SCAN = "scan";
	private static final String METHOD_CONNECT = "connect";
	private static final String METHOD_DISCONNECT = "disconnect";
	private static final String METHOD_WRITE_CHARACTERISTIC = "writeValueForCharacteristic";
	private static final String METHOD_READ_CHARACTERISTIC = "readValueForCharacteristic";
	private static final String METHOD_WRITE_DESCRIPTOR = "writeValueForDescriptor";
	private static final String METHOD_READ_DESCRIPTOR = "readValueForDescriptor";

//	private static final int STATE_UNKNOWN = -1;
	private static final int STATE_AVAILABLE = 0;
//	private static final int STATE_UNAVAILABLE = 1;
	private static final int STATE_CONNECTED = 2;
//	private static final int STATE_DISCONNECTED = 3;
	private static final int STATE_CONNECTING = 4;
	
	private PeripheralManager peripheralManager = null;
	private JSONArray scanServices = null;
	
	// write type
//	private static final int WRITETYPE_WRITEWITHRESPONSE = 0;
//	private static final int WRITETYPE_WRITEWITHOUTRESPONSE = 1;

	/*
	// services
	private static final String SERVICE_ALERT_NOTIFICATION = "1811";
	private static final String SERVICE_BATTERY_SERVICE = "180F";
	private static final String SERVICE_BLOOD_PRESSURE = "1810";
	private static final String SERVICE_CYCLINGSPEED_AND_CADENCE = "1816";	
	private static final String SERVICE_CURRENT_TIME = "1805";
	private static final String SERVICE_DEVICE_INFORMATION = "180A";
	private static final String SERVICE_GENERIC_ACCESS = "1800";
	private static final String SERVICE_GENERIC_ATTRIBUTE = "1801";
	private static final String SERVICE_GLUCOSE = "1808";
	private static final String SERVICE_HEALTH_THERMOMETER = "1809";
	private static final String SERVICE_HEART_RATE = "180D";
	private static final String SERVICE_HUMAN_INTERFACE_DEVICE = "1812";
	private static final String SERVICE_IMMEDIATE_ALERT = "1802";
	private static final String SERVICE_HUMAN_LINK_LOSS = "1803";
	private static final String SERVICE_NEXT_DST_CHANGE = "1807";
	private static final String SERVICE_PHONE_ALERT_STATUS = "180E";
	private static final String SERVICE_REFERENCE_TIME_UPDATE = "1806";
	private static final String SERVICE_SCAN_PARAMETERS = "1813";
	private static final String SERVICE_TX_POWER = "1804";
	

	// characteristics
	private static final int CHARACTERISTICS_ALERT_CATEGORY_ID = 0x2A43;
	private static final int CHARACTERISTICS_ALERT_CATEGORY_ID_BIT_MASK = 0x2A42;
	private static final int CHARACTERISTICS_ALERT_LEVEL = 0x2A06;
	private static final int CHARACTERISTICS_ALERT_NOTIFICATION_CONTROL_POINT = 0x2A44;
	private static final int CHARACTERISTICS_ALERT_STATUS = 0x2A3F;
	private static final int CHARACTERISTICS_APPEARANCE = 0x2A01;
	private static final int CHARACTERISTICS_BATTERY_LEVEL = 0x2A19;
	private static final int CHARACTERISTICS_BLOOD_PRESSURE_FEATURE = 0x2A49;
	private static final int CHARACTERISTICS_BLOOD_PRESSURE_MEASUREMENT = 0x2A35;
	private static final int CHARACTERISTICS_BODY_SENSOR_LOCATION = 0x2A38;
	private static final int CHARACTERISTICS_BOOT_KEYBOARD_INPUT_REPORT = 0x2A22;
	private static final int CHARACTERISTICS_BOOT_KEYBOARD_OUTPUT_REPORT = 0x2A32;
	private static final int CHARACTERISTICS_BOOT_MOUSE_INPUT_REPORT = 0x2A33;
	private static final int CHARACTERISTICS_CURRENT_TIME = 0x2A2B;
	private static final int CHARACTERISTICS_DATE_TIME = 0x2A08;
	private static final int CHARACTERISTICS_DAY_DATE_TIME = 0x2A0A;
	private static final int CHARACTERISTICS_DAY_OF_WEEK = 0x2A09;
	private static final int CHARACTERISTICS_DEVICE_NAME = 0x2A00;
	private static final int CHARACTERISTICS_DST_OFFSET = 0x2A0D;
	private static final int CHARACTERISTICS_EXACT_TIME_256 = 0x2A0C;
	private static final int CHARACTERISTICS_FIRMWARE_REVISION_STRING = 0x2A26;
	private static final int CHARACTERISTICS_GLUCOSE_FEATURE = 0x2A51;
	private static final int CHARACTERISTICS_GLUCOSE_MEASUREMENT = 0x2A18;
	private static final int CHARACTERISTICS_GLUCOSE_MEASUREMENT_CONTEXT = 0x2A18;
	private static final int CHARACTERISTICS_HARDWARE_REVISION_STRING = 0x2A27;
	private static final int CHARACTERISTICS_HEART_RATE_CONTROL_POINT = 0x2A39;
	private static final int CHARACTERISTICS_HEART_RATE_MEASUREMENT = 0x2A37;
	private static final int CHARACTERISTICS_HID_CONTROL_POINT = 0x2A4C;
	private static final int CHARACTERISTICS_HID_INFORMATION = 0x2A4A;
	private static final int CHARACTERISTICS_IEEE_11073_20601_REGULATORY_CERTIFICATION_DATA_LIST = 0x2A2A;
	private static final int CHARACTERISTICS_INTERMEDIATE_BLOOD_PRESSURE = 0x2A36;
	private static final int CHARACTERISTICS_INTERMEDIATE_TEMPERATURE = 0x2A1E;
	private static final int CHARACTERISTICS_LOCAL_TIME_INFORMATION = 0x2A0F;
	private static final int CHARACTERISTICS_MANUFACTURER_NAME_STRING = 0x2A29;
	private static final int CHARACTERISTICS_MEASUREMENT_INTERVAL = 0x2A21;
	private static final int CHARACTERISTICS_MODEL_NUMBER_STRING = 0x2A24;
	private static final int CHARACTERISTICS_NEW_ALERT = 0x2A46;
	private static final int CHARACTERISTICS_PERIPHERAL_PREFERRED_CONNECTION_PARAMETERS = 0x2A04;
	private static final int CHARACTERISTICS_PERIPHERAL_PRIVACY_FLAG = 0x2A02;
	private static final int CHARACTERISTICS_PNP_ID = 0x2A50;
	private static final int CHARACTERISTICS_PROTOCOL_MODE = 0x2A4E;
	private static final int CHARACTERISTICS_RECONNECTION_ADDRESS = 0x2A03;
	private static final int CHARACTERISTICS_RECORD_ACCESS_CONTROL_POINT = 0x2A52;
	private static final int CHARACTERISTICS_REFERENCE_TIME_INFORMATION = 0x2A14;
	private static final int CHARACTERISTICS_REPORT = 0x2A4D;
	private static final int CHARACTERISTICS_REPORT_MAP = 0x2A4B;
	private static final int CHARACTERISTICS_RINGER_CONTROL_POINT = 0x2A40;
	private static final int CHARACTERISTICS_RINGER_SETTING = 0x2A41;
	private static final int CHARACTERISTICS_SCAN_INTERVAL_WINDOW = 0x2A4F;
	private static final int CHARACTERISTICS_SCAN_REFRESH = 0x2A31;
	private static final int CHARACTERISTICS_SERIAL_NUMBER_STRING = 0x2A25;
	private static final int CHARACTERISTICS_SERVICE_CHANGED = 0x2A05;
	private static final int CHARACTERISTICS_SOFTWARE_REVISION_STRING = 0x2A28;
	private static final int CHARACTERISTICS_SUPPORTED_NEW_ALERT_CATEGORY = 0x2A47;
	private static final int CHARACTERISTICS_SUPPORTED_UNREAD_ALERT_CATEGORY = 0x2A48;
	private static final int CHARACTERISTICS_SYSTEM_ID = 0x2A23;
	private static final int CHARACTERISTICS_TEMPERATURE_MEASUREMENT = 0x2A1C;
	private static final int CHARACTERISTICS_TEMPERATURE_TYPE = 0x2A1D;
	private static final int CHARACTERISTICS_TIME_ACCURACY = 0x2A12;
	private static final int CHARACTERISTICS_TIME_SOURCE = 0x2A13;
	private static final int CHARACTERISTICS_TIME_UPDATE_CONTROL_POINT = 0x2A16;
	private static final int CHARACTERISTICS_TIME_UPDATE_STATE = 0x2A17;
	private static final int CHARACTERISTICS_TIME_WITH_DST = 0x2A11;
	private static final int CHARACTERISTICS_TIME_ZONE = 0x2A0E;
	private static final int CHARACTERISTICS_TX_POWER_LEVEL = 0x2A07;
	private static final int CHARACTERISTICS_UNREAD_ALERT_STATUS = 0x2A07;
	private static final int CHARACTERISTICS_PULSE_OXIMETRY_CONTROL_POINT = 0x2A62;
	private static final int CHARACTERISTICS_PULSE_OXIMETRY_FEATURES = 0x2A61;
	private static final int CHARACTERISTICS_PULSE_OXIMETRY_PULSATILE_EVENT = 0x2A60;
	private static final int CHARACTERISTICS_PULSE_OXIMETRY_SPOT_CHECK_MEASUREMENT = 0x2A5E;

	// descriptors
	private static final int DESCRIPTORS_CHARACTERISTIC_AGGREGATE_FORMAT = 0x2905;
	private static final int DESCRIPTORS_CHARACTERISTIC_EXTENDED_PROPERTIES = 0x2900;
	private static final int DESCRIPTORS_CHARACTERISTIC_PRESENTATION_FORMAT = 0x2904;
	private static final int DESCRIPTORS_CHARACTERISTIC_USER_DESCRIPTION = 0x2901;
	private static final int DESCRIPTORS_CLIENT_CHARACTERISTIC_CONFIGURATION = 0x2902;
	private static final int DESCRIPTORS_EXTERNAL_REPORT_REFERENCE = 0x2907;
	private static final int DESCRIPTORS_REPORT_REFERENCE = 0x2908;
	private static final int DESCRIPTORS_SERVER_CHARACTERISTIC_CONFIGURATION = 0x2903;
	private static final int DESCRIPTORS_VALID_RANGE = 0x2906;

*/
	private DynamicAppBluetooth4LE() {
		super();
		mBtAdapter = BluetoothAdapter.getDefaultAdapter();
		deviceList = new ArrayList<BluetoothDevice>();
		peripheralManager = PeripheralManager.getInstance();
	}

	public static synchronized DynamicAppBluetooth4LE getInstance() {
		if (instance == null) {
			instance = new DynamicAppBluetooth4LE();
		}

		return instance;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		final int OK = Activity.RESULT_OK;
		if (requestCode == ACTIVITY_REQUEST_CD_PAIR_DEVICE) {
			DebugLog.w(TAG, "intent: "+ intent);
		} else if(requestCode == ACTIVITY_REQUEST_CD_ENABLE_BT) {
			if(resultCode == OK) {
				this.scanDevices();
			}
		}
	}

	@Override
	public void onBackKeyDown() {
		try {
			if(isUnregistered) {
				mainActivity.unregisterReceiver(mReceiver);
				isUnregistered = false;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
		
		instance = null;
	}
	
	@Override
	public void onPause() {
		unregisterReceiver();
	}

	@Override
	public void onResume() {
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction( BleAdapter.ACTION_UUID);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        Utilities.dynamicAppActivityRef.registerReceiver(mReceiver, filter);
        isUnregistered = true;
        //populateList();
	}
	
	public void unregisterReceiver() {
		try {
			if(isUnregistered) {
				Utilities.dynamicAppActivityRef.unregisterReceiver(mReceiver);
				isUnregistered = false;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void execute() {
		DebugLog.w(TAG, "method " + methodName + " is called.");
		DebugLog.w(TAG, "parameters are: " + params);
		mainActivity.callJsEvent(PROCESSING_FALSE);
		
		if (methodName.equalsIgnoreCase(METHOD_SCAN)) {
			this.serviceList = new ArrayList<String>();
			scanServices = param.get("services", new JSONArray());

			try {
				for(int i=0; i<scanServices.length(); i++) {
					String service = scanServices.getString(i);
					if(BtLE4ClientServices.hasService(service)) {
						//BleGattID myuuid = new BleGattID(makeUUID(service));
						//DebugLog.i(TAG, "myuuid.toString() : " + myuuid.toString());
						this.serviceList.add(BtLE4ClientServices.makeUUIDs(service));
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			this.scanDevices();
		} else if(methodName.equalsIgnoreCase(METHOD_CONNECT)) {
			String peripheralData = param.get("peripheralData", "");
			JSONObjectWrapper jsonPeripheralData = null;

			try {
				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
			} catch (JSONException e) {
				DynamicAppBluetooth4LE.onError("", callbackId);
				if(Utilities.isDebuggable)
					e.printStackTrace();
			}
			
			String deviceName = jsonPeripheralData.get("deviceName", "unknown");
			String deviceId = jsonPeripheralData.get("id", "unknown");
			int state = jsonPeripheralData.get("state", STATE_CONNECTING);
			
			Peripheral peripheral = new Peripheral(deviceName, deviceId);
			peripheral.setState(state, false);
			
			this.connect(peripheral);
		} else if(methodName.equalsIgnoreCase(METHOD_DISCONNECT)) {
			String peripheralData = param.get("peripheralData", "");
			JSONObjectWrapper jsonPeripheralData = null;

			try {
				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
			} catch (JSONException e) {
				DynamicAppBluetooth4LE.onError("", callbackId);
				if(Utilities.isDebuggable)
					e.printStackTrace();
			}
			
			String deviceName = jsonPeripheralData.get("deviceName", "unknown");
			String deviceId = jsonPeripheralData.get("id", "unknown");
			int state = jsonPeripheralData.get("state", STATE_AVAILABLE);
			
			Peripheral peripheral = new Peripheral(deviceName, deviceId);
			peripheral.setState(state, false);
			
			this.disconnect(peripheral);
		} else if(methodName.equalsIgnoreCase(METHOD_WRITE_CHARACTERISTIC)) {
			String peripheralData = param.get("peripheralData", "");
			JSONObjectWrapper jsonPeripheralData = null;

			try {
				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
			} catch (JSONException e) {
				DynamicAppBluetooth4LE.onError("", callbackId);
				if(Utilities.isDebuggable)
					e.printStackTrace();
			}
			
			String deviceName = jsonPeripheralData.get("deviceName", "unknown");
			String deviceId = jsonPeripheralData.get("id", "unknown");
			String value = param.get("value", "");
			int valueType = Integer.valueOf(param.get("valueType", "0"));
			String service = param.get("service", "");
			String characteristic = param.get("characteristic", "");
			int writeType = Integer.valueOf(param.get("writeType", "0"));
			
			Peripheral peripheral = new Peripheral(deviceName, deviceId);
			
			this.writeValueForCharacteristoc(peripheral, value, valueType, service, characteristic, writeType);
		} else if(methodName.equalsIgnoreCase(METHOD_READ_CHARACTERISTIC)) {
//			String peripheralData = param.get("peripheralData", "");
//			JSONObjectWrapper jsonPeripheralData = null;
//
//			try {
//				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
//			} catch (JSONException e) {
//				DynamicAppBluetooth4LE.onError("", callbackId);
//				if(DynamicAppUtils.DEBUG)
//					e.printStackTrace();
//			}			
		} else if(methodName.equalsIgnoreCase(METHOD_WRITE_DESCRIPTOR)) {
//			String peripheralData = param.get("peripheralData", "");
//			JSONObjectWrapper jsonPeripheralData = null;
//
//			try {
//				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
//			} catch (JSONException e) {
//				DynamicAppBluetooth4LE.onError("", callbackId);
//				if(DynamicAppUtils.DEBUG)
//					e.printStackTrace();
//			}			
		} else if(methodName.equalsIgnoreCase(METHOD_READ_DESCRIPTOR)) {
//			String peripheralData = param.get("peripheralData", "");
//			JSONObjectWrapper jsonPeripheralData = null;
//
//			try {
//				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
//			} catch (JSONException e) {
//				DynamicAppBluetooth4LE.onError("", callbackId);
//				if(DynamicAppUtils.DEBUG)
//					e.printStackTrace();
//			}			
		}
	}
	
	/**
     * The BroadcastReceiver that listens for discovered devices
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @SuppressLint("NewApi")
		public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
           
            if (methodName != null && BluetoothDevice.ACTION_FOUND.equals(action)) {
            	DebugLog.w(TAG, "Found device.");
                byte deviceType = intent.getByteExtra(BleAdapter.EXTRA_DEVICE_TYPE, BleAdapter.DEVICE_TYPE_BREDR);
                String type = String.valueOf(deviceType);
                DebugLog.w(TAG, "deviceType :" + type);
                type = String.valueOf(BleAdapter.DEVICE_TYPE_BLE);
                DebugLog.w(TAG, "BleAdapter.DEVICE_TYPE_BLE :" + type);
 //               if (deviceType == BleAdapter.DEVICE_TYPE_BLE) {
                	BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					String deviceName = (device.getName() != null) ? device
						.getName() : "unknown";
//					String address = device.getAddress();
				
					DebugLog.w(TAG, "deviceName :" + deviceName);

					if (Build.VERSION.SDK_INT >= 15) {
						device.fetchUuidsWithSdp();
					} else {
						Peripheral peripheral = new Peripheral(device.getName(), device.getAddress());
	            		peripheral.setState(STATE_AVAILABLE, true);
	            		peripheralManager.addPeriperal(peripheral);
						
						addDevice(device);
					}
//               }
            }
           
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	DebugLog.w(TAG, "Device scan is finished.");
                // hide loading screen
                if (deviceList.size() == 0) {
                   // no devices are found.                    
                } else {
 //               	for(BluetoothDevice listDev : deviceList) {
 //               		BleAdapter.getRemoteServices(listDev.getAddress());	
 //                	}
                }
            }
            
            if (BleAdapter.ACTION_UUID.equals(action)) {
            	DebugLog.w(TAG, "ACTION_UUID.");
                Bundle bundle = intent.getExtras();
                Parcelable[] uuids = bundle.getParcelableArray(BluetoothDevice.EXTRA_UUID);
                if(uuids != null) {
	                for( int i = 0; i != uuids.length; ++i ) {
	                    ParcelUuid uuid = (ParcelUuid) uuids[i];
	                    
	                    if(isSearching(uuid.toString())) {
		                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		                    DebugLog.w(TAG, "Device name:" +device.getName() + ", uuid: " + uuid.toString());
		                    
		                    Peripheral peripheral = new Peripheral(device.getName(), device.getAddress());
	                    	peripheral.setState(STATE_AVAILABLE, true);
	                    	peripheralManager.addPeriperal(peripheral);
		                    
	                    	addDevice(device);
		                }
	                }
                }
            }
            
            if (BluetoothDevice.ACTION_ACL_CONNECTED
					.equals(action)) {
				DebugLog.w(TAG, "device is about to connect...");
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceName = (device.getName() != null) ? device
						.getName() : "unknown";
				String address = device.getAddress();
				
				Peripheral peripheral = new Peripheral(deviceName, address);
            	peripheral.setState(STATE_CONNECTED, true);
            
                //addDevice(device);
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED
					.equals(action)) {
				//DebugLog.i(TAG, "device is about to disconnect...");
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED
					.equals(action)) {
				/*BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceName = (device.getName() != null) ? device
						.getName() : "unknown";
				String address = device.getAddress();
				
				Peripheral peripheral = new Peripheral(deviceName, address);
            	peripheral.setState(STATE_DISCONNECTED, true);*/
			}
            
        }
    };

    private boolean isSearching(String uuid) {
    	for(String sUUID : this.serviceList) {
    		if(sUUID.compareToIgnoreCase(uuid) == 0) {
    			return true;
    		}
    	}
    	return false;
    }
	
	// plug-in methods
//	private void populateList() {
//        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
//        
//        for(BluetoothDevice pairedDevice: pairedDevices) {
//        	addDevice(pairedDevice);
//        }
//    }
	
	private void addDevice(BluetoothDevice device) {
        boolean deviceFound = false;
       
        for(BluetoothDevice listDev: deviceList) {
                if (listDev.getAddress().equals(device.getAddress())) {
                        deviceFound = true;
                        break;
                }
        }
       
        if (!deviceFound) {
           deviceList.add(device);
        }
    }
	
	private BluetoothDevice getDevice(String address) {
		BluetoothDevice device = null;
		for(BluetoothDevice listDev: deviceList) {
			if(listDev.getAddress().compareToIgnoreCase(address) == 0) {
				device = listDev;
				break;
			}
		}
		
		return device;
	}
	
	private void scanDevices() {
		if (mBtAdapter == null) {
			DebugLog.w(TAG, "Bluetooth is unsupported.");
		} else {
			if (mBtAdapter.isEnabled()) {
				makeDeviceAlwaysDiscoverable();
				DebugLog.w(TAG, "Bluetooth is enabled");
				mBtAdapter.startDiscovery();
			} else {
				this.enableBluetoothByIntent();
			}
		}
	}
	
	private void makeDeviceAlwaysDiscoverable() {
    	Intent discoverableIntent = new
		Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
		mainActivity.startActivity(discoverableIntent);
    }
	
	private void enableBluetoothByIntent() {
		Intent enableBtIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		mainActivity.startActivityForResult(enableBtIntent,
				ACTIVITY_REQUEST_CD_ENABLE_BT);
	}
	
	private void connect(Peripheral peripheral) {
		mBtAdapter.cancelDiscovery();

		// TODO connect to peripheral
		String address = peripheral.getId();
		DebugLog.w(TAG, "address:" + address);
//		BluetoothDevice device = mBtAdapter.getRemoteDevice(address);
    	peripheral.setState(STATE_CONNECTED, true);
    	
		DynamicAppBluetooth4LE.onSuccess("", callbackId, false);
/*		
		if (Build.VERSION.SDK_INT >= 15) {
			device.fetchUuidsWithSdp();
		} else {
			
		}
*/

	}
	
	private void disconnect(Peripheral peripheral) {
		// TODO disconnect to peripheral
	}
	
	private void writeValueForCharacteristoc(Peripheral peripheral, String value, int valueType, 
			String service, String characteristic, int writeType) {
		
		BaseClientProfile profile = getProfileForService(service);
		if(profile == null) {
			// error
			return;
		}
		
		BluetoothDevice device = getDevice(peripheral.getId());
		if(device == null) {
			//error
			return;
		}
		
		profile.writeValueForCharacteristic(device, value, valueType, service, characteristic);
	}
	
//	private void readValueForCharacteristic(Peripheral peripheral) {
//		
//	}
	
//	private void writeValueForDescriptor(Peripheral peripheral) {
//		
//	}
	
//	private void readValueForDescriptor(Peripheral peripheral) {
//		
//	}
	
	private BaseClientProfile getProfileForService(String service) {
		BaseClientProfile profile = null;

		if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_ALERT_NOTIFICATION)) 
		{
			profile = new AlertNotificationProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_BATTERY_SERVICE) || 
				service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_HUMAN_INTERFACE_DEVICE)) 
		{
			profile = new HIDOVERGATTProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_BLOOD_PRESSURE) || 
				service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_DEVICE_INFORMATION)) 
		{
			profile = new BloodPressureProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_CURRENT_TIME) || 
				service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_REFERENCE_TIME_UPDATE) || 
				service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_NEXT_DST_CHANGE)) 
		{
			profile = new TimeProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_CYCLINGSPEED_AND_CADENCE)) 
		{
			profile = new CyclingSpeedAndCadenceProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_GENERIC_ACCESS)) 
		{
			// ?
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_GENERIC_ATTRIBUTE)) 
		{
			// ?
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_GLUCOSE)) 
		{
			profile = new GlucoseProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_HEALTH_THERMOMETER)) 
		{
			profile = new HealthThermometerProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_HEART_RATE)) 
		{
			profile = new HeartRateProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_IMMEDIATE_ALERT)) 
		{
			profile = new FindMeProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_HUMAN_LINK_LOSS) || 
				service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_TX_POWER)) 
		{
			profile = new ProximityProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_PHONE_ALERT_STATUS)) 
		{
			profile = new PhoneAlertStatusProfileClient(Utilities.dynamicAppActivityRef);
		} 
		else if(service.equalsIgnoreCase(BtLE4ClientServices.SERVICE_SCAN_PARAMETERS)) 
		{
			profile = new ScanParametersProfileClient(Utilities.dynamicAppActivityRef);
		}

		return profile;
	}
	

}
