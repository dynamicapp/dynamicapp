package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.Peripheral.OnReadCharacteristicListener;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.Peripheral.OnReadDescriptorListener;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.Peripheral.OnWriteCharacteristicListener;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.Peripheral.OnWriteDescriptorListener;
import jp.zyyx.dynamicapp.core.Plugin;
import jp.zyyx.dynamicapp.plugins.DynamicAppBluetooth4LE;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;
import jp.zyyx.dynamicapp.wrappers.JSONObjectWrapper;

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
public class PeripheralManager extends Plugin {
	private static final String TAG = "PeripheralManager";

	private static PeripheralManager instance = null;
	private HashMap<String, Peripheral> peripheralList = null;

	private static final String METHOD_WRITE_CHARACTERISTIC = "writeValueForCharacteristic";
	private static final String METHOD_READ_CHARACTERISTIC = "readValueForCharacteristic";
	private static final String METHOD_WRITE_DESCRIPTOR = "writeValueForDescriptor";
	private static final String METHOD_READ_DESCRIPTOR = "readValueForDescriptor";

	// static final int STATE_UNKNOWN = -1;
	static final int STATE_AVAILABLE = 0;
	// static final int STATE_UNAVAILABLE = 1;
	private static final int STATE_CONNECTED = 2;
	//private static final int STATE_DISCONNECTED = 3;
	//private static final int STATE_CONNECTING = 4;

	private OnWriteCharacteristicListener mOnWriteCharacteristicListener = null;
	private OnReadCharacteristicListener mOnReadCharacteristicListener = null;
	private OnWriteDescriptorListener mOnWriteDescriptorListener = null;
	private OnReadDescriptorListener mOnReadDescriptorListener = null;

	private PeripheralManager() {
		super();
		peripheralList = new HashMap<String, Peripheral>();
		mOnWriteCharacteristicListener = new OnWriteCharacteristicListener() {
			@Override
			public void onSuccess() {
				PeripheralManager.onSuccess(new JSONObject(), callbackId, false);
			}
			@Override
			public void onError() {
				PeripheralManager.onError("", callbackId);
			}
		};

		mOnReadCharacteristicListener = new OnReadCharacteristicListener() {
			@Override
			public void onSuccess(String value) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onError() {
				// TODO Auto-generated method stub
			}
		};

		mOnWriteDescriptorListener = new OnWriteDescriptorListener() {
			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
			}
			@Override
			public void onError() {
				// TODO Auto-generated method stub
			}
		};

