package jp.zyyx.dynamicapp.plugins;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import jp.zyyx.dynamicapp.core.Plugin;
import jp.zyyx.dynamicapp.plugins.activity.CameraActivity;
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
public class DynamicAppCamera extends Plugin {
	private static final String TAG = "DynamicAppCamera";

	private static final int SOURCE_TYPE_PHOTOLIBRARY = 0;
	private static final int SOURCE_TYPE_CAMERA = 1;
	private static final int SOURCE_TYPE_SAVEDPHOTOALBUM = 2;
	
	private static final int DESTINATION_TYPE_DATA_URL = 0;
	private static final int DESTINATION_TYPE_FILE_URI = 1;
	
	private static DynamicAppCamera instance = null;

	public static int quality = 100;
	public static int destinationType = 0;
	
	public static int targetHeight = -1;
	public static int targetWidth = -1;
	public static int encodingType = 0;
	
	private DynamicAppCamera() {
		super();
	}

    /**
     * @return	DynamicAppCamera instance
     */
    public static synchronized DynamicAppCamera getInstance() {
            if (instance == null) {
                    instance = new DynamicAppCamera();
            }
           
            return instance;
    }
     
	@Override
	public void execute() {
		DebugLog.w(TAG, "method " + methodName + " is called.");
		DebugLog.w(TAG, "parameters are: " + params);

		if (methodName.equalsIgnoreCase("getPicture")) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.getPicture();
		} else if(methodName.equalsIgnoreCase("recordVideo")) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.captureVideo();
		}
	}

	/**
	 * Getting the picture from Camera or Library.
	 */
	public void getPicture() {
		quality = param.get("quality", 1);
		destinationType = param.get("destinationType", 1);
		targetHeight = param.get("targetHeight", 90);
		targetWidth = param.get("targetWidth", 90);
		encodingType = param.get("encodingType", 0);
		int sourceType = param.get("sourceType", 1);
		
		DebugLog.w(TAG, "source type:" + sourceType);
		
		switch(sourceType) {
			case SOURCE_TYPE_PHOTOLIBRARY:
			case SOURCE_TYPE_SAVEDPHOTOALBUM:
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				mainActivity.startActivityForResult(
						Intent.createChooser(intent, "Select Picture"), 1);
				break;
			case SOURCE_TYPE_CAMERA:
				Intent intent2 = new Intent(mainActivity, CameraActivity.class);
				intent2.putExtra("quality", quality);
				intent2.putExtra("destinationType", destinationType);
				intent2.putExtra("sourceType", sourceType);
				intent2.putExtra("targetHeight", targetHeight);
				intent2.putExtra("targetWidth", targetWidth);
				intent2.putExtra("encodingType", encodingType);
				mainActivity.startActivity(intent2);
				break;
		}
	}
	
	public void captureVideo() {
		if(isIntentAvailable(MediaStore.ACTION_VIDEO_CAPTURE))
			dispatchTakeVideoIntent();
		else {
			DebugLog.e(TAG, "no available video app");
			DynamicAppCamera.onError("2");
		}
	}
	
	public static boolean isIntentAvailable(String action) {
	    final PackageManager packageManager = mainActivity.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	        packageManager.queryIntentActivities(intent,
	            PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	private void dispatchTakeVideoIntent() {
	    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	    mainActivity.startActivityForResult(takeVideoIntent, ACTIVITY_REQUEST_CD_TAKE_VIDEO);
	}
	
	private void handleCameraVideo(Intent intent) {
		mainActivity.callJsEvent(PROCESSING_FALSE);
	    Uri mVideoUri = intent.getData();
	    DebugLog.w(TAG, "video uri: " + mVideoUri);
	    String videoID =  mVideoUri.getPathSegments().get(3);
	    DebugLog.w(TAG, "videoID: " + videoID);

	    onSuccess(mVideoUri.toString(), callbackId, false);
	}
	
	
	/**
	 * @param data	the base64 image data result from captured image
	 */
	public static void onSuccessResult(String data) {
		DynamicAppCamera.onSuccess(data, callbackId, false);
		mainActivity.callJsEvent(PROCESSING_FALSE);
	}
	
	public static void onError(String data) {
		DynamicAppCamera.onError(data , callbackId);
		mainActivity.callJsEvent(PROCESSING_FALSE);
	
	}
	
	/**
	 * 
	 */
	public static void onCancel() {
		mainActivity.callJsEvent(PROCESSING_FALSE);
	}
	
	@Override
	public void onBackKeyDown() {
		Utilities.currentCommand = null;
		instance = null;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == ACTIVITY_REQUEST_CD_CAMERA) {
			if (resultCode == Activity.RESULT_OK) {
				CompressFormat format = (DynamicAppCamera.encodingType == 0) ? CompressFormat.JPEG : CompressFormat.PNG;
				
				Uri selectedImage = intent.getData();
		        switch(destinationType) {
		        	case DESTINATION_TYPE_DATA_URL:
		        		selectedImage = intent.getData();
			            String[] filePathColumn = {MediaStore.Images.Media.DATA};
	
			            Cursor cursor = mainActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			            cursor.moveToFirst();
	
			            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			            String filePath = cursor.getString(columnIndex);
			            cursor.close();

		    			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		    			Bitmap bitmap = null;
			            try {
			    			bitmap = BitmapFactory.decodeStream(new FileInputStream(filePath));
			        		bitmap.compress(format, quality, outputStream);

				            bitmap = getScaledPic(outputStream.toByteArray());
							bitmap.compress(format, 100, outputStream);
							byte[] data = outputStream.toByteArray();

			    			String contents = Base64.encodeToString(data, Base64.DEFAULT);
			    			contents = "data:image/jpeg;base64," + contents;
			    			DynamicAppCamera.onSuccessResult(contents);
			            } catch (FileNotFoundException e) {
			            	e.printStackTrace();
			            } finally {
					        if (bitmap != null) {
					        	bitmap.recycle();
					        	bitmap = null;
					        }
			    			try {
								outputStream.flush();
							} catch (IOException e) {
								e.printStackTrace();
							}
			            }
		        		break;
		        	case DESTINATION_TYPE_FILE_URI:
		        		DynamicAppCamera.onSuccessResult(selectedImage.toString());
		        		break;
		        }
		        System.gc();
			}
		} else if(requestCode == ACTIVITY_REQUEST_CD_TAKE_VIDEO) {
			if(resultCode == Activity.RESULT_OK) {
				handleCameraVideo(intent);
			} else {
				mainActivity.callJsEvent(PROCESSING_FALSE);
			}
		}
	}
	
	public static Bitmap getScaledPic(byte[] image) {
	    int targetW = targetWidth;
	    int targetH = targetHeight;

	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeByteArray(image, 0, image.length, bmOptions);

	    int scaleFactor = Math.min(bmOptions.outWidth/targetW, bmOptions.outHeight/targetH);
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	    return BitmapFactory.decodeByteArray(image, 0, image.length, bmOptions);
	}
}
