package jp.zyyx.dynamicapp.plugins;

import java.io.ByteArrayOutputStream;
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

import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.plugins.activity.CameraActivity;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;

/**
 * @author Zyyx
 *
 */
public class DynamicAppCamera extends DynamicAppPlugin {
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
	
    private DynamicAppCamera() {}

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
		DebugLog.i(TAG, "method " + methodName + " is called.");
		DebugLog.i(TAG, "parameters are: " + params);

		if (methodName.equalsIgnoreCase("getPicture")) {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			this.getPicture();
		} else if(methodName.equalsIgnoreCase("recordVideo")) {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
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
		
		DebugLog.i(TAG, "source type:" + sourceType);
		
		switch(sourceType) {
			case SOURCE_TYPE_PHOTOLIBRARY:
			case SOURCE_TYPE_SAVEDPHOTOALBUM:
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				dynamicApp.startActivityForResult(
						Intent.createChooser(intent, "Select Picture"), 1);
				break;
			case SOURCE_TYPE_CAMERA:
				Intent intent2 = new Intent(dynamicApp, CameraActivity.class);
				intent2.putExtra("quality", quality);
				intent2.putExtra("destinationType", destinationType);
				intent2.putExtra("sourceType", sourceType);
				intent2.putExtra("targetHeight", targetHeight);
				intent2.putExtra("targetWidth", targetWidth);
				intent2.putExtra("encodingType", encodingType);
				dynamicApp.startActivity(intent2);
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
	    final PackageManager packageManager = dynamicApp.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list =
	        packageManager.queryIntentActivities(intent,
	            PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	private void dispatchTakeVideoIntent() {
	    Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
	    dynamicApp.startActivityForResult(takeVideoIntent, ACTIVITY_REQUEST_CD_TAKE_VIDEO);
	}
	
	private void handleCameraVideo(Intent intent) {
		dynamicApp.callJsEvent(PROCESSING_FALSE);
	    Uri mVideoUri = intent.getData();
	    DebugLog.i(TAG, "video uri: " + mVideoUri);
	    String videoID =  mVideoUri.getPathSegments().get(3);
	    DebugLog.i(TAG, "videoID: " + videoID);

	    onSuccess(mVideoUri.toString(), callbackId, false);
	}
	
	
	/**
	 * @param data	the base64 image data result from captured image
	 */
	public static void onSuccessResult(String data) {
		DynamicAppCamera.onSuccess(data, callbackId, false);
		dynamicApp.callJsEvent(PROCESSING_FALSE);
	}
	
	public static void onError(String data) {
		DynamicAppCamera.onError(data , callbackId);
		dynamicApp.callJsEvent(PROCESSING_FALSE);
	
	}
	
	/**
	 * 
	 */
	public static void onCancel() {
		dynamicApp.callJsEvent(PROCESSING_FALSE);
	}
	
	@Override
	public void onBackKeyDown() {
		DynamicAppUtils.currentCommandRef = null;
		instance = null;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		final int RESULT_OK = Activity.RESULT_OK;
		
		if (requestCode == ACTIVITY_REQUEST_CD_CAMERA) {
			if (resultCode == RESULT_OK) {
				CompressFormat format = (DynamicAppCamera.encodingType == 0) ? CompressFormat.JPEG : CompressFormat.PNG;
				
				Uri selectedImage = intent.getData();
				ByteArrayOutputStream bos = null;
				Bitmap bMap = null;
				
		        switch(destinationType) {
		        	case DESTINATION_TYPE_DATA_URL:
		        		selectedImage = intent.getData();
			            String[] filePathColumn = {MediaStore.Images.Media.DATA};
	
			            Cursor cursor = dynamicApp.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
			            cursor.moveToFirst();
	
			            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			            String filePath = cursor.getString(columnIndex);
			            cursor.close();
			            
			            bMap = getScaledPic(filePath);
			            
			            bos = new ByteArrayOutputStream();
		        		bMap.compress(format, quality, bos);
		    			byte[] _bArray = bos.toByteArray();
		    			String contents = Base64.encodeToString(_bArray, Base64.DEFAULT);
		    			contents = "data:image/jpeg;base64," + contents;
		    			DynamicAppCamera.onSuccessResult(contents);
		    			
		        		break;
		        	case DESTINATION_TYPE_FILE_URI:
		        		DynamicAppCamera.onSuccessResult(selectedImage.toString());
		        		
		        		break;
		        }
		        
		        if (bMap != null) {
		        	bMap.recycle();
		        	bMap = null;
		        }
		        
		        if (bos != null) {
		        	bos = null;
		        }
		        
		        System.gc();
			}
		} else if(requestCode == ACTIVITY_REQUEST_CD_TAKE_VIDEO) {
			if(resultCode == RESULT_OK) {
				handleCameraVideo(intent);
			} else {
				dynamicApp.callJsEvent(PROCESSING_FALSE);
			}
		}
	}
	
	public static Bitmap getScaledPic(String photoPath) {
	    int targetW = targetWidth;
	    int targetH = targetHeight;

	    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
	    bmOptions.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(photoPath, bmOptions);
	    int photoW = bmOptions.outWidth;
	    int photoH = bmOptions.outHeight;

	    int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
	  
	    bmOptions.inJustDecodeBounds = false;
	    bmOptions.inSampleSize = scaleFactor;
	    bmOptions.inPurgeable = true;
	  
	    Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
	    
	    return bitmap;
	}
}
