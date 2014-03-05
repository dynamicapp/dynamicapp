package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile;

import java.util.Collection;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;

import com.broadcom.bt.le.api.BleClientProfile;
import com.broadcom.bt.le.api.BleGattID;
import com.broadcom.bt.le.api.BleClientService;
import com.broadcom.bt.le.api.BleCharacteristic;

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
public class BaseClientProfile extends BleClientProfile {
    private static String TAG = "BaseClientProfile";

	// The type of process;
	public static final int PROCESS_TYPE_WRITE = 0;
	public static final int PROCESS_TYPE_READ = 1;
	
	// The type of value.
	public static final int VALUE_TYPE_UUID = 0;
	public static final int VALUE_TYPE_BOOLEAN = 1;
	public static final int VALUE_TYPE_2BIT = 2;
	public static final int VALUE_TYPE_NIBBLE = 3;
	public static final int VALUE_TYPE_8BIT = 4;
	public static final int VALUE_TYPE_16BIT = 5;
	public static final int VALUE_TYPE_24BIT = 6;
	public static final int VALUE_TYPE_32BIT = 7;
	public static final int VALUE_TYPE_UINT8 = 8;
	public static final int VALUE_TYPE_UINT12 = 9;
	public static final int VALUE_TYPE_UINT16 = 10;
	public static final int VALUE_TYPE_UINT24 = 11;
	public static final int VALUE_TYPE_UINT32 = 12;
	public static final int VALUE_TYPE_UINT40 = 13;
	public static final int VALUE_TYPE_UINT48 = 14;
	public static final int VALUE_TYPE_UINT64 = 15;
	public static final int VALUE_TYPE_UINT128 = 16;
	public static final int VALUE_TYPE_SINT8 = 17;
	public static final int VALUE_TYPE_SINT12 = 18;
	public static final int VALUE_TYPE_SINT16 = 19;
	public static final int VALUE_TYPE_SINT24 = 20;
	public static final int VALUE_TYPE_SINT32 = 21;
	public static final int VALUE_TYPE_SINT48 = 22;
	public static final int VALUE_TYPE_SINT64 = 23;
	public static final int VALUE_TYPE_SINT128 = 24;
	public static final int VALUE_TYPE_FLOAT32 = 25;
	public static final int VALUE_TYPE_FLOAT64 = 26;
	public static final int VALUE_TYPE_SFLOAT = 27;
	public static final int VALUE_TYPE_FLOAT = 28;
	public static final int VALUE_TYPE_DUNIT16 = 29;
	public static final int VALUE_TYPE_UTF8S = 30;
	public static final int VALUE_TYPE_UTF16S = 31;	
	
	private HashMap<String, BleClientService> services = new HashMap<String, BleClientService>();
	
	protected String value;
	protected int valueType;
	protected String serviceId;
	protected String characteristicId;
	protected String descriptorId;
	
	protected int procType;
	
	public BaseClientProfile(Context context, BleGattID uid) {
		super(context, uid);
		
		value = "";
		valueType = 0;
		serviceId = "";
		characteristicId = "";
		descriptorId = "";
		
		
	}
	
	protected void initialize() {
		ArrayList<BleClientService> services = new ArrayList<BleClientService>();

		Collection<BleClientService> co = this.services.values();
		for (Iterator<BleClientService> i = co.iterator(); i.hasNext();) {
			services.add((BleClientService)i.next());
		}
		
		init(services, null);
	}
	
	protected void addService(BleClientService service) {
		this.services.put(service.getServiceId().toString(), service);
	}
	
