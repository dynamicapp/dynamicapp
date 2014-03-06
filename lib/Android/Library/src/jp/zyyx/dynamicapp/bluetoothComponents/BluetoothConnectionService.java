package jp.zyyx.dynamicapp.bluetoothComponents;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import jp.zyyx.dynamicapp.plugins.DynamicAppBluetooth;
import jp.zyyx.dynamicapp.utilities.DebugLog;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
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
public class BluetoothConnectionService {
	private static final String TAG = "BluetoothConnectionService";

    private static final String NAME_SECURE = "BluetoothDataCommSecure";
    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private ConnectThread mConnectThread;
    private ArrayList<ConnectedThread> connectedThreads;
    private ArrayList<String> uuidList;
    private ArrayList<String> btDeviceAddresses;
    public static HashMap<String, Boolean> availableUuids;
    private HashMap<String, BluetoothSocket> btSockets;
    private HashMap<BluetoothDevice, String> deviceUuidList;
    private int mState;

    public static final int STATE_NONE = 0;   
    public static final int STATE_LISTEN = 1;   
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;
    public static final int STATE_FULL = 4;
    private static final int MAX_CONNECTIONS = 7;
    private int connections = 0;

    public BluetoothConnectionService(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        connectedThreads = new ArrayList<ConnectedThread>();
        btDeviceAddresses = new ArrayList<String>();
        btSockets = new HashMap<String, BluetoothSocket>();
        deviceUuidList = new HashMap<BluetoothDevice, String>();
        this.generateUUIDs();
    }
    
    private void generateUUIDs() {
    	uuidList = new ArrayList<String>();
    	availableUuids = new HashMap<String, Boolean>();
    	
    	uuidList.add("a60f35f0-b93a-11de-8a39-08002009c666");
    	uuidList.add("503c7430-bc23-11de-8a39-0800200c9a66");
    	uuidList.add("503c7431-bc23-11de-8a39-0800200c9a66");
    	uuidList.add("503c7432-bc23-11de-8a39-0800200c9a66");
    	uuidList.add("503c7433-bc23-11de-8a39-0800200c9a66");
    	uuidList.add("503c7434-bc23-11de-8a39-0800200c9a66");
    	uuidList.add("503c7435-bc23-11de-8a39-0800200c9a66");
    	
    	int size = uuidList.size();
    	for(int i = 0; i < size; i++) {
    		availableUuids.put(uuidList.get(i), true);
    	}
	}

    private synchronized void setState(int state) {
        DebugLog.w(TAG, "setState() " + mState + " -> " + state);
        mState = state;

        mHandler.obtainMessage(DynamicAppBluetooth.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
    	DebugLog.w(TAG, "start");

        setState(STATE_LISTEN);

        if (mSecureAcceptThread == null) {
        	DebugLog.w(TAG, "starting accept thread...");
            mSecureAcceptThread = new AcceptThread();
            mSecureAcceptThread.start();
        }
        
    }

    public synchronized void connect(BluetoothDevice device) {
    	DebugLog.w(TAG, "connect to: " + device);

        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
            device) {
    	String address = device.getAddress();
    	btDeviceAddresses.add(address);
        // Start the thread to manage the connection and perform transmissions
    	ConnectedThread mConnectedThread = new ConnectedThread(device, socket);
        mConnectedThread.start();
        
        connectedThreads.add(mConnectedThread);
        // Send the name of the connected device back to the UI Activity
        Message msg = mHandler.obtainMessage(DynamicAppBluetooth.MESSAGE_DEVICE_CONNECTED);
        Bundle bundle = new Bundle();
        bundle.putString(DynamicAppBluetooth.DEVICE_NAME, device.getName());
        bundle.putString(DynamicAppBluetooth.DEVICE_ADD, address);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
    	DebugLog.w(TAG, "stop");

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread = null;
        }
        
        this.disconnectAll();
        
        setState(STATE_NONE);
    }
    
    public void disconnectAll(){
        try {
            for (int i = 0; i < btDeviceAddresses.size(); i++) {
                BluetoothSocket socket = btSockets.get(btDeviceAddresses.get(i));
                if(socket != null)
                	socket.close();
            }
            btSockets = new HashMap<String, BluetoothSocket>();
            connectedThreads = new ArrayList<ConnectedThread>();
            btDeviceAddresses = new ArrayList<String>();
            
        } catch (IOException e) {
            DebugLog.e(TAG, "IOException in shutdown");
        }
    }
    
