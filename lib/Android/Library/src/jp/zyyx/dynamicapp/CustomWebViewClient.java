package jp.zyyx.dynamicapp;

import jp.zyyx.dynamicapp.core.Command;
import jp.zyyx.dynamicapp.utilities.Constant;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CustomWebViewClient extends WebViewClient {
	private static final String TAG = "CustomWebViewClient";

	private static final String KEY_WAIT_MSG = "wait_msg";
	private static final String REG_SLASH = "\\.";
	private static final String REG_EQUALS = "=";

	private ProgressDialog progressDialog = null;

	@SuppressLint("InlinedApi")
	@Override
	public void onPageStarted(final WebView webView, String url, Bitmap favicon) {
		DebugLog.e(TAG, "onPageStarted:" + url);

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
	public boolean shouldOverrideUrlLoading(WebView view, String urlStr) {
		DebugLog.e(TAG, "shouldOverrideUrlLoading:" + urlStr);
		return this.executeCommand(urlStr);
	}

	@Override
	public void onPageFinished(WebView view, String url) {
		DebugLog.e(TAG, "onPageFinished:" + url);
		if (null != progressDialog) {
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			progressDialog = null;
		}
	}

	/**
	 * This function executes the class method being passed from
	 * the urlStr parameter. It uses the DynamicAppFactory class
	 * which loads the class method dynamically.
	 * 
	 * @param urlStr	the generated URL from dynamicapp.js execute function
	 * @return true		to disable page redirection
	 */
	public boolean executeCommand(String urlStr) {
		boolean isFunction = false;
		Uri uri = Uri.parse(urlStr);
		String scheme = uri.getScheme();

		if (scheme.equalsIgnoreCase(Constant.APP_TAG)) {
			String[] hostList = uri.getHost().split(REG_SLASH);
			final String cls = hostList[0];
			final String methodName = hostList[1];

			String[] query = uri.getQuery().split(REG_EQUALS);
			final String callbackId = query[1];
			final String strParams = uri.getPath().substring(1);

			DynamicAppActivity dynamicApp = (DynamicAppActivity) DynamicAppUtils.dynamicAppActivityRef;
			if (dynamicApp.isPluginEnabled(cls)) {
				Command command = DynamicAppFactory.instantiate(cls, methodName, strParams, callbackId);
				DynamicAppUtils.currentCommandRef = command;
				command.execute();
			} else {
				dynamicApp.callJsEvent("DynamicApp.processing = false;");
				
				if (dynamicApp.isXMLSettingsMissing())
					DebugLog.e(TAG, "ERROR: dynamicappsettings.xml is not found in res/xml folder.");
				else
					DebugLog.e(TAG, cls + " plugin is not enabled or not in dynamicappsettings.xml.");
			}
			
			isFunction = true;
		}
		return isFunction;
	}
}

