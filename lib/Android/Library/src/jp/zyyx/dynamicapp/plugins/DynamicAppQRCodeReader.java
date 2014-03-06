package jp.zyyx.dynamicapp.plugins;

import android.app.Activity;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import jp.zyyx.dynamicapp.core.Plugin;
import jp.zyyx.dynamicapp.plugins.activity.QRScannerActivity;
import jp.zyyx.dynamicapp.plugins.zxing.RGBLuminanceSource;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;

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
public class DynamicAppQRCodeReader extends Plugin {
	private static final String TAG = "DynamicAppQRCodeReader";

	private static final int ERROR_NO_MATCH_FOUND = 1;
	private static DynamicAppQRCodeReader instance = null;
	private static final int  SCAN_FROM_CAMERA = 0;
	
	private DynamicAppQRCodeReader() {
		super();
	}

	/**
	 * @return DynamicAppQRCodeReader instance
	 */
	public static synchronized DynamicAppQRCodeReader getInstance() {
		if (instance == null) {
			instance = new DynamicAppQRCodeReader();
		}
		return instance;
	}

	public void init(String methodName, String params, String callbackId) {
		super.init(methodName, params, callbackId);
	}

	@Override
	public void execute() {
		DebugLog.w(TAG, "method " + methodName + " is executed.");
		DebugLog.w(TAG, "parameters are: " + params);
		int source = param.get("source", 1);
		
		mainActivity.callJsEvent(PROCESSING_FALSE);
		if (methodName.equalsIgnoreCase("scan")) {
			mainActivity.callJsEvent(PROCESSING_FALSE);

			if (source == SCAN_FROM_CAMERA) {
				PackageManager pm = mainActivity.getPackageManager();
				
				if(!Utilities.isEmulator() && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
					Intent intent = new Intent(mainActivity, QRScannerActivity.class);
					mainActivity.startActivity(intent);
				} else {
					mainActivity.callJsEvent(PROCESSING_FALSE);
				}
				
			} else {	
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				mainActivity.startActivityForResult(
						Intent.createChooser(intent, "Select Picture"), 2);
			}
		} else if (methodName.equalsIgnoreCase("executeQRCodeResult")) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
		}

	}

	/**
	 * @param scanResult
	 *            zxing scan result
	 */
	public static void onSuccessResult(Result scanResult) {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		DynamicAppQRCodeReader.onSuccess(scanResult, callbackId, false);
	}

	/**
	 * @param scanResult
	 *            string type scan result
	 */
	public static void onSuccessResult(String scanResult) {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		DynamicAppQRCodeReader.onSuccess(scanResult, callbackId, false);
	}

	/**
	 * call javascript error callback
	 */
	public static void onErrorResult() {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		DynamicAppQRCodeReader.onError(ERROR_NO_MATCH_FOUND + "", callbackId);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		final int RESULT_OK = Activity.RESULT_OK;
		
		if (requestCode == ACTIVITY_REQUEST_CD_QR) {
			if (resultCode == RESULT_OK) {
				Uri selectedImage = intent.getData();
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};

	            Cursor cursor = mainActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
	            cursor.moveToFirst();

	            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
	            String filePath = cursor.getString(columnIndex);
	            cursor.close();
	            
	            BitmapFactory.Options options = new BitmapFactory.Options();
	            options.inSampleSize = 8;
	            
	            Bitmap bMap = BitmapFactory.decodeFile(filePath, options);
	            LuminanceSource source = new RGBLuminanceSource(bMap);
	            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

	            MultiFormatReader reader = new MultiFormatReader();
	            Result scanResult = null;
	            try {
	            	scanResult = reader.decodeWithState(bitmap);
	            } catch (ReaderException re) {
	            	// continue
	            } finally {
	            	reader.reset();
	            }
	            
	            if (scanResult != null) {
	            	DynamicAppQRCodeReader.onSuccessResult(scanResult);
	            } else {
	            	DynamicAppQRCodeReader.onErrorResult();
	            }
	            
	            if (bMap != null) {
		        	bMap.recycle();
		        	bMap = null;
		        }
	            System.gc();
			}
		}
	}
}
