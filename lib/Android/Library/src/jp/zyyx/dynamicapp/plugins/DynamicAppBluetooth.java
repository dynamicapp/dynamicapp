package jp.zyyx.dynamicapp.plugins;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import jp.zyyx.dynamicapp.bluetoothComponents.BluetoothConnectionService;
import jp.zyyx.dynamicapp.bluetoothComponents.PeerData;
import jp.zyyx.dynamicapp.core.Plugin;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;
import jp.zyyx.dynamicapp.wrappers.JSONObjectWrapper;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;

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
public class DynamicAppBluetooth extends Plugin {
	private static final String TAG = "DynamicAppBluetooth";

	private static final String METHOD_DISCOVER = "discover";
	private static final String METHOD_CONNECT = "connect";
	private static final String METHOD_DISCONNECT = "disconnect";
	private static final String METHOD_SEND = "send";
	private static final String METHOD_DISCOVERABLE = "ensureDiscoverable";
	
	private static final int STATE_UNKNOWN = -1;
	private static final int STATE_AVAILABLE = 0;
	private static final int STATE_UNAVAILABLE = 1;
	private static final int STATE_CONNECTED = 2;
	private static final int STATE_DISCONNECTED = 3;
	private static final int STATE_CONNECTING = 4;
	
	public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_CONNECTED = 4;
    public static final int MESSAGE_FAILED_CONNECTION = 5;
    public static final int MESSAGE_DISCONNECTION = 6;
    
    public static final String DEVICE_NAME = "device_name";
    public static final String DEVICE_ADD = "device_add";
    public static final String DEVICE_STATE = "device_state";
    public static final String TOAST = "toast";
    public static final String SEND_RESULT = "sendResult";
    public static final String READ_RESULT = "readResult";
    public static final String RECEIVED_MESSAGE = "message";

	private static DynamicAppBluetooth instance = null;
	private static String mConnectedDeviceName = null;
	private static String mConnectedDeviceAdd = null;
	private static int mConnectedDeviceState = -1;
	private static boolean result = true;
	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothConnectionService mDataCommService = null;
	private PeerData currentPeerData = null;
	private PeerData selfPeerData = null;
	private HashMap<String, Boolean> connectedDeviceList = null;
	private HashMap<String, Boolean> discoveredDeviceList = null;
	private HashMap<String, PeerData> deviceList = null;
	private boolean isUnregistered = false;
	
	private DynamicAppBluetooth() {
		super();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		connectedDeviceList = new HashMap<String, Boolean>();
		discoveredDeviceList = new HashMap<String, Boolean>();
		deviceList = new HashMap<String, PeerData>();
		if(mBluetoothAdapter != null)
			selfPeerData = new PeerData(mBluetoothAdapter.getName(),mBluetoothAdapter.getAddress());
		else
			selfPeerData = new PeerData("unknown", "unknown");
	}
	
	public static synchronized DynamicAppBluetooth getInstance() {
		if (instance == null) {
			instance = new DynamicAppBluetooth();
		}

		return instance;
	}
	
