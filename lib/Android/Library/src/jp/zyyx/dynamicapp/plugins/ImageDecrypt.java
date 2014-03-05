package jp.zyyx.dynamicapp.plugins;

import jp.zyyx.dynamicapp.core.Plugin;
import jp.zyyx.dynamicapp.utilities.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;

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
public class ImageDecrypt extends Plugin{
	private native byte[] decrypt(String filename);

	private static ImageDecrypt instance = null;
	
	private ImageDecrypt() {
		super();
	}
	
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
		mainActivity.callJsEvent(PROCESSING_FALSE);
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
							data = decrypt(Utilities.makeWWWPath(filename));
						}

						if(data != null) {
							JSONObject jsonObject = new JSONObject();

							jsonObject.put("srcEnc", filename);
							jsonObject.put("srcDec", "data:image/jpeg;base64," + Base64.encodeToString(data, Base64.DEFAULT));
							
							Plugin.onSuccess(jsonObject, callbackId, (i != len-1));
						} else {
							Plugin.onError(callbackId);
						}
					} 
				}
			} catch (JSONException e) {
				Plugin.onError(callbackId);
				e.printStackTrace();
			}
		}
		instance = null;
	}
}
