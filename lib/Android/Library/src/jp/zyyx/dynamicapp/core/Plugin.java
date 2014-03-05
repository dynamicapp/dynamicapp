package jp.zyyx.dynamicapp.core;

import jp.zyyx.dynamicapp.DynamicAppActivity;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;
import jp.zyyx.dynamicapp.wrappers.JSONObjectWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;

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
public abstract class Plugin implements Command {
	private static final String TAG = "Plugin";

	private static final String KEY_MESSAGE = "message";
	private static final String KEY_STATUS = "status";
	private static final String KEY_KEEPCALLBACK = "keepCallback";
	private static final int STATUS_OK = 1;
	private static final int ERROR = 9;

	protected static DynamicAppActivity mainActivity;
	protected static JSONObject jsonObject = null;
	protected static JSONObject message = null;
	protected static String callbackId = null;

	protected String params = null;
	protected String methodName = null;
	protected JSONObjectWrapper param = null;

	protected static final int ACTIVITY_REQUEST_CD_CAMERA = 1;
	protected static final int ACTIVITY_REQUEST_CD_QR = 2;
//	private static final int ACTIVITY_REQUEST_CD_SPLASH = 3;
//	private static final int ACTIVITY_REQUEST_CD_NOTIFICATION = 4;
	protected static final int ACTIVITY_REQUEST_CD_SHOW_CONTACTS = 5;
	protected static final int ACTIVITY_REQUEST_CD_PICK_CONTACT = 6;
	protected static final int ACTIVITY_REQUEST_CD_ENABLE_BT = 7;
	protected static final int ACTIVITY_REQUEST_CD_PAIR_DEVICE = 8;
	protected static final int ACTIVITY_REQUEST_CD_TAKE_VIDEO = 9;

	protected static final String PROCESSING_FALSE = "DynamicApp.processing = false;";

	/**
	 * Default plug-in constructor,
	 * also executed before executing subclass constructors
	 */
	public Plugin() {
		mainActivity = (DynamicAppActivity) Utilities.dynamicAppActivityRef;
		jsonObject = new JSONObject();
		message = new JSONObject();
		try {
			jsonObject.put(KEY_MESSAGE, message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(String methodName, String params, String callbackId) {
		DebugLog.w(TAG, "init methodName:" + methodName + "|params:" + params + "|callbackId:" + callbackId + "***");
		this.methodName = methodName;
		this.params = params;
		try {
			this.param = new JSONObjectWrapper(params);
		} catch (JSONException e) {
			this.param = null;
			e.printStackTrace();
		}
		Plugin.callbackId = callbackId;
	}

	@Override
	public void execute() {
		DebugLog.w(TAG, "execute");
	}

	@Override
	public void onSuccess() {
		try {
			jsonObject.put(KEY_STATUS, STATUS_OK);
			jsonObject.put(KEY_KEEPCALLBACK, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String script = "DynamicApp.callbackSuccess(\"" + callbackId + "\"," + jsonObject + ");";
		DebugLog.w(TAG, "onSuccess script:" + script + "***");
		mainActivity.callJsEvent(script);
	}

	/**
	 * @param data			json format data to be return to the javascript client
	 * @param callbackId	javascript callback method id
	 * @param keepCallback	flag for keeping callbacks
	 */
	public static void onSuccess(Object data, String callbackId, boolean keepCallback) {
		try {
			jsonObject.put(KEY_MESSAGE, data);
			jsonObject.put(KEY_STATUS, STATUS_OK);
			jsonObject.put(KEY_KEEPCALLBACK, keepCallback);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String script = "DynamicApp.callbackSuccess(\"" + callbackId + "\"," + jsonObject + ");";
		DebugLog.w(TAG, "onSuccess callbackId:" + callbackId + "|keepCallback:" + keepCallback + "|script:" + script + "***");
		mainActivity.callJsEvent(script);
	}

	@Override
	public void onError() {
		String script = "DynamicApp.callbackError(\"" + callbackId + "\"," + jsonObject + ");";
		DebugLog.e(TAG, "onError script:" + script + "***");
		mainActivity.callJsEvent(script);
	}

	/**
	 * @param callbackId	javascript callback method id
	 */
	public static void onError(String callbackId) {
		String script = "DynamicApp.callbackError(\"" + callbackId + "\"," + jsonObject + ");";
		DebugLog.e(TAG, "onError callbackId:" + callbackId + "|script:" + script + "***");
		mainActivity.callJsEvent(script);
	}

	/**
	 * @param data			json format data to be return to the javascript client
	 * @param callbackId	javascript callback method id
	 */
	public static void onError(String data, String callbackId) {
		try {
			jsonObject.put(KEY_MESSAGE, data);
			jsonObject.put(KEY_STATUS, ERROR);
			jsonObject.put(KEY_KEEPCALLBACK, false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		String script = "DynamicApp.callbackError(\"" + callbackId + "\"," + jsonObject + ");";
		DebugLog.e(TAG, "onError data:" + data + "|callbackId:" + callbackId + "|script:" + script + "***");
		mainActivity.callJsEvent(script);
	}

	/**
	 * @param dynamicApp	reference to DynamicAppActivity
	 */
	public void setContext(DynamicAppActivity dynamicApp) {
		mainActivity = dynamicApp;
	}

	@Override
	public void onResume() {
		DebugLog.w(TAG, "onResume");
	}

	@Override
	public void onPause() {
		DebugLog.w(TAG, "onPause");
	}

	@Override
	public void onDestroy() {
		DebugLog.w(TAG, "onDestroy");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		DebugLog.w(TAG, "onActivityResult requestCode: " + requestCode + "|resultCode: " + resultCode + "***");
    }

	@Override
	public void onBackKeyDown() {
		DebugLog.w(TAG, "onBackKeyDown");
	}

	@Override
	public void onConfigurationChanged(Configuration config) {
		DebugLog.w(TAG, "onConfigurationChanged");
	}
}

