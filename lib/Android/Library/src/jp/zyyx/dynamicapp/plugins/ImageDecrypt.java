package jp.zyyx.dynamicapp.plugins;

import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

public class ImageDecrypt extends DynamicAppPlugin{
	private native byte[] decrypt(String filename);

	private static ImageDecrypt instance = null;
	
	private ImageDecrypt() {}
	
	public static synchronized ImageDecrypt getInstance() {
        if (instance == null) {
	            instance = new ImageDecrypt();
	            
	    }
	    return instance;
	}
	
	
	
	public void init(String methodName, String params, String callbackId){
		super.init(methodName, params, callbackId);
		
	}
	
	@Override
	public void execute() {
		dynamicApp.callJsEvent(PROCESSING_FALSE);
		if (methodName.equalsIgnoreCase("decrypt")) {
			try {
				JSONArray jsonArray = (JSONArray) param.get("imgSrcList");
				
				if (jsonArray != null) { 
					int len = jsonArray.length();
					for (int i=0;i<len;i++){ 
						String filename;
						filename = jsonArray.get(i).toString();
						byte[] data = null;
						if(filename.length() > 0) {
							data = decrypt(DynamicAppUtils.makeWWWPath(filename));
						}

						if(data != null) {
							JSONObject jsonObject = new JSONObject();

							jsonObject.put("srcEnc", filename);
							jsonObject.put("srcDec", "data:image/jpeg;base64," + Base64.encodeToString(data, Base64.DEFAULT));
							
							DynamicAppPlugin.onSuccess(jsonObject, callbackId, (i != len-1));
						} else {
							DynamicAppPlugin.onError(callbackId);
						}
					} 
				}
			} catch (JSONException e) {
				DynamicAppPlugin.onError(callbackId);
				e.printStackTrace();
			}
		}
		instance = null;
	}
}
