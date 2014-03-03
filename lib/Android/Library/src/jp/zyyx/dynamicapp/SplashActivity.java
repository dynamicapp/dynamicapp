package jp.zyyx.dynamicapp;

import java.io.File;

import jp.zyyx.dynamicapp.utilities.Constant;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;
import jp.zyyx.dynamicapp.wrappers.SharedPreferencesWrapper;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

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

		DynamicAppUtils.fixDisplayOrientation(this);

        ImageView imageView = new ImageView(this);
		Intent intent = getIntent();
		int resourceId = intent.getIntExtra(DynamicAppActivity.KEY_RESOURCE_ID, 0);
		if (resourceId != 0) {
			imageView.setImageResource(resourceId);
		}
		imageView.setAdjustViewBounds(false);
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		setContentView(imageView);

		DynamicAppUtils.setBasePath(getFilesDir() + "");
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

			int versionFlag = DynamicAppUtils.isNewerVersion(currentAppVersion, savedAppVersion);
			DebugLog.i(TAG, "versionFlag : "+versionFlag);
			switch(versionFlag) {
				case DOWNGRADED :
				case SAME:
					File wwwFolder = new File(DynamicAppUtils.makePath(Constant.WWW_FOLDER));
					copy_flag = (!wwwFolder.exists())? true : false;
					updatedVersion = savedAppVersion;
					break;
				case UPGRADED:
					copy_flag = true;
					updatedVersion = currentAppVersion;
					break;
			}

			DebugLog.i(TAG, "savedAppVersion : "+savedAppVersion);
			DebugLog.i(TAG, "currentAppVersion : "+currentAppVersion);
			DebugLog.i(TAG, "updatedVersion : "+updatedVersion);

			SharedPreferencesWrapper.setVersion(context, updatedVersion);

			if (copy_flag) {
				DebugLog.i(TAG, "www folder is copied.");
				DynamicAppUtils.copyAsset(getApplicationContext(), Constant.WWW_FOLDER, Constant.WWW_FOLDER);
			} else {
				DebugLog.i(TAG, "www folder is not copied.");
			}

			DynamicAppUtils.copyAsset(getApplicationContext(), Constant.MEDIA_FOLDER);
			DynamicAppUtils.setBasePath(getFilesDir() + "");
			if (DynamicAppUtils.download_flg) {
				String serverAddress = SharedPreferencesWrapper.getServerAddress(context);
				String userId = SharedPreferencesWrapper.getUserId(context);;
				String password = SharedPreferencesWrapper.getPassword(context);
				DebugLog.e(TAG, "serverAddress : "+serverAddress);
				DebugLog.e(TAG, "userId : "+userId);
				DebugLog.e(TAG, "password : "+password);
				if (serverAddress.compareTo("") != 0 && userId.compareTo("") != 0) {
					DynamicAppUtils.httpDownload("http://" + serverAddress + "/~" + userId + "/www.zip", "", userId, password);
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

