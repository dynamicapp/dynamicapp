package jp.zyyx.dynamicapp;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import jp.zyyx.dynamicapp.core.PluginsFactory;
import jp.zyyx.dynamicapp.plugins.Ads;
import jp.zyyx.dynamicapp.plugins.CustomNotification;
import jp.zyyx.dynamicapp.plugins.DynamicAppBluetooth;
import jp.zyyx.dynamicapp.plugins.DynamicAppBluetooth4LE;
import jp.zyyx.dynamicapp.plugins.Felica;
import jp.zyyx.dynamicapp.plugins.view.DynamicAppVideoView;
import jp.zyyx.dynamicapp.utilities.Constant;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;
import jp.zyyx.dynamicapp.wrappers.JSONObjectWrapper;
import jp.zyyx.dynamicapp.wrappers.WebViewClientWrapper;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.media.AudioManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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
@SuppressWarnings("deprecation")
public class DynamicAppActivity extends Activity {
	private static final String TAG = "DynamicAppActivity";

	private static final int MENU_ID_MENU1 = (Menu.FIRST + 1);
	private static final int REQUEST_CODE_SPLASH = 3;
	private static final int ACTIVITY_REQUEST_CD_NOTIFICATION = 4;
	public static final String KEY_RESOURCE_ID = "resourceID";

	public static RelativeLayout mainLayout;
	public static Handler handler = null;
	private int splashResourceID = 0;
	private WebView webView = null;
	private DynamicAppVideoView videoView = null;

	private DynamicAppBluetooth bluetoothPlugin = null;
	private DynamicAppBluetooth4LE bluetooth4LEPlugin = null;
	private Felica felicaPlugin = null;

	private HashMap<String, Boolean> plugins = null;
	private boolean isMissing = false;
	private boolean webViewRestored = false;

	public static JSONObjectWrapper strings = null;

	static {
		System.loadLibrary(Constant.LIB_IMAGE_DECRYPT);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DebugLog.e(TAG, "onCreate");

		init();
		// comment out this line below to disable orientation/rotation
		//DynamicAppUtils.fixDisplayOrientation(this);

		Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
		intent.putExtra(KEY_RESOURCE_ID, splashResourceID);
		startActivityForResult(intent, REQUEST_CODE_SPLASH);
	}

