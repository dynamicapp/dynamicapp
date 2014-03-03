package jp.zyyx.dynamicapp.plugins;

import java.security.MessageDigest;
import java.util.Random;

import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.utilities.DebugLog;

import org.json.JSONException;

/**
 * Methods:
 * <ul>
 * <li>encrypt
 * <li>decrypt
 * </ul>	
 * 
 * @author 		Zyyx
 * @version     %I%, %G%
 * @since       1.0
 */
public class Encryptor extends DynamicAppPlugin{
	private static final String TAG = "Encryptor";

	private static final String ENCRYPT_KEY = "cf2a20e1";
	private static Encryptor instance = null;
	 
    private Encryptor() {}

    /**
     * return Encryptor class instance
     *
     * @return
     */
	public static synchronized Encryptor getInstance() {
            if (instance == null) {
                    instance = new Encryptor();
            }
            return instance;
    }
	
	/**
     * encodes a string using Base64Encode
	 * @return 
	 */
	public String encryptText(){
		try {
			String text = param.get("text", "");
			int size = 0;

			StringBuilder sb = new StringBuilder();
			char[] encryptedArray = md5(Encryptor.ENCRYPT_KEY);
			char[] data = text.toCharArray();
			size = data.length;

			for(int i=0; i<size; i++) {
				sb.append(String.format("%04x", (data[i] & 0xFFFF) ^ encryptedArray[i%1024]));
			}
			
			return sb.toString();
		} catch(Exception e) {
			DebugLog.e(TAG, "Exception @ encrypt: " + e.getMessage());
			return null;
		}
	}
	
	/**
     * decodes an encrypted string using Base64Encode
	 * @return 
     */
	public String decrypt(){
		String text = param.get("text", "");
		char byte_chars[] = {'\0','\0','\0','\0','\0'}; 
		char[] encryptedArray = md5(Encryptor.ENCRYPT_KEY);
		int length = text.length();
		StringBuilder sb = new StringBuilder();

		try {
			for (int i=0; i < length / 4; i++) {
		        byte_chars[0] = text.charAt(i*4);
		        byte_chars[1] = text.charAt(i*4+1);
		        byte_chars[2] = text.charAt(i*4+2);
		        byte_chars[3] = text.charAt(i*4+3);
		        String bChars = Character.toString(byte_chars[0])
		        		+ Character.toString(byte_chars[1])
		        		+ Character.toString(byte_chars[2])
		        		+ Character.toString(byte_chars[3]);
		        long cc = Long.parseLong(bChars, 16);
		        
		        sb.append((char) ((cc & 0xFFFF) ^ encryptedArray[i%1024]));
		    }
	        			
			return sb.toString();
		} catch(Exception e) {
			DebugLog.e(TAG, "Exception @ decrypt: " + e.getMessage());
		}
		return null;
	}

	/**
     * encodes the string using MD5
     * 
     * @param str	the string to be converted to MD5
     */
	private static char[] md5(String str) 
	{
		char encryptArray[] = new char[1024];
	    try {
	        // Create MD5 Hash
	    	String strUTF8 = new String(str.getBytes(), "UTF-8");
	        MessageDigest digest = MessageDigest.getInstance("MD5");
	        digest.update(strUTF8.getBytes(),0,str.length());
	        byte messageDigest[] = digest.digest();
	        
	        long var[] = new long[4];
	        for(int i=0; i<4; i++) {
	            var[i] = 0 & 0xffff;
	            for(int j=0; j<4; j++) {
	                var[i] += (messageDigest[15 - (4*i+j)] << 8*(3-j));
	                var[i] = var[i] & 0xffff;
	            }
	        }
	        
	        long seed = 0 & 0xffff;
	        for(int i=0; i<4; i++) {
	            seed += var[i];
	            seed = seed & 0xffff;
	        }
	        
	        Random rand = new Random(seed);
		    for(int i=0; i<1024; i++) {
		    	long rnd = rand.nextLong() & 0xffff;
	            encryptArray[i] = (char) (rnd) ;
	        }
	        
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return encryptArray;
	}
	
	@Override
	public void execute() {
		DebugLog.i(TAG, this.methodName +" method is called.");
		if(this.methodName.equalsIgnoreCase("encryptText")) {
			String encryptedText = this.encryptText();
			
			if(encryptedText != null) {
				try {
					message.put("encText", encryptedText);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.onSuccess();
			}else {
				try {
					message.put("msg", "Failed to encrypt text.");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.onError();
			}
			dynamicApp.callJsEvent(PROCESSING_FALSE);
		} else if(this.methodName.equalsIgnoreCase("decryptText")) {
			String decryptedText = this.decrypt();
			if(decryptedText != null) {
				try {
					message.put("decText", decryptedText);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.onSuccess();
			}else {
				try {
					message.put("msg", "Failed to decrypt text.");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				this.onError();
			}
			dynamicApp.callJsEvent(PROCESSING_FALSE);
		}
	}
}