	@Override
	public void execute() {
		DebugLog.w(TAG, "method " + methodName + " is called.");
		DebugLog.w(TAG, "parameters are: " + params);
		mainActivity.callJsEvent(PROCESSING_FALSE);
		
		if (methodName.equalsIgnoreCase(METHOD_DISCOVER)) {
			mBluetoothAdapter.cancelDiscovery();
			this.doDiscovery();
		} else if (methodName.equalsIgnoreCase(METHOD_CONNECT)) {
			String peerDataStr = param.get("peerData", "");
			JSONObjectWrapper jsonPeerData = null;

			try {
				jsonPeerData = new JSONObjectWrapper(peerDataStr);
			} catch (JSONException e) {
				DynamicAppBluetooth.onError(STATE_UNAVAILABLE+"", callbackId);
				if(Utilities.isDebuggable)
					e.printStackTrace();
			}
			
			this.connectDevice(jsonPeerData);
		} else if (methodName.equalsIgnoreCase(METHOD_DISCONNECT)) {
			String peerDataStr = param.get("peerData", "");
			JSONObjectWrapper jsonPeerData = null;

			try {
				jsonPeerData = new JSONObjectWrapper(peerDataStr);
			} catch (JSONException e) {
				DynamicAppBluetooth.onError(STATE_UNAVAILABLE+"", callbackId);
				if(Utilities.isDebuggable)
					e.printStackTrace();
			}
			this.disconnect(jsonPeerData);
		} else if (methodName.equalsIgnoreCase(METHOD_SEND)) {
			String sendData = param.get("sendData", "sample");
			String peerDataStr = param.get("peerData", "");
			
			JSONObjectWrapper jsonPeerData = null;

			try {
				jsonPeerData = new JSONObjectWrapper(peerDataStr);
			} catch (JSONException e) {
				DynamicAppBluetooth.onError(STATE_UNAVAILABLE+"", callbackId);
				if(Utilities.isDebuggable)
					e.printStackTrace();
			}
			
			this.sendData(jsonPeerData, sendData);
		} else if (methodName.equalsIgnoreCase(METHOD_DISCOVERABLE)) {
			this.makeDeviceAlwaysDiscoverable();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		final int OK = Activity.RESULT_OK;
		if (requestCode == ACTIVITY_REQUEST_CD_PAIR_DEVICE) {
			DebugLog.w(TAG, "intent: "+ intent);
		} else if(requestCode == ACTIVITY_REQUEST_CD_ENABLE_BT) {
			if(resultCode == OK) {
				this.doDiscovery();
			}
		}
	}

	@Override
	public void onBackKeyDown() {
		this.release();
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
		this.release();
	}
	
	@Override
	public void onResume() {
		if (mDataCommService != null) {
            if (mDataCommService.getState() == BluetoothConnectionService.STATE_NONE) {
            	mDataCommService.start();
            }
        }
	}
	
	private void release() {
		DebugLog.w(TAG, "release is executed.");
		if (mDataCommService != null) 
			mDataCommService.stop();
		connectedDeviceList.clear();
		discoveredDeviceList.clear();
		instance = null;
	}
	
	public void setupServer() {
        DebugLog.w(TAG, "Initialize the BluetoothDataCommService");
        
        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
	        if (mDataCommService == null)
	        	 mDataCommService = new BluetoothConnectionService(mainActivity, mHandler);
	        
	        if (mDataCommService != null) {
	            if (mDataCommService.getState() == BluetoothConnectionService.STATE_NONE) {
	            	mDataCommService.start();
	            	selfPeerData.setState(STATE_UNKNOWN);
	            }
	        }
        }
    }
	
	public void registerReceiver() {
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
		filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
		filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
		filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);

		Utilities.dynamicAppActivityRef.registerReceiver(mReceiver, filter);
		isUnregistered = true;
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

	private void sendData(JSONObjectWrapper deviceData, String message) {
		String address = deviceData.get("id", "");
		//String deviceName = deviceData.get("deviceName", "unknown");
		
        /*if (!isConnectedTo(address)) {
        	DebugLog.e(TAG, "device is not connected to " + deviceName);
            DynamicAppBluetooth.onError(1+"", callbackId);
        } else {*/
        	if (message.length() > 0) {
            	DebugLog.w(TAG, "try send:" + message);
            	if (mDataCommService != null) {
            		mDataCommService.sendMessage(address, message);
    	        } else {
    	        	DebugLog.e(TAG, "mDataCommService not initialialized or started.");
    				DynamicAppBluetooth.onError(1+"", callbackId);
    	        }
            } else {
            	DebugLog.e(TAG, "data to send is empty");
            	DynamicAppBluetooth.onError(1+"", callbackId);
            }
        //}
    }
	
	private boolean isConnectedTo(String address) {
		return connectedDeviceList.get(address) != null;
	}

	private void doDiscovery() {
		if (mBluetoothAdapter == null) {
			DebugLog.w(TAG, "Bluetooth is unsupported.");
		} else {
			makeDeviceAlwaysDiscoverable();
			if (mBluetoothAdapter.isEnabled()) {
				DebugLog.w(TAG, "Bluetooth is enabled");
				mBluetoothAdapter.startDiscovery();
			} else {
				this.enableBluetoothByIntent();
			}
		}
	}
	
