package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile;

import java.util.ArrayList;

import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile.BaseClientProfile;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientService.AlertNotificationServiceClient;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import com.broadcom.bt.le.api.BleGattID;
import com.broadcom.bt.le.api.BleClientService;

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
public class AlertNotificationProfileClient extends BaseClientProfile {
	private static final String TAG = "AlertNotificationProfileClient";

	private static final BleGattID myUuid = new BleGattID("015f613f-fe1d-475b-b0da-dd947ead9c2a");
	private Context mContext = null;
	private AlertNotificationServiceClient mAlertService = new AlertNotificationServiceClient();

	//private static final BleGattID ALERT_LEVEL_CHARACTERISTIC = new BleGattID("00002a06-0000-1000-8000-00805f9b34fb");
	public static final String FINDME_CONNECTED = "com.broadcom.action.findme_connected";
    public static final String FINDME_DISCONNECTED = "com.broadcom.action.findme_disconnected";

    public final static int ALERT_LEVEL_NONE = 0;
    public final static int ALERT_LEVEL_LOW = 1;
    public final static int ALERT_LEVEL_HIGH = 2;

//    private static final BleGattID CHARACTERISTICS_ALERT_CATEGORY_ID = new BleGattID(0x2A43);
//    private static final BleGattID CHARACTERISTICS_ALERT_CATEGORY_ID_BIT_MASK = new BleGattID(0x2A42);
//    private static final BleGattID CHARACTERISTICS_ALERT_LEVEL = new BleGattID(0x2A06);
//    private static final BleGattID CHARACTERISTICS_ALERT_NOTIFICATION_CONTROL_POINT = new BleGattID(0x2A44);
//    private static final BleGattID CHARACTERISTICS_ALERT_STATUS = new BleGattID(0x2A3F);

//    private Peripheral peripheral = null;
    
	public AlertNotificationProfileClient(Context context) {
		super(context, myUuid);
		mContext = context;

		DebugLog.w(TAG, "AlertNotificationProfileClient");

		ArrayList<BleClientService> services = new ArrayList<BleClientService>();
		services.add(mAlertService);

		init(services, null);
	}

	public synchronized void deregister() throws InterruptedException {
		deregisterProfile();
		wait(5000);
	}

	public void alert(BluetoothDevice device) {
		/*BleCharacteristic alertLevelCharacteristic = mAlertService
				.getCharacteristic(device, ALERT_LEVEL_CHARACTERISTIC);

		byte[] value = { AlertNotificationProfileClient.ALERT_LEVEL_HIGH };
		alertLevelCharacteristic.setValue(value);

		mAlertService.writeCharacteristic(device, 0,
				alertLevelCharacteristic);*/
		
//		BleCharacteristic alertCategoryIdCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_CATEGORY_ID);
		
//		BleCharacteristic alertCategoryIdBitMaskCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_CATEGORY_ID_BIT_MASK);
		
//		BleCharacteristic alertLevelCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_LEVEL);
		
//		BleCharacteristic alertNotificationControlPointCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_NOTIFICATION_CONTROL_POINT);
		
//		BleCharacteristic alertStatusCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_STATUS);
	}

	public void onInitialized(boolean success) {
		DebugLog.w(TAG, "onInitialized");
		if (success) {
			registerProfile();
		}
	}

	public void onDeviceConnected(BluetoothDevice device) {
		DebugLog.w(TAG, "onDeviceConnected");
		refresh(device);
		
//		String deviceName = device.getName();
//		String macAddress = device.getAddress();
//		peripheral = new Peripheral(deviceName, macAddress);
	}

	public void onDeviceDisconnected(BluetoothDevice device) {
		DebugLog.w(TAG, "onDeviceDisconnected");

		Intent intent = new Intent();
		intent.setAction(FINDME_DISCONNECTED);
		intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
		mContext.sendBroadcast(intent);

		connectBackground(device);
	}

	public void onRefreshed(BluetoothDevice device) {
		DebugLog.w(TAG, "onRefreshed");

		Intent intent = new Intent();
		intent.setAction(FINDME_CONNECTED);
		intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
		mContext.sendBroadcast(intent);
	}

	public void onProfileRegistered() {
		DebugLog.w(TAG, "onProfileRegistered");
	}

	public void onProfileDeregistered() {
		DebugLog.w(TAG, "onProfileDeregistered");
		notifyAll();
	}

}