		mOnReadDescriptorListener = new OnReadDescriptorListener() {
			@Override
			public void onSuccess(String value) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onError() {
				// TODO Auto-generated method stub
			}
		};
	}

	public static synchronized PeripheralManager getInstance() {
		if (instance == null) {
			instance = new PeripheralManager();
		}
		return instance;
	}

	@Override
	public void execute() {
		DebugLog.w(TAG, "method " + methodName + " is called.");
		DebugLog.w(TAG, "parameters are: " + params);
		mainActivity.callJsEvent(PROCESSING_FALSE);

		if (methodName.equalsIgnoreCase(METHOD_WRITE_CHARACTERISTIC)) {
			String peripheralData = param.get("peripheralData", "");
			JSONObjectWrapper jsonPeripheralData = null;

			try {
				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
			} catch (JSONException e) {
				DynamicAppBluetooth4LE.onError("", callbackId);
				if (Utilities.isDebuggable)
					e.printStackTrace();
			}

			String deviceName = jsonPeripheralData.get("deviceName", "unknown");
			String deviceId = jsonPeripheralData.get("id", "unknown");

			Peripheral peripheral = new Peripheral(deviceName, deviceId);
			String value = param.get("value", "");
			int valueType = param.get("valueType", -1);
			int characteristic = param.get("characteristic", -1);
			int writeType = param.get("writeType", -1);

			this.writeCharacteristic(peripheral, value, valueType, characteristic,
					writeType);
			
			// TODO add checking if it is connected to the peripheral
		} else if (methodName.equalsIgnoreCase(METHOD_READ_CHARACTERISTIC)) {
			String peripheralData = param.get("peripheralData", "");
			JSONObjectWrapper jsonPeripheralData = null;

			try {
				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
			} catch (JSONException e) {
				DynamicAppBluetooth4LE.onError("", callbackId);
				if (Utilities.isDebuggable)
					e.printStackTrace();
			}

			String deviceName = jsonPeripheralData.get("deviceName", "unknown");
			String deviceId = jsonPeripheralData.get("id", "unknown");

			Peripheral peripheral = new Peripheral(deviceName, deviceId);
			int characteristic = param.get("characteristic", -1);
			int valueType = param.get("valueType", -1);

			this.readCharacteristic(peripheral, characteristic, valueType);
		} else if (methodName.equalsIgnoreCase(METHOD_WRITE_DESCRIPTOR)) {
			String peripheralData = param.get("peripheralData", "");
			JSONObjectWrapper jsonPeripheralData = null;

			try {
				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
			} catch (JSONException e) {
				DynamicAppBluetooth4LE.onError("", callbackId);
				if (Utilities.isDebuggable)
					e.printStackTrace();
			}

			String deviceName = jsonPeripheralData.get("deviceName", "unknown");
			String deviceId = jsonPeripheralData.get("id", "unknown");

			Peripheral peripheral = new Peripheral(deviceName, deviceId);
			String value = param.get("value", "");
			int characteristic = param.get("characteristic", -1);
			int descriptor = param.get("descriptor", -1);
			int valueType = param.get("valueType", -1);

			this.writeDescriptor(peripheral, value, characteristic, descriptor, valueType);
		} else if (methodName.equalsIgnoreCase(METHOD_READ_DESCRIPTOR)) {
			String peripheralData = param.get("peripheralData", "");
			JSONObjectWrapper jsonPeripheralData = null;

			try {
				jsonPeripheralData = new JSONObjectWrapper(peripheralData);
			} catch (JSONException e) {
				DynamicAppBluetooth4LE.onError("", callbackId);
				if (Utilities.isDebuggable)
					e.printStackTrace();
			}

			String deviceName = jsonPeripheralData.get("deviceName", "unknown");
			String deviceId = jsonPeripheralData.get("id", "unknown");

			Peripheral peripheral = new Peripheral(deviceName, deviceId);
			int characteristic = param.get("characteristic", -1);
			int descriptor = param.get("descriptor", -1);
			int valueType = param.get("valueType", -1);
			
			this.readDescriptor(peripheral, characteristic, descriptor, valueType);
		}
	}

	private void writeCharacteristic(Peripheral peripheral, final String value,
			final int valueType,
			final int characteristic, final int writeType) {
		if (peripheral.getState() != STATE_CONNECTED) {
			PeripheralManager.onError("", callbackId);
		} else {
			peripheral.writeValueForCharacteristic(value, valueType, characteristic,
					writeType, mOnWriteCharacteristicListener);
		}
	}

	private void readCharacteristic(Peripheral peripheral,
			final int characteristic, final int valueType) {
		if (peripheral.getState() != STATE_CONNECTED) {
			PeripheralManager.onError("", callbackId);
		} else {
			peripheral.readValueForCharacteristic(characteristic,valueType,
					mOnReadCharacteristicListener);
		}
	}

	private void writeDescriptor(Peripheral peripheral, final String value,
			final int valueType,
			final int characteristic, final int descriptor) {
		if (peripheral.getState() != STATE_CONNECTED) {
			PeripheralManager.onError("", callbackId);
		} else {
			peripheral.writeValueForDescriptor(value, valueType, characteristic,
					descriptor, mOnWriteDescriptorListener);
		}
	}

	private void readDescriptor(Peripheral peripheral,
			final int characteristic, final int descriptor, final int valueType) {
		if (peripheral.getState() != STATE_CONNECTED) {
			PeripheralManager.onError("", callbackId);
		} else {
			peripheral.readValueForDescriptor(characteristic, descriptor, valueType, mOnReadDescriptorListener);
		}
	}
	
	public boolean addPeriperal(Peripheral peripheral) {
		boolean added = false;
		String address = peripheral.getId();
		
		if(peripheralList.get(address) == null) {
			peripheral.setState(STATE_AVAILABLE, true);
			peripheralList.put(address, peripheral);
			added = true;
		}
		
		return added;
	}
	
	public Peripheral getPeripheral(String address) {
		return peripheralList.get(address);
	}
}