    public synchronized void disconnect(BluetoothDevice device) {
    	String addressToStop = device.getAddress();
    	BluetoothSocket socket = btSockets.get(addressToStop);
        if(socket != null) {
        	try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    	btSockets.remove(addressToStop);
    	btDeviceAddresses.remove(addressToStop);
		
    	DebugLog.w(TAG, "stop");
    	
    	int size = connectedThreads.size();
    	
        for(int i=0; i < size; i++) {
        	ConnectedThread conThread = connectedThreads.get(i);
        	
        	if(conThread != null) {
	        	String address = conThread.getMmDevice().getAddress();
	        	if(addressToStop.equals(address)){
	        		conThread = null;
	        		connectedThreads.remove(i);
	        		break;
	        	}
        	}
        }
        
        this.start();
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(BluetoothDevice device, byte[] out) {
        // Create temporary object
        ConnectedThread r = null;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            String addressToStop = device.getAddress();
        	int size = connectedThreads.size();
        	
            for(int i=0; i < size; i++) {
            	ConnectedThread conThread = connectedThreads.get(i);
            	
            	if(conThread != null) {
    	        	String address = conThread.getMmDevice().getAddress();
    	        	if(addressToStop.equals(address)) {
    	        		r = conThread;
    	        		break;
    	        	}
            	}
            }
        }
        // Perform the write unsynchronized
        if(r != null) {
        	r.write(out);
        } else {
        	DebugLog.w(TAG, "write thread not connected");
        }
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed(BluetoothDevice device) {
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(DynamicAppBluetooth.MESSAGE_FAILED_CONNECTION);
        Bundle bundle = new Bundle();
        bundle.putString(DynamicAppBluetooth.TOAST, "Unable to connect device");
        bundle.putString(DynamicAppBluetooth.DEVICE_NAME, device.getName());
        bundle.putString(DynamicAppBluetooth.DEVICE_ADD, device.getAddress());
        bundle.putInt(DynamicAppBluetooth.DEVICE_STATE, 3);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothConnectionService.this.start();
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     */
    private void connectionLost(BluetoothDevice device) {
    	String uid = deviceUuidList.get(device);
    	if(uid != null) {
	    	if(availableUuids.get(uid) != null && !availableUuids.get(uid))
	    		connections--;
			availableUuids.put(uid, true);
    	}
        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(DynamicAppBluetooth.MESSAGE_DISCONNECTION);
        Bundle bundle = new Bundle();
        bundle.putString(DynamicAppBluetooth.TOAST, "connection to " + device.getName() + " was lost.");
        bundle.putString(DynamicAppBluetooth.DEVICE_NAME, device.getName());
        bundle.putString(DynamicAppBluetooth.DEVICE_ADD, device.getAddress());
        bundle.putInt(DynamicAppBluetooth.DEVICE_STATE, 3);
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        // Start the service over to restart listening mode
        BluetoothConnectionService.this.start();
    }

    private class AcceptThread extends Thread {
		public AcceptThread() {}

		public void run() {
			mAdapter.cancelDiscovery();
			try {
				DebugLog.w(TAG, "start of AcceptThread:");
				for (int i = 0; (i < MAX_CONNECTIONS) && (connections < MAX_CONNECTIONS);) {
					
					String uid = uuidList.get(i);
					if(availableUuids.get(uid)) {
						BluetoothServerSocket myServerSocket = mAdapter
								.listenUsingRfcommWithServiceRecord(NAME_SECURE,
										UUID.fromString(uid));
						
						BluetoothSocket socket = myServerSocket.accept();
							
						if (socket != null) {
							synchronized (BluetoothConnectionService.this) {
								DebugLog.w(TAG, "AcceptThread:" + i);
								String address = socket.getRemoteDevice().getAddress();
				                btSockets.put(address, socket);
				                connections++;
				        		availableUuids.put(uid, false);
				        		deviceUuidList.put(socket.getRemoteDevice(), uid);
								connected(socket, socket.getRemoteDevice());
								try {
									myServerSocket.close();
	                            } catch (IOException e) {
	                            	DebugLog.e(TAG, "Could not close unwanted socket");
	                            }
		                    }
						}
					} else {
						DebugLog.e(TAG, "try AcceptThread:" + i);
					}
					
					i++;
					
					if((i == MAX_CONNECTIONS) && (connections < MAX_CONNECTIONS)) {
						i = 0;
					}
				}
				DebugLog.w(TAG, "end of AcceptThread:");
				connectionFullFailure();

			} catch (IOException e) {

			}
		}
	}
   
    private BluetoothSocket getConnectedSocket(BluetoothDevice myBtServer, UUID uuidToTry) {
        BluetoothSocket myBSock;
        try {
            myBSock = myBtServer.createRfcommSocketToServiceRecord(uuidToTry);
            myBSock.connect();
            return myBSock;
        } catch (IOException e) {
            DebugLog.e(TAG, "IOException in getConnectedSocket");
        }
        return null;
    }
    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
        }

        public void run() {
        	mAdapter.cancelDiscovery();
        	BluetoothDevice myBtServer = mmDevice;
            BluetoothSocket myBSock = null;

            for (int i = 0; i < MAX_CONNECTIONS && myBSock == null; i++) {
                for (int j = 0; j < 3 && myBSock == null; j++) {
	            	String uid = uuidList.get(i);
	                myBSock = getConnectedSocket(myBtServer, UUID.fromString(uid));
	                if (myBSock == null) {
	                    try {
	                        Thread.sleep(200);
	                    } catch (InterruptedException e) {
	                    	connectionFailed(mmDevice);
	                    }
	                }
                }
            }
            
            if (myBSock == null) {
            	connectionFailed(mmDevice);
            	return;
            }

            synchronized (BluetoothConnectionService.this) {
                mConnectThread = null;
            }
            
            // Start the connected thread
            btSockets.put(mmDevice.getAddress(), myBSock);
            connected(myBSock, mmDevice);;
        }

        public void cancel() {
            /*try {
                mmSocket.close();
            } catch (IOException e) {
            	DebugLog.e(TAG, "close() of connect socket failed");
            }*/
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private BluetoothDevice mmDevice;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ConnectedThread(BluetoothDevice device, BluetoothSocket socket) {
        	//BluetoothSocket bSock = btSockets.get(socke);
        	mmDevice = device;
        	DebugLog.w(TAG, "create ConnectedThread: ");
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            	DebugLog.e(TAG, "temp sockets not created");
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
        	DebugLog.w(TAG, "BEGIN mConnectedThread");
        	final int MAX_SIZE = 5242880; 
            byte[] buffer = new byte[MAX_SIZE];
            int bytesRead = -1;
            String message = "";
            // Keep listening to the InputStream while connected
            while (true) {
            	try {
            		message = "";
	                bytesRead = mmInStream.read(buffer);
	                if (bytesRead != -1) {
	                	while ((bytesRead >= 0) && (buffer[bytesRead - 1] != 0)) {
                            message = message + new String(buffer, 0, bytesRead);
                            bytesRead = mmInStream.read(buffer);
                        }
	                	
	                	if(buffer[bytesRead - 1] == 0) {
	                        message = message + new String(buffer, 0, bytesRead - 1); // Remove stop marker
	
		                    Message msg = mHandler.obtainMessage(DynamicAppBluetooth.MESSAGE_READ);
		                    Bundle bundle = new Bundle();
		                    bundle.putBoolean(DynamicAppBluetooth.READ_RESULT, true);
		                    bundle.putString(DynamicAppBluetooth.RECEIVED_MESSAGE, message);
		                    bundle.putString(DynamicAppBluetooth.DEVICE_NAME, mmDevice.getName());
		                    bundle.putString(DynamicAppBluetooth.DEVICE_ADD, mmDevice.getAddress());
		                    msg.setData(bundle);
		                    mHandler.sendMessage(msg);
	                	}
	                } else {
	                	DebugLog.e(TAG, "end of read");
	                }
            	} catch (IOException e) {
                	DebugLog.e(TAG, "disconnected");
                    connectionLost(mmDevice);
                    // Start the service over to restart listening mode
                    BluetoothConnectionService.this.start();
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);

                // Share the sent message back to the UI Activity
                mHandler.obtainMessage(DynamicAppBluetooth.MESSAGE_WRITE, -1, -1, buffer)
                        .sendToTarget();
            } catch (IOException e) {
            	DebugLog.e(TAG, "Exception during write");
            }
        }

		/**
		 * @return the mmDevice
		 */
		public BluetoothDevice getMmDevice() {
			return mmDevice;
		}
    }

	/**
	 * 
	 */
	public void connectionFullFailure() {
		//this.setState(STATE_FULL);
		DebugLog.w(TAG, "Sockets are full.");
	}
	
	public void sendMessage(String destination, String message){
        try {
            BluetoothSocket myBsock = btSockets.get(destination);
            if (myBsock != null) {
                OutputStream outStream = myBsock.getOutputStream();
                byte[] stringAsBytes = (message + " ").getBytes();
                stringAsBytes[stringAsBytes.length - 1] = 0; // Add a stop marker
                outStream.write(stringAsBytes);
                DebugLog.w(TAG, "successful sendMessage - Dest:" + destination + ", Msg:" + message);
                Message msg = mHandler.obtainMessage(DynamicAppBluetooth.MESSAGE_WRITE);
                Bundle bundle = new Bundle();
                bundle.putBoolean(DynamicAppBluetooth.SEND_RESULT, true);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } else {
            	Message msg = mHandler.obtainMessage(DynamicAppBluetooth.MESSAGE_WRITE);
                Bundle bundle = new Bundle();
                bundle.putBoolean(DynamicAppBluetooth.SEND_RESULT, false);
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            }
        } catch (IOException e) {
            DebugLog.e(TAG, "IOException in sendMessage - Dest:" + destination + ", Msg:" + message);
            Message msg = mHandler.obtainMessage(DynamicAppBluetooth.MESSAGE_WRITE);
            Bundle bundle = new Bundle();
            bundle.putBoolean(DynamicAppBluetooth.SEND_RESULT, false);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
        
        return;
    }
}
