package jp.zyyx.dynamicapp;

import java.io.File;

import jp.zyyx.dynamicapp.utilities.Constant;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;
import jp.zyyx.dynamicapp.wrappers.SharedPreferencesWrapper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

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
public class SplashActivity extends Activity {
	private static final String TAG = "SplashActivity";

	private static final int SAME = 0;
	private static final int DOWNGRADED = -1;
	private static final int UPGRADED = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		Utilities.fixDisplayOrientation(this);

        ImageView imageView = new ImageView(this);
		Intent intent = getIntent();
		int resourceId = intent.getIntExtra(DynamicAppActivity.KEY_RESOURCE_ID, 0);
		if (resourceId != 0) {
			imageView.setImageResource(resourceId);
		}
		imageView.setAdjustViewBounds(false);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		setContentView(imageView);

		boolean debuggable = (this.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
		Utilities.setDebugMode(debuggable);
		Utilities.setBasePath(getFilesDir() + "");
		new CopyAssetsTask(this).execute();
	}


	/** Private Classes */
	private class CopyAssetsTask extends AsyncTask<String, Void, Boolean> {
		private Context context;
		private ProgressDialog progressDialog;
		public CopyAssetsTask(Context context) {
			this.context = context;
		}
		@SuppressLint("InlinedApi")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			DebugLog.w(TAG, "onPreExecute");
			if (progressDialog == null) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					progressDialog = new ProgressDialog(context, android.R.style.Theme_Holo_Light_Panel);
				} else {
					progressDialog = new ProgressDialog(context);
				}
			}
			progressDialog.setCancelable(false);
			progressDialog.setCanceledOnTouchOutside(false);
			progressDialog.setMessage(context.getString(R.string.wait_msg));
			if (!progressDialog.isShowing()) {
				progressDialog.show();
			}
		}
		@Override
		protected Boolean doInBackground(String... parameters) {
			String savedAppVersion = SharedPreferencesWrapper.getVersion(context);
			String currentAppVersion = "0";
			try {
				currentAppVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			String updatedVersion = "0";
			boolean copy_flag = false;

			int versionFlag = Utilities.isNewerVersion(currentAppVersion, savedAppVersion);
			DebugLog.w(TAG, "versionFlag : "+versionFlag);
			switch(versionFlag) {
				case DOWNGRADED :
				case SAME:
					File wwwFolder = new File(Utilities.makePath(Constant.WWW_FOLDER));
					copy_flag = (!wwwFolder.exists())? true : false;
					updatedVersion = savedAppVersion;
					break;
				case UPGRADED:
					copy_flag = true;
					updatedVersion = currentAppVersion;
					break;
			}

			DebugLog.w(TAG, "savedAppVersion : "+savedAppVersion);
			DebugLog.w(TAG, "currentAppVersion : "+currentAppVersion);
			DebugLog.w(TAG, "updatedVersion : "+updatedVersion);

			SharedPreferencesWrapper.setVersion(context, updatedVersion);

			if (copy_flag) {
				DebugLog.w(TAG, "www folder is copied.");
				Utilities.copyAsset(getApplicationContext(), Constant.WWW_FOLDER, Constant.WWW_FOLDER);
			} else {
				DebugLog.w(TAG, "www folder is not copied.");
			}

			Utilities.copyAsset(getApplicationContext(), Constant.MEDIA_FOLDER);
			Utilities.setBasePath(getFilesDir() + "");
			if (Utilities.download_flg) {
				String serverAddress = SharedPreferencesWrapper.getServerAddress(context);
				String userId = SharedPreferencesWrapper.getUserId(context);;
				String password = SharedPreferencesWrapper.getPassword(context);
				DebugLog.e(TAG, "serverAddress : "+serverAddress);
				DebugLog.e(TAG, "userId : "+userId);
				DebugLog.e(TAG, "password : "+password);
				if (serverAddress.compareTo("") != 0 && userId.compareTo("") != 0) {
					Utilities.httpDownload("http://" + serverAddress + "/~" + userId + "/www.zip", "", userId, password);
				}
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			DebugLog.w(TAG, "onPostExecute");
			if (progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
			setResult(RESULT_OK, null);
			finish(); 
		}
	}
}