	private void connectDevice(JSONObjectWrapper data) {
		mBluetoothAdapter.cancelDiscovery();
		String deviceName = data.get("deviceName", "");
		String address = data.get("id", "");
		currentPeerData =  new PeerData(deviceName, address);
		
		if(isValid(address)) {
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
			if(!isConnectedTo(address)) {
//				if(isPairedDevice(address)) {
//					unpairDevice(device);
//				}
				
				String clientDevice = mBluetoothAdapter.getName();
				DebugLog.w(TAG, "Client Device Name:"+ clientDevice);
				
				if (mDataCommService != null) {
					mDataCommService.connect(device);
		        } else {
		        	DebugLog.e(TAG, "mDataCommService not initialialized or started.");
					DynamicAppBluetooth.onError(1+"", callbackId);
		        }
			} else {
				//Toast.makeText(dynamicApp, "is already connected to "
                //        + device.getName(), Toast.LENGTH_SHORT).show();
			}
		} else {
			DebugLog.e(TAG, "invalid device address");
			DynamicAppBluetooth.onError(1+"", callbackId);
		}
	}
	
//	private boolean isPairedDevice(String address) {
//		boolean isPaired = false;
//		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
//				.getBondedDevices();
//		
//		if (pairedDevices.size() > 0) {
//			
//			for (BluetoothDevice device : pairedDevices) {
//				if(device.getAddress().equalsIgnoreCase(address)) {
//					isPaired = true;
//					break;
//				}
//			}
//		}
//		return isPaired;
//	}
	
//	private void unpairDevice(BluetoothDevice device) {
//		try {
//		    Method m = device.getClass()
//		        .getMethod("removeBond", (Class[]) null);
//		    m.invoke(device, (Object[]) null);
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
//	}
	
	private boolean isValid(String address) {
		return BluetoothAdapter.checkBluetoothAddress(address);
	}

	private void disconnect(JSONObjectWrapper data) {
		if (mDataCommService != null) {
			String address = data.get("id", "");
			BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
			if(isConnectedTo(address)) {
	            mDataCommService.disconnect(device);
			} else {
				//Toast.makeText(dynamicApp, "is already disconnected to "
                //        + device.getName(), Toast.LENGTH_SHORT).show();
			}
        } else {
        	DebugLog.e(TAG, "mDataCommService not initialialized or started.");
			DynamicAppBluetooth.onError(1+"", callbackId);
        }
	}

	private void enableBluetoothByIntent() {
		Intent enableBtIntent = new Intent(
				BluetoothAdapter.ACTION_REQUEST_ENABLE);
		mainActivity.startActivityForResult(enableBtIntent,
				ACTIVITY_REQUEST_CD_ENABLE_BT);
	}
	