	protected BleClientService getService(String serviceId) {
		return this.services.get(serviceId);
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
       
        BleClientService service = getService(this.serviceId);
        if(service == null) {
        	// error
        	return;
        }
        
        BleCharacteristic characteristic = service.getCharacteristic(device, new BleGattID(this.characteristicId));
        if(this.procType == PROCESS_TYPE_WRITE) {
        	characteristic.setValue(changeValue2writeValue());
        	service.writeCharacteristic(device, 0, characteristic);
        } else {
        	service.readCharacteristic(device, characteristic);
//        	byte[] bytValue = characteristic.getValue();
        }
        
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
    
    public void onCharacteristicChanged(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic) {
        Log.d(TAG, "onCharacteristicChanged");
           	
    }

    public void onWriteCharacteristicComplete(int status,
            BluetoothDevice remoteDevice,
            BleCharacteristic characteristic) {
        Log.d(TAG, "onWriteCharacteristicComplete");
    	
    }
 
    public void onReadCharacteristicComplete(BluetoothDevice remoteDevice,
            BleCharacteristic characteristic) {
        Log.d(TAG, "onReadCharacteristicComplete");
    	
    }
 
 	public void writeValueForCharacteristic(BluetoothDevice device, String value, int valueType, 
 			String service, String characteristic) 
 	{
		this.value = value;
		this.valueType = valueType;
//		this.serviceId = serviceId;
//		this.characteristicId = characteristicId;
		this.procType = PROCESS_TYPE_WRITE;
		
		Log.d(TAG, "device:" + device.getName());
    	this.connect(device);
    }
	
 	public void readValueForCharacteristic(BluetoothDevice device, String service, 
 			String characteristic, int valueType) 
 	{
 		this.procType = PROCESS_TYPE_READ;
	}
	
 	public void writeValueForDescriptor(BluetoothDevice device, String value, int valueType, 
 			String service, String characteristic, String descriptor) 
 	{
		this.procType = PROCESS_TYPE_WRITE;
	}
	
 	public void readValueForDescriptor(BluetoothDevice device, String service, String characteristic, 
 			String descriptor, int valueType) 
 	{
 		this.procType = PROCESS_TYPE_READ;		
	} 
    
	private byte[] changeValue2writeValue() {		
		byte[] value = null;
		
		switch(this.valueType) 
		{
		case VALUE_TYPE_UUID:
			break;
		case VALUE_TYPE_BOOLEAN:
			{
				byte bytValue = Byte.valueOf(this.value);
				value = new byte[1];
				value[0] = bytValue;
			}
			break;
		case VALUE_TYPE_2BIT:
			break;
		case VALUE_TYPE_NIBBLE:
			break;
		case VALUE_TYPE_8BIT:
			break;
		case VALUE_TYPE_16BIT:
			break;
		case VALUE_TYPE_24BIT:
			break;
		case VALUE_TYPE_32BIT:
			break;
		case VALUE_TYPE_UINT8:
			{
				Integer ivalue = Integer.valueOf(this.value);
				int i = ivalue.intValue();
				value = new byte[4];
				value[0] = (byte)((i >>> 24 ) & 0xFF);
				value[1] = (byte)((i >>> 16 ) & 0xFF);
				value[2] = (byte)((i >>>  8 ) & 0xFF);
				value[3] = (byte)((i >>>  0 ) & 0xFF);			
			}
			break;
		case VALUE_TYPE_UINT12:
			break;
		case VALUE_TYPE_UINT16:
			break;
		case VALUE_TYPE_UINT24:
			break;
		case VALUE_TYPE_UINT32:
			break;
		case VALUE_TYPE_UINT40:
			break;
		case VALUE_TYPE_UINT48:
			break;
		case VALUE_TYPE_UINT64:
			break;
		case VALUE_TYPE_UINT128:
			break;
		case VALUE_TYPE_SINT8:
			break;
		case VALUE_TYPE_SINT12:
			break;
		case VALUE_TYPE_SINT16:
			break;
		case VALUE_TYPE_SINT24:
			break;
		case VALUE_TYPE_SINT32:
			break;
		case VALUE_TYPE_SINT48:
			break;
		case VALUE_TYPE_SINT64:
			break;
		case VALUE_TYPE_SINT128:
			break;
		case VALUE_TYPE_FLOAT32:
			break;
		case VALUE_TYPE_FLOAT64:
			break;
		case VALUE_TYPE_SFLOAT:
			break;
		case VALUE_TYPE_FLOAT:
			break;
		case VALUE_TYPE_DUNIT16:
			break;
		case VALUE_TYPE_UTF8S:
			break;
		case VALUE_TYPE_UTF16S:
			break;
		}
		
		return value;
	}
}
