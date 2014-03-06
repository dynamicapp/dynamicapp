package jp.zyyx.dynamicapp.plugins;

import java.nio.ByteBuffer;
import java.util.Arrays;

import jp.zyyx.dynamicapp.core.Plugin;

import org.json.JSONArray;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcF;
import android.os.Build;

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
@SuppressLint("NewApi")
public class Felica extends Plugin {
	private static Felica instance = null;
	private NfcF felicaTag = null;
	private Intent nfcIntent = null;
	
	private IdM idM = null;
	
    // request system code
    public static final byte COMMAND_REQUEST_SYSTEMCODE = 0x0c;
    public static final byte RESPONSE_REQUEST_SYSTEMCODE = 0x0d;
    
    // search service code
    public static final byte COMMAND_SEARCH_SERVICECODE = 0x0a;
    public static final byte RESPONSE_SEARCH_SERVICECODE = 0x0b;
    
    // request response
    public static final byte COMMAND_REQUEST_RESPONSE = 0x04;
    public static final byte RESPONSE_REQUEST_RESPONSE = 0x05;

    // read without encryption
    public static final byte COMMAND_READ_WO_ENCRYPTION = 0x06;
    public static final byte RESPONSE_READ_WO_ENCRYPTION = 0x07;
    
    // write without encryption
    public static final byte COMMAND_WRITE_WO_ENCRYPTION = 0x08;
    public static final byte RESPONSE_WRITE_WO_ENCRYPTION = 0x09;
	
	private Felica() {
		super();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (Integer.valueOf(Build.VERSION.SDK_INT) < 10 || NfcAdapter.getDefaultAdapter(mainActivity) == null || !NfcAdapter.getDefaultAdapter(mainActivity).isEnabled()) {
			return;
		}

		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(mainActivity.getApplicationContext());
		try {
			nfcAdapter.disableForegroundDispatch(mainActivity);
		} catch(Exception e) { }
	}

	public static synchronized Felica getInstance() {
        if (instance == null) {
            instance = new Felica();
	    }
	    return instance;
	}
	