	@Override
	protected void onResume() {
		super.onResume();
		DebugLog.e(TAG, "onResume");
		/**
		 * Call onResume on plugins that are recently paused
		 */
		if (Utilities.currentCommand != null) {
			Utilities.currentCommand.onResume();
		}
		if (bluetoothPlugin != null) {
			bluetoothPlugin.registerReceiver();
		}
		if (felicaPlugin != null) {
			felicaPlugin.registerReceiver(getApplicationContext());
		}
		if (bluetooth4LEPlugin != null) {
			bluetooth4LEPlugin.onResume();
		}
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	@Override
	public void onPause() {
		super.onPause();
		DebugLog.e(TAG, "onPause");
		/**
		 * Call onPause on plugins that are active or playing
		 */
		if (Utilities.currentCommand != null) {
			Utilities.currentCommand.onPause();
		}
		if (bluetoothPlugin != null) {
			bluetoothPlugin.unregisterReceiver();
		}
		if (bluetooth4LEPlugin != null) {
			bluetooth4LEPlugin.onPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		DebugLog.e(TAG, "onDestroy");
		if (Utilities.currentCommand != null) {
			Utilities.currentCommand.onDestroy();
		}
		if (bluetooth4LEPlugin != null) {
			bluetooth4LEPlugin.onDestroy();
		}

//TODO		File cache = getCacheDir();
//		File appDir = new File(cache.getParent());
//		if (appDir.exists()) {
//			String[] children = appDir.list();
//			for (String s : children) {
//				if (!s.equals("libs") && !s.equals("databases")) {
//					Utilities.deletefile(new File(appDir, s));
//					DebugLog.e(TAG, "File /data/data/APP_PACKAGE/" + s + " DELETED");
//				}
//			}
//		}
		//webView.destroy();
		//webView = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		DebugLog.e(TAG, "onSaveInstanceState");
		if (webView != null && outState != null) {
			DebugLog.e(TAG, "Saving webview state...");
			webView.saveState(outState);
		}
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		DebugLog.e(TAG, "onRestoreInstanceState");
		if (webView == null) {
			webView = new WebView(this);
		}
		if (webView != null && savedInstanceState != null) {
			DebugLog.e(TAG, "Restoring webview state...");
			webView.restoreState(savedInstanceState);
			webViewRestored = true;
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		DebugLog.e(TAG, "onConfigurationChanged");
		if (isPluginEnabled(PluginsFactory.PLUGIN_AD)) {
			Ads.getInstance().onConfigurationChanged(newConfig); // changes plug-in layout on config change 
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		DebugLog.e(TAG, "onKeyDown keyCode:" + keyCode + "***");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (Utilities.currentCommand != null) {
				Utilities.currentCommand.onBackKeyDown();
			}
			if (bluetooth4LEPlugin != null) {
				bluetooth4LEPlugin.onBackKeyDown();
			}
			moveTaskToBack(true);
			/*if (mWebView.canGoBack()) {
				mWebView.goBack();
				return true;
			}*/
		} else if (keyCode == KeyEvent.KEYCODE_FOCUS || keyCode == KeyEvent.KEYCODE_CAMERA) {
			DebugLog.w(TAG, "KEYCODE_CAMERA");
			// Handle these events so they don't launch the Camera app
			return true;
		} 
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		DebugLog.e(TAG, "onCreateOptionsMenu");
		if (Utilities.download_flg) {
			menu.add(0 , MENU_ID_MENU1 , Menu.NONE , "Server Settings");
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		DebugLog.e(TAG, "onOptionsItemSelected menu:" + item.getTitle() + "***");
		switch (item.getItemId()) {
		default:
			super.onOptionsItemSelected(item);
			break;
		case MENU_ID_MENU1:
			if (Utilities.download_flg) {
				Intent i = new Intent(getApplicationContext(), jp.zyyx.dynamicapp.Preferences.class);
				startActivity(i);
			}
			break;
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@SuppressLint({ "InlinedApi", "SetJavaScriptEnabled" })
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		DebugLog.e(TAG, "onActivityResult requestCode:" + requestCode + "|resultCode:" + resultCode +
				"|RESULT_OK:" + Activity.RESULT_OK + "|RESULT_CANCELED:" + Activity.RESULT_CANCELED + "***");
		// Relays intent results to the plug-in
		if (Utilities.currentCommand != null) {
			Utilities.currentCommand.onActivityResult(requestCode, resultCode, intent);
		}

		if (requestCode == REQUEST_CODE_SPLASH) {
			try {
				strings = new JSONObjectWrapper(StringResource.load(this));
			} catch (JSONException e) {
				e.printStackTrace();
			}

			requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			setVolumeControlStream(AudioManager.STREAM_MUSIC);

			mainLayout = new RelativeLayout(this);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			mainLayout.setLayoutParams(params);
			if (webView == null) {
				webView = new WebView(this);
			}
			videoView = new DynamicAppVideoView(this);
			videoView.setBackgroundColor(Color.TRANSPARENT);
			LinearLayout linearLayout = new LinearLayout(this);
			linearLayout.addView(videoView);
			webView.addView(linearLayout);
			mainLayout.addView(webView);
			setContentView(mainLayout);

			Utilities.VideoViewRef = videoView;
			webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
			webView.getLayoutParams().height = WindowManager.LayoutParams.MATCH_PARENT;
			webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
			webView.getSettings().setJavaScriptEnabled(true);
			webView.getSettings().setBuiltInZoomControls(true);
			webView.getSettings().setLoadWithOverviewMode(true);
			webView.getSettings().setUseWideViewPort(true);
			webView.getSettings().setDomStorageEnabled(true);
			webView.getSettings().setSupportZoom(false);
			webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
			File databasePath = new File(getCacheDir(), Constant.DATABASE_FILE);
			webView.getSettings().setDatabasePath(databasePath.toString());
			webView.setWebViewClient(new WebViewClientWrapper());
			webView.setWebChromeClient(new WebChromeClient() {
				@Override
				public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota,
						long estimatedSize, long totalUsedQuota, QuotaUpdater quotaUpdater) {
					quotaUpdater.updateQuota(5 * 1024 * 1024);
				}
				@Override
				public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
					DebugLog.w(TAG, "onConsoleMessage sourceId:"+ consoleMessage.sourceId() + "|lineNumber:"+ consoleMessage.lineNumber() +
							"|message: "+ consoleMessage.message() + "***");
					return true;
				}
			});

			if (!webViewRestored) {
				webView.loadUrl("file://" + Utilities.makePath("www/index.html"));
			}
		} else if (requestCode == ACTIVITY_REQUEST_CD_NOTIFICATION) {
			CustomNotification.hideStatusBar();
			String action = intent.getAction();
			if (action != null) {
				startActivity(intent);
			}
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String action = intent.getAction();
		if (felicaPlugin != null && NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			felicaPlugin.initTech(intent);
		}
	}


	/** Private Methods */
	private void init() {
		Utilities.dynamicAppActivityRef = this;
		handler = new Handler();
		loadPlugins();

		if (isPluginEnabled(PluginsFactory.PLUGIN_BLUETOOTH)) {
			bluetoothPlugin = DynamicAppBluetooth.getInstance();
			bluetoothPlugin.setupServer();
		} 

		/* For now, comment out this code.
		bluetooth4LEPlugin = DynamicAppBluetooth4LE.getInstance();
		*/

		if (isPluginEnabled(PluginsFactory.PLUGIN_FELICA)) {
			felicaPlugin = Felica.getInstance();
		}
	}

	private void loadPlugins() {
		int resId = this.getResources().getIdentifier("dynamicappsettings", "xml", this.getPackageName());
		this.plugins =  new HashMap<String, Boolean>();

		if (resId == 0) {
			this.isMissing = true;
			DebugLog.e(TAG, "ERROR: dynamicappsettings.xml is not found in res/xml folder.");
		} else {
			this.isMissing = false;
			DebugLog.w(TAG, "dynamicappsettings.xml is found.");
			XmlResourceParser xml = this.getResources().getXml(resId);

			int eventType = -1;
			String pluginLoadable = "false";
			String pluginName = "";
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String strNode = xml.getName();
					DebugLog.w(TAG, "strNode: " + strNode);
					if (strNode.equals("plugin")) {
						pluginLoadable = xml.getAttributeValue(null, "value");
						pluginName = xml.getAttributeValue(null, "name");
						boolean loadable = (pluginLoadable != null && pluginLoadable.equalsIgnoreCase("true"));
						this.plugins.put(pluginName, loadable);
						DebugLog.w(TAG, "Plugin: "+pluginName+" => " + loadable);
					}
				}
				try {
					eventType = xml.next();
				} catch (XmlPullParserException e) {
					this.plugins = new HashMap<String,Boolean>();
					DebugLog.e(TAG, "Exception");
				} catch (IOException e) {
					this.plugins = new HashMap<String,Boolean>();
					DebugLog.e(TAG, "Exception");
				}
			}
		}
	}


	/** Public Methods */
	/**
	 * @param query  javascript code to be executed
	 */
	private Handler localHandler = new Handler();
	public void callJsEvent(final String query) {
		DebugLog.w(TAG, "callJsEvent query:" + query + "***");
		localHandler.post(new Runnable() {
			@Override
			public void run() {
				webView.loadUrl("javascript:" + query);
			}
		});
	}

	public WebView getWebView() {
		return this.webView;
	}

	public boolean isPluginEnabled(String pluginName) {	
		PackageManager packageManager = getPackageManager();
		boolean enabled = (this.plugins.containsKey(pluginName) && this.plugins.get(pluginName));
		DebugLog.w(TAG, "isEmulator:" + Utilities.isEmulator());
		if (pluginName.equalsIgnoreCase("Bluetooth") && (Utilities.isEmulator() || !packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH))) {
			enabled = false;
		} else if (pluginName.equalsIgnoreCase("Felica") && (Utilities.isEmulator() || !packageManager.hasSystemFeature(PackageManager.FEATURE_NFC))){
			enabled = false;
		} else if (pluginName.equalsIgnoreCase("Camera") && (Utilities.isEmulator() || !packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA))){
			enabled = false;
		}
		return enabled;
	}

	public boolean isXMLSettingsMissing() {
		return this.isMissing;
	}
}