	private void makeDeviceAlwaysDiscoverable() {
    	Intent discoverableIntent = new
		Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
		discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600);
		mainActivity.startActivity(discoverableIntent);
    }

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
			String action = intent.getAction();
			
			if (methodName != null && BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceName = (device.getName() != null) ? device
						.getName() : "unknown";
				String address = device.getAddress();
				
				String btClassStr = device.getBluetoothClass().toString(); // 5a020c - Android Constant
				int deviceMajorClassNo = device.getBluetoothClass().getMajorDeviceClass();
				
				if((deviceMajorClassNo == BluetoothClass.Device.Major.PHONE) && btClassStr.equalsIgnoreCase("5a020c")) {
					if(discoveredDeviceList.get(address) == null) {
						discoveredDeviceList.put(address, true);
						DebugLog.w(TAG, deviceName + " is found.");
						PeerData foundPeer = new PeerData(deviceName, address);
						foundPeer.setState(STATE_AVAILABLE);
						deviceList.put(address, foundPeer);
					}
				}
			} else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
				//Toast.makeText(dynamicApp, "discovery started...",
                //        Toast.LENGTH_SHORT).show();
				DebugLog.w(TAG, "discovery started...");
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				discoveredDeviceList.clear();
				discoveredDeviceList = new HashMap<String, Boolean>();
				
				//Toast.makeText(dynamicApp, "discovery finished...",
                //        Toast.LENGTH_SHORT).show();
				DebugLog.w(TAG, "discovery finished...");
			} else if (BluetoothDevice.ACTION_ACL_CONNECTED
					.equals(action)) {
				DebugLog.w(TAG, "device is about to connect...");
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				String deviceName = (device.getName() != null) ? device
						.getName() : "unknown";
				String address = device.getAddress();
				
				PeerData foundPeer = new PeerData(deviceName, address);
				foundPeer.setState(STATE_CONNECTING);
				deviceList.put(address, foundPeer);
				
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED
					.equals(action)) {
				DebugLog.w(TAG, "device is about to disconnect...");
			} else if (BluetoothDevice.ACTION_ACL_DISCONNECTED
					.equals(action)) {
				DebugLog.w(TAG, "device has disconnected...");
			} else if (ACTION_PAIRING_REQUEST.equals(action)) {
				DebugLog.w(TAG, "device pairing is done...");
			}
		}
	};

	private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_STATE_CHANGE:
                DebugLog.w(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                switch (msg.arg1) {
                case BluetoothConnectionService.STATE_CONNECTED:
                    DebugLog.w(TAG, "connected to " + mConnectedDeviceName);
                    PeerData newPeer = new PeerData(mConnectedDeviceName, mConnectedDeviceAdd);
                	newPeer.setState(STATE_CONNECTED);
                	DynamicAppBluetooth.onSuccess(new JSONObject(), callbackId, false);
                    break;
                case BluetoothConnectionService.STATE_CONNECTING:
                	DebugLog.w(TAG, "connecting state...");
                	if(currentPeerData != null)
                		currentPeerData.setState(STATE_CONNECTING);
                	//selfPeerData.setState(STATE_CONNECTING);
                    break;
                case BluetoothConnectionService.STATE_LISTEN:
                case BluetoothConnectionService.STATE_NONE:
                	DebugLog.w(TAG, "initial state");
                    break;
                }
                break;
            case MESSAGE_WRITE:
            	result = msg.getData().getBoolean(SEND_RESULT);
            	if(result)
            		DynamicAppBluetooth.onSuccess(new JSONObject(), callbackId, false);
            	else
            		DynamicAppBluetooth.onError("1", callbackId);
                break;
            case MESSAGE_READ:
                String readMessage = msg.getData().getString(RECEIVED_MESSAGE);
                String escapedMessage = readMessage.replace("\n", "\\n");

                JSONObject senderData = new JSONObject();
                
                try {
                	senderData.put("deviceName", msg.getData().getString(DEVICE_NAME));
                	senderData.put("id", msg.getData().getString(DEVICE_ADD));
                	senderData.put("state", STATE_CONNECTED);
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					String script = "Bluetooth.onRecvCallback(" + senderData + ",\"" + escapedMessage + "\");";
					DebugLog.w(TAG, "script: " + script);
	        		mainActivity.callJsEvent(script);
				}
                
                break;
            case MESSAGE_DEVICE_CONNECTED:
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                mConnectedDeviceAdd = msg.getData().getString(DEVICE_ADD);
                
                connectedDeviceList.put(mConnectedDeviceAdd, true);
                
                PeerData peer = new PeerData(mConnectedDeviceName, mConnectedDeviceAdd);
            	peer.setState(STATE_CONNECTED);
            	
                //Toast.makeText(dynamicApp, "Connected to "
                //               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                
                PeerData foundPeer = new PeerData(mConnectedDeviceName, mConnectedDeviceAdd);
				foundPeer.setState(STATE_CONNECTED);
				deviceList.put(mConnectedDeviceAdd, foundPeer);
				
                break;
            case MESSAGE_FAILED_CONNECTION:
                //Toast.makeText(dynamicApp, msg.getData().getString(TOAST),
                //               Toast.LENGTH_SHORT).show();
                mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                mConnectedDeviceAdd = msg.getData().getString(DEVICE_ADD);
                mConnectedDeviceState = msg.getData().getInt(DEVICE_STATE);
                
                if(methodName.equalsIgnoreCase(METHOD_CONNECT)) {
                	foundPeer = new PeerData(mConnectedDeviceName, mConnectedDeviceAdd);
    				foundPeer.setState(STATE_UNAVAILABLE);
                	DynamicAppBluetooth.onError(mConnectedDeviceState + "", callbackId);
                }
                mDataCommService.start();
                break;
            case MESSAGE_DISCONNECTION:
            	//Toast.makeText(dynamicApp, msg.getData().getString(TOAST),
                //       Toast.LENGTH_SHORT).show();
            	mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
            	mConnectedDeviceAdd = msg.getData().getString(DEVICE_ADD);
            	mConnectedDeviceState = msg.getData().getInt(DEVICE_STATE);
            	
            	/*if(deviceList.get(mConnectedDeviceAdd) != null) {
            		PeerData deviceData = deviceList.get(mConnectedDeviceAdd);
            		
            		if(deviceData.getState() == STATE_CONNECTING) {
            			//deviceData.setState(STATE_UNAVAILABLE);
            			foundPeer = new PeerData(mConnectedDeviceName, mConnectedDeviceAdd);
        				foundPeer.setState(STATE_UNAVAILABLE);
            		} else if(deviceData.getState() == STATE_CONNECTED) {
            			//deviceData.setState(STATE_DISCONNECTED);
            			
            		}
            	}*/
            	
            	foundPeer = new PeerData(mConnectedDeviceName, mConnectedDeviceAdd);
				foundPeer.setState(STATE_DISCONNECTED);
				
				connectedDeviceList.remove(mConnectedDeviceAdd);
				deviceList.remove(mConnectedDeviceAdd);
				
            	if(methodName != null && methodName.equalsIgnoreCase(METHOD_DISCONNECT))
            		DynamicAppBluetooth.onSuccess(new JSONObject(), callbackId, false);
            	mDataCommService.start();
            	break;
            }
            
        }
    };
}
