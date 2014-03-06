package jp.zyyx.dynamicapp.wrappers;

import jp.zyyx.dynamicapp.DynamicAppActivity;
import jp.zyyx.dynamicapp.core.Command;
import jp.zyyx.dynamicapp.core.PluginsFactory;
import jp.zyyx.dynamicapp.utilities.Constant;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
public class WebViewClientWrapper extends WebViewClient {
	private static final String TAG = "WebViewClientWrapper";

	private static final String KEY_WAIT_MSG = "wait_msg";
	private static final String REG_SLASH = "\\.";
	private static final String REG_EQUALS = "=";

	private ProgressDialog progressDialog = null;

	@SuppressLint("InlinedApi")
	@Override
	public void onPageStarted(final WebView webView, String url, Bitmap favicon) {
		DebugLog.e(TAG, "onPageStarted url:" + url + "***");

		String title = "お待ちください....";
		if (DynamicAppActivity.strings != null) {
			title = DynamicAppActivity.strings.get(KEY_WAIT_MSG, title);
		}

		if (progressDialog == null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				progressDialog = new ProgressDialog(webView.getContext(), android.R.style.Theme_Holo_Light_Panel);
			} else {
				progressDialog = new ProgressDialog(webView.getContext());
			}
		}
		progressDialog.setMessage(title);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.setCancelable(true);
		progressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				webView.stopLoading();
			}
		});
		if (!progressDialog.isShowing()) {
			progressDialog.show();
		}
	}

	@Override
	public boolean shouldOverrideUrlLoading(WebView webView, String url) {
		DebugLog.e(TAG, "shouldOverrideUrlLoading url:" + url + "***");
		Uri uri = Uri.parse(url);
		String scheme = uri.getScheme();
		if (scheme.equalsIgnoreCase(Constant.APP_TAG)) {
			String[] hostList = uri.getHost().split(REG_SLASH);
			String className = hostList[0];
			String methodName = hostList[1];

			String[] query = uri.getQuery().split(REG_EQUALS);
			String callbackId = query[1];
			String params = uri.getPath().substring(1);

			DynamicAppActivity dynamicApp = (DynamicAppActivity) Utilities.dynamicAppActivityRef;
			if (dynamicApp.isPluginEnabled(className)) {
				Command command = PluginsFactory.instantiate(className, methodName, params, callbackId);
				Utilities.currentCommand = command;
				command.execute();
			} else {
				dynamicApp.callJsEvent("DynamicApp.processing = false;");
				if (dynamicApp.isXMLSettingsMissing()) {
					DebugLog.e(TAG, "ERROR: dynamicappsettings.xml is not found in res/xml folder.");
				} else {
					DebugLog.e(TAG, className + " plugin is not enabled or not in dynamicappsettings.xml.");
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		DebugLog.e(TAG, "onPageFinished url:" + url + "***");
		if (null != progressDialog) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			progressDialog = null;
		}
	}
}

