/**
 * 
 */
package jp.zyyx.dynamicapp.core;

import jp.zyyx.dynamicapp.DynamicAppActivity;
import jp.zyyx.dynamicapp.JSONObjectWrapper;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.res.Configuration;
import android.webkit.WebView;

/**
 * @author Zyyx
 *
 */
public abstract class DynamicAppPlugin implements Command {
	private static final String TAG = "DynamicAppPlugin";

	private static final int STATUS_OK = 1;
	private static final int ERROR = 9;
	
	protected static DynamicAppActivity dynamicApp;
	protected static String callbackId = null;
	protected static JSONObject jsonObject = null;
	protected static JSONObject message = null;
	
	protected WebView webView;
	protected String params = null;
	protected String methodName = null;
	protected JSONObjectWrapper param = null;
	
	protected static final int ACTIVITY_REQUEST_CD_CAMERA = 1;
	protected static final int ACTIVITY_REQUEST_CD_QR = 2;
	protected static final int ACTIVITY_REQUEST_CD_SPLASH = 3;
	protected static final int ACTIVITY_REQUEST_CD_NOTIFICATION = 4;
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
	public DynamicAppPlugin() {
		DynamicAppPlugin.dynamicApp = (DynamicAppActivity) DynamicAppUtils.dynamicAppActivityRef;
		DynamicAppPlugin.jsonObject = new JSONObject();
		DynamicAppPlugin.message = new JSONObject();
		
		try {
			jsonObject.put("message", message);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public abstract void execute();

	@Override
	public void init(String methodName, String params, String callbackId) {
		this.methodName = methodName;
		this.params = params;
		
		try {
			param = new JSONObjectWrapper(params);
		} catch (JSONException e) {
			param = null;
			e.printStackTrace();
		}
		
		DynamicAppPlugin.callbackId = callbackId;
	}

	@Override
	public void onSuccess() {
		try {
			jsonObject.put("status", STATUS_OK);
			jsonObject.put("keepCallback", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String script = "DynamicApp.callbackSuccess(\"" + callbackId + "\","
				+ jsonObject + ");";
		DebugLog.i(TAG, "success: " + script);
		dynamicApp.callJsEvent(script);
	}
	
	/**
	 * @param data			json format data to be return to the javascript client
	 * @param callbackId	javascript callback method id
	 * @param keepCallback	flag for keeping callbacks
	 */
	public static void onSuccess(Object data, String callbackId, boolean keepCallback) {
		try {
			jsonObject.put("message", data);
			jsonObject.put("status", STATUS_OK);
			jsonObject.put("keepCallback", keepCallback);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String script = "DynamicApp.callbackSuccess(\"" + callbackId + "\","
				+ jsonObject + ");";
		DebugLog.i(TAG, "success: " + script);
		dynamicApp.callJsEvent(script);
	}

	@Override
	public void onError() {
		String script = "DynamicApp.callbackError(\"" + callbackId + "\","
				+ jsonObject + ");";
		DebugLog.e(TAG, "error: " + script);
		dynamicApp.callJsEvent(script);
	}
	
	/**
	 * @param callbackId	javascript callback method id
	 */
	public static void onError(String callbackId) {
		String script = "DynamicApp.callbackError(\"" + callbackId + "\","
				+ jsonObject + ");";
		DebugLog.e(TAG, "error: " + script);
		dynamicApp.callJsEvent(script);
	}

	/**
	 * @param data			json format data to be return to the javascript client
	 * @param callbackId	javascript callback method id
	 */
	public static void onError(String data, String callbackId) {
		try {
			jsonObject.put("message", data);
			jsonObject.put("status", ERROR);
			jsonObject.put("keepCallback", false);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		String script = "DynamicApp.callbackError(\"" + callbackId + "\","
				+ jsonObject + ");";
		DebugLog.e(TAG, "error: " + script);
		dynamicApp.callJsEvent(script);
	}

	/**
	 * @param dynamicApp	reference to DynamicAppActivity
	 */
	public void setContext(DynamicAppActivity dynamicApp) {
		DynamicAppPlugin.dynamicApp = dynamicApp;
	}

	@Override
	public void onPause() {
		DebugLog.i(TAG, "Plugin onPause");
	}

	@Override
	public void onResume() {
		DebugLog.i(TAG, "Plugin onResume");
	}

	@Override
	public void onDestroy() {
		DebugLog.i(TAG, "Plugin onDestroy");
	}

	@Override
	public void onBackKeyDown() {
		DebugLog.i(TAG, "Plugin onBackKeyDown");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		DebugLog.i(TAG, "requestCode: " + requestCode);
		DebugLog.i(TAG, "resultCode: " + resultCode);
		DebugLog.i(TAG, "Intent: " + intent);
    }

	@Override
	public void onConfigurationChanged(Configuration config) {
		DebugLog.i(TAG, "Plugin onConfigurationChanged");
	}
}