	public void registerReceiver(Context context) {		
		if (Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 10 || NfcAdapter.getDefaultAdapter(context) == null || !NfcAdapter.getDefaultAdapter(context).isEnabled()) {
			return;
		}

		BroadcastReceiver nfcTagListener = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();

				if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) { }
			}
		};
		IntentFilter filter = new IntentFilter("android.nfc.action.TECH_DISCOVERED"); 
		context.registerReceiver(nfcTagListener, filter); 
		
		IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
	    try {
	        tech.addDataType("*/*");
	    } catch (MalformedMimeTypeException e) {
	        throw new RuntimeException("fail", e);
	    }
		IntentFilter[] intentFiltersArray = new IntentFilter[] { tech };
		String[][] techList = new String[][] { new String[] { NfcF.class.getName() } };

		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context,
                mainActivity.getClass()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		
		NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
		try {
			nfcAdapter.enableForegroundDispatch(mainActivity, pendingIntent, intentFiltersArray, techList);
		} catch(Exception e) { }
	}

	@Override
	public void execute() {
		if(Integer.valueOf(android.os.Build.VERSION.SDK_INT) < 10 || this.felicaTag == null || this.idM == null) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			Felica.onError(callbackId);
			return;
		}
		
		if (methodName.equalsIgnoreCase("getIDm")) {
			this.getIDM();
		} else if (methodName.equalsIgnoreCase("getPMm")) {
			this.getPMm();
		} else if (methodName.equalsIgnoreCase("requestSystemCode")) {
			this.requestSystemCode();
		} else if (methodName.equalsIgnoreCase("requestServiceCode")) {
			this.requestServiceCode();
		} else if (methodName.equalsIgnoreCase("requestResponse")) {
			this.requestResponse();
		} else if (methodName.equalsIgnoreCase("readWithoutEncryption")) {
			this.readWithoutEncryption();
		} else if (methodName.equalsIgnoreCase("writeWithoutEncryption")) {
			this.writeWithoutEncryption();
		} else {
			mainActivity.callJsEvent(PROCESSING_FALSE);
		}
	}
	
	public void initTech(Intent nfcIntent) {
		this.nfcIntent = nfcIntent;
    	Tag tag = (Tag) this.nfcIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    	this.felicaTag = NfcF.get(tag);
    	
    	this.idM = new IdM(nfcIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID));
	}

	private void getIDM() {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		if(this.idM != null) {
			Felica.onSuccess(this.idM.toString(), callbackId, false);
		} else {
			Felica.onError(callbackId);
		}
	}

	private void getPMm() {
		PMm pMm = new PMm(this.felicaTag.getManufacturer());

		mainActivity.callJsEvent(PROCESSING_FALSE);
		if(this.felicaTag.getManufacturer().length > 0) {
			Felica.onSuccess(pMm.toString(), callbackId, false);
		} else {
			Felica.onError(callbackId);
		}
	}

	private void requestSystemCode() {
		JSONArray result = new JSONArray();
		
		try {
			CommandRequest request = new CommandRequest(
					COMMAND_REQUEST_SYSTEMCODE, this.idM);
			CommandResponse response = executeFelica(request);
	        byte[] retBytes = response.getBytes();
	        int num = (int) retBytes[10];
	        
	        for (int i = 0; i < num; i++) {
	        	result.put(new SystemCode(Arrays.copyOfRange(retBytes, 11 + i * 2, 13 + i * 2)).toString());
	        }
		} catch (NfcException e) {
			e.printStackTrace();
		}

		mainActivity.callJsEvent(PROCESSING_FALSE);
		if(result.length() > 0) {
			Felica.onSuccess(result, callbackId, false);
		} else {
			Felica.onError(callbackId);
		}
	}

	private void requestServiceCode() {
		JSONArray result = new JSONArray();

        try {
            int index = 1;
	        while (true) {
	            CommandRequest reqServiceCode = new CommandRequest(
	                    COMMAND_SEARCH_SERVICECODE, this.idM, new byte[] {
	                            (byte) (index & 0xff), (byte) (index >> 8) });
	            CommandResponse r = executeFelica(reqServiceCode);
	            byte[] _bytes = r.getBytes();
	            if (_bytes[1] != (byte) 0x0b) {
	            	throw new NfcException("ResponseCode is not 0x0b");
	            }
	            byte[] bytes = Arrays.copyOfRange(_bytes, 10, _bytes.length);
	            
	            if (bytes.length != 2 && bytes.length != 4)
	                break;
	            if (bytes.length == 2) {
	                if (bytes[0] == (byte) 0xff && bytes[1] == (byte) 0xff)
	                    break;
	                
	                result.put(new ServiceCode(bytes).toString());
	            }
	            index++;
	        }
		} catch (NfcException e) {
			e.printStackTrace();
		}
        
		mainActivity.callJsEvent(PROCESSING_FALSE);
		if(result.length() > 0) {
			Felica.onSuccess(result, callbackId, false);
		} else {
			Felica.onError(callbackId);
		}
	}

	private void requestResponse() {
		String result = null;
		
		try {
			CommandRequest request = new CommandRequest(
					COMMAND_REQUEST_RESPONSE, this.idM);
			CommandResponse data = executeFelica(request);
			byte[] responseResult = Arrays.copyOfRange(data.getBytes(), 1, data.length);
	        result = Util.getHexString(responseResult);
		} catch (NfcException e) {
			e.printStackTrace();
		}

		mainActivity.callJsEvent(PROCESSING_FALSE);
		if(result != null) {
			Felica.onSuccess(result, callbackId, false);
		} else {
			Felica.onError(callbackId);
		}
	}

	private void readWithoutEncryption() {
		ServiceCode serviceCode = new ServiceCode(hexStringToByteArray(param.get("serviceCode", "")));
		int address = Integer.valueOf(param.get("address", "0"));
		String result = null;
		
		try {
			byte[] bytes = serviceCode.getBytes();

			CommandRequest readWoEncrypt = new CommandRequest(
					COMMAND_READ_WO_ENCRYPTION, this.idM, new byte[] {
							(byte) 0x01
							, (byte) bytes[0], (byte) bytes[1], (byte) 0x01
							, (byte) 0x80, (byte) address });

			CommandResponse r = executeFelica(readWoEncrypt);
			
			if (r.data[0] == 0) {
				result = Util.getHexString(Arrays.copyOfRange(r.data, 3,
						r.data.length));
			}
		} catch (NfcException e) {
			e.printStackTrace();
		}
		
		mainActivity.callJsEvent(PROCESSING_FALSE);
		if(result != null) {
			Felica.onSuccess(result, callbackId, false);
		} else {
			Felica.onError(callbackId);
		}
	}

	private void writeWithoutEncryption() {
		ServiceCode serviceCode = new ServiceCode(hexStringToByteArray(param.get("serviceCode", "")));
		int address = Integer.valueOf(param.get("address", "0"));
		byte[] buffer = hexStringToByteArray(param.get("buffer", ""));
		boolean result = false;

		try {
	        if (buffer == null || buffer.length != 16) {
	           throw new NfcException("Buffer data is incorrect");
	        }
	
	        byte[] bytes = serviceCode.getBytes();
	        ByteBuffer b = ByteBuffer.allocate(6 + buffer.length);
	        b.put(new byte[] { (byte) 0x01
	                , (byte) bytes[0]
	                , (byte) bytes[1], (byte) 1
	                , (byte) 0x80, (byte) address
	                });
	        b.put(buffer);
	
	        CommandRequest writeWoEncrypt = new CommandRequest(
	                COMMAND_WRITE_WO_ENCRYPTION, this.idM, b.array());
	        CommandResponse r = executeFelica(writeWoEncrypt);
	        byte[] retBytes = r.getBytes();
	        if (retBytes != null && retBytes.length > 10
	                && retBytes[10] == (byte) 0) {
	        	result = true;
	        }
		} catch (NfcException e) {
			e.printStackTrace();
		}

		mainActivity.callJsEvent(PROCESSING_FALSE);
		if(result) {
			this.onSuccess();
		} else {
			Felica.onError(callbackId);
		}
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}

    private CommandResponse executeFelica(CommandRequest commandRequest)
            throws NfcException {
        byte[] result;

        if (this.felicaTag == null) {
            throw new NfcException("felicaTag is null!");
        }

        try {
            if (!felicaTag.isConnected()) {
                felicaTag.connect();
            }
            result = felicaTag.transceive(commandRequest.getBytes());
            
            StringBuffer sb = new StringBuffer();
            for(int i=0;i<result.length;i++) {
            	sb.append(String.format("%02X", result[i]) + " ");
            }
        } catch (Exception e) {
            throw new NfcException(e);
        }
        return new CommandResponse(result);
    }

    public static class CommandRequest {
        protected final byte length;
        protected final byte commandCode;
        protected final IdM idm;
        protected final byte[] data;

        public CommandRequest(CommandRequest command) {
            this(command.getBytes());
        }

        public CommandRequest(final byte[] data) {
            this(data[0], Arrays.copyOfRange(data, 1, data.length));
        }
        
        public CommandRequest(byte commandCode, final byte... data) {
            this.commandCode = commandCode;
            if (data.length >= 8) {
                this.idm = new IdM(Arrays.copyOfRange(data, 0, 8));
                this.data = Arrays.copyOfRange(data, 8, data.length);
            } else {
                this.idm = null;
                this.data = Arrays.copyOfRange(data, 0, data.length);
            }
            this.length = (byte) (data.length + 2);
        }

        public CommandRequest(byte commandCode, IdM idm, final byte... data) {
            this.commandCode = commandCode;
            this.idm = idm;
            this.data = data;
            this.length = (byte) (idm.getBytes().length + data.length + 2);
        }
        
        public CommandRequest(byte commandCode, byte[] idm, final byte... data) {
            this.commandCode = commandCode;
            this.idm = new IdM(idm);
            this.data = data;
            this.length = (byte) (idm.length + data.length + 2);
        }

        public IdM getIDm() {
            return this.idm;
        }

        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(this.length);
            if (this.idm != null) {
                buff.put(this.length).put(this.commandCode).put(
                        this.idm.getBytes()).put(this.data);
            } else {
                buff.put(this.length).put(this.commandCode).put(this.data);
            }
            return buff.array();
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("FeliCa コマンドパケット \n");
            sb.append(" コマンド名:" + Util.getHexString(this.commandCode) + "\n");
            sb.append(" データ長: " + Util.getHexString(this.length) + "\n");
            sb.append(" コマンドコード : " + Util.getHexString(this.commandCode)
                    + "\n");
            if (this.idm != null)
                sb.append(" " + this.idm.toString() + "\n");
            sb.append(" データ: " + Util.getHexString(this.data) + "\n");
            return sb.toString();
        }

    }

    public static class CommandResponse {
        protected final byte[] rawData;
        protected final byte length;
        protected final byte responseCode;
        protected final IdM idm;
        protected final byte[] data;

        public CommandResponse(CommandResponse response) {
            this(response.getBytes());
        }

        public CommandResponse(byte[] data) {
            this.rawData = data;
            this.length = data[0];
            this.responseCode = data[1];
            this.idm = new IdM(Arrays.copyOfRange(data, 2, 10));
            this.data = Arrays.copyOfRange(data, 10, data.length);
        }

        public IdM getIDm() {
            return this.idm;
        }

        public byte[] getBytes() {
            return this.rawData;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(" \n\n");
            sb.append("FeliCa レスポンスパケット \n");
            sb.append(" コマンド名:" + Util.getHexString(this.responseCode) + "\n");
            sb.append(" データ長: " + Util.getHexString(this.length) + "\n");
            sb.append(" レスポンスコード: " + Util.getHexString(this.responseCode)
                    + "\n");
            sb.append(" " + this.idm.toString() + "\n");
            sb.append(" データ: " + Util.getHexString(this.data) + "\n");
            return sb.toString();
        }
    }

    public static class IdM {
        final byte[] manufactureCode;
        final byte[] cardIdentification;
        
        public IdM(byte[] bytes) {
            this.manufactureCode = new byte[] { bytes[0], bytes[1] };
            this.cardIdentification = new byte[] { bytes[2], bytes[3],
                    bytes[4], bytes[5], bytes[6], bytes[7] };
        }

        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(this.manufactureCode.length
                    + this.cardIdentification.length);
            buff.put(this.manufactureCode).put(this.cardIdentification);
            return buff.array();
        }

        @Override
        public String toString() {
        	return Util.getHexString(this.getBytes());
        }

        public String simpleToString() {
            return Util.getHexString(getBytes());
        }
    }
    
    public static class PMm {
        final byte[] icCode;
        final byte[] maximumResponseTime;

        public PMm(byte[] bytes) {
            this.icCode = new byte[] { bytes[0], bytes[1] };
            this.maximumResponseTime = new byte[] { bytes[2], bytes[3],
                    bytes[4], bytes[5], bytes[6], bytes[7] };
        }

        public byte[] getBytes() {
            ByteBuffer buff = ByteBuffer.allocate(this.icCode.length
                    + this.maximumResponseTime.length);
            buff.put(this.icCode).put(this.maximumResponseTime);
            return buff.array();
        }

        @Override
        public String toString() {
            return Util.getHexString(this.icCode);
        }
    }
    
    public static class SystemCode {
        final byte[] systemCode;

        public SystemCode(byte[] bytes) {
            this.systemCode = bytes;
        }

        public byte[] getBytes() {
            return this.systemCode;
        }

        @Override
        public String toString() {
            return Util.getHexString(this.systemCode);
        }
    }

    public static class ServiceCode {
        final byte[] serviceCode;
        final byte[] serviceCodeLE;

        public ServiceCode(byte[] bytes) {
            this.serviceCode = bytes;
            if (bytes.length == 2) {
                this.serviceCodeLE = new byte[] { bytes[0], bytes[1] };
            } else {
                this.serviceCodeLE = null;
            }
        }

        public ServiceCode(int serviceCode) {
            this(new byte[] { (byte) (serviceCode & 0xff),
                    (byte) (serviceCode >> 8) });
        }

        public byte[] getBytes() {
            return this.serviceCode;
        }

        public boolean encryptNeeded() {
            boolean ret = false;
            if (serviceCodeLE != null) {
                ret = (serviceCodeLE[0] & 0x1) == 0;
            }
            return ret;
        }
        public boolean isWritable() {
            boolean ret = false;
            if (serviceCodeLE != null) {
                int accessInfo = serviceCodeLE[0] & 0x3F;
                ret = (accessInfo & 0x2) == 0 || accessInfo == 0x13
                        || accessInfo == 0x12;
            }
            return ret;
        }
        
        @Override
        public String toString() {
            return Util.getHexString(serviceCodeLE);
        }
    }
    
    public static class NfcException extends Exception{
        private static final long serialVersionUID = 1L;
        public NfcException(Exception e) {
            super(e);
        }
        public NfcException(String s) {
            super(s);
        }
    }
    
    public static class Util {
        public static String getHexString(byte[] byteArray, int... split) {
            StringBuilder builder = new StringBuilder();
            byte[] target = null;
            if (split.length <= 1) {
                target = byteArray;
            } else if (split.length < 2) {
                target = Arrays.copyOfRange(byteArray, 0, 0 + split[0]);
            } else {
                target = Arrays.copyOfRange(byteArray, split[0], split[0] + split[1]);
            }

            for (byte b : target) {
                builder.append(String.format("%02X", b).toUpperCase());
            }
            return builder.toString();
        }

        public static String getHexString(byte data) {
            return String.format("%02X", data);
        }

        public static String getHexString(Object[] objList) {
            if (objList == null) {
                return null;
            }
            StringBuffer sb = new StringBuffer();
            for (Object obj : objList) {
                sb.append(obj.toString()+",");
            }
            return sb.toString();
        }
    }
}