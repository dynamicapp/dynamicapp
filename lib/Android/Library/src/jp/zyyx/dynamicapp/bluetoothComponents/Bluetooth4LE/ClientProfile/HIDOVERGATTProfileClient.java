package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile.BaseClientProfile;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientService.HumanInterfaceDeviceServiceClient;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientService.BatteryServiceClient;

import com.broadcom.bt.le.api.BleClientService;
import com.broadcom.bt.le.api.BleGattID;

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
public class HIDOVERGATTProfileClient extends BaseClientProfile {
    private static String TAG = "HIDOVERGATTProfileClient";

    public static final String FINDME_CONNECTED = "com.broadcom.action.findme_connected";
    public static final String FINDME_DISCONNECTED = "com.broadcom.action.findme_disconnected";

    public final static int ALERT_LEVEL_NONE = 0;
    public final static int ALERT_LEVEL_LOW = 1;
    public final static int ALERT_LEVEL_HIGH = 2;

    private static final BleGattID myUuid = new BleGattID("015f613f-fe1d-475b-b0da-dd947ead9c3b");
//    private static final BleGattID ALERT_LEVEL_CHARACTERISTIC = new BleGattID("00002a06-0000-1000-8000-00805f9b34fb");

    private HumanInterfaceDeviceServiceClient mHumanInterfaceDevice = new HumanInterfaceDeviceServiceClient();
    private BatteryServiceClient mBatteryService = new BatteryServiceClient();
    
    public HIDOVERGATTProfileClient(Context context) {
        super(context, myUuid);

        Log.d(TAG, "HIDOVERGATTProfileClient");

        ArrayList<BleClientService> services = new ArrayList<BleClientService>();
        services.add(mHumanInterfaceDevice);
        services.add(mBatteryService);

		addService(mHumanInterfaceDevice);
		addService(mBatteryService);
        
        init(services, null);
    }
   
    public synchronized void deregister() throws InterruptedException {
        deregisterProfile();
                wait(5000);
    }

    public void alert(BluetoothDevice device) {
	    /*BleCharacteristic alertLevelCharacteristic =
	                mImmediateAlertService.getCharacteristic(device, ALERT_LEVEL_CHARACTERISTIC);
	
	            byte[] value = { FindMeProfileClient.ALERT_LEVEL_HIGH };
	            alertLevelCharacteristic.setValue(value);
	
	            mImmediateAlertService.writeCharacteristic(device, 0, alertLevelCharacteristic);*/
    }
   
    public void onInitialized(boolean success) {
        Log.d(TAG, "onInitialized");
        if (success) {
                registerProfile();
        }
    }

    public void onDeviceConnected(BluetoothDevice device) {
        Log.d(TAG, "onDeviceConnected");        
        refresh(device);
    }

    public void onDeviceDisconnected(BluetoothDevice device) {
        Log.d(TAG, "onDeviceDisconnected");
       
        /*Intent intent = new Intent();
        intent.setAction(FINDME_DISCONNECTED);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
        mContext.sendBroadcast(intent);
       
        connectBackground(device);*/
    }

    public void onRefreshed(BluetoothDevice device) {
        Log.d(TAG, "onRefreshed");
       
        /*Intent intent = new Intent();
        intent.setAction(FINDME_CONNECTED);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
        mContext.sendBroadcast(intent);*/
    }

    public void onProfileRegistered() {
        Log.d(TAG, "onProfileRegistered");
    }

    public void onProfileDeregistered() {
        Log.d(TAG, "onProfileDeregistered");
        notifyAll();
    }
}
