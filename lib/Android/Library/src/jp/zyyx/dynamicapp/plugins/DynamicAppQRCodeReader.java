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

import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.plugins.activity.QRScannerActivity;
import jp.zyyx.dynamicapp.plugins.zxing.RGBLuminanceSource;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;

/**
 * @author Zyyx
 * 
 */
public class DynamicAppQRCodeReader extends DynamicAppPlugin {
	private static final String TAG = "DynamicAppQRCodeReader";

	private static final int ERROR_NO_MATCH_FOUND = 1;
	private static DynamicAppQRCodeReader instance = null;
	private static final int  SCAN_FROM_CAMERA = 0;
	
	private DynamicAppQRCodeReader() {
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
		DebugLog.i(TAG, "method " + methodName + " is executed.");
		DebugLog.i(TAG, "parameters are: " + params);
		int source = param.get("source", 1);
		
		dynamicApp.callJsEvent(PROCESSING_FALSE);
		if (methodName.equalsIgnoreCase("scan")) {
			dynamicApp.callJsEvent(PROCESSING_FALSE);

			if (source == SCAN_FROM_CAMERA) {
				PackageManager pm = dynamicApp.getPackageManager();
				
				if(!DynamicAppUtils.isEmulator() && pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
					Intent intent = new Intent(dynamicApp, QRScannerActivity.class);
					dynamicApp.startActivity(intent);
				} else {
					dynamicApp.callJsEvent(PROCESSING_FALSE);
				}
				
			} else {	
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				dynamicApp.startActivityForResult(
						Intent.createChooser(intent, "Select Picture"), 2);
			}
		} else if (methodName.equalsIgnoreCase("executeQRCodeResult")) {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
		}

	}

	/**
	 * @param scanResult
	 *            zxing scan result
	 */
	public static void onSuccessResult(Result scanResult) {
		dynamicApp.callJsEvent(PROCESSING_FALSE);
		DynamicAppQRCodeReader.onSuccess(scanResult, callbackId, false);
	}

	/**
	 * @param scanResult
	 *            string type scan result
	 */
	public static void onSuccessResult(String scanResult) {
		dynamicApp.callJsEvent(PROCESSING_FALSE);
		DynamicAppQRCodeReader.onSuccess(scanResult, callbackId, false);
	}

	/**
	 * call javascript error callback
	 */
	public static void onErrorResult() {
		dynamicApp.callJsEvent(PROCESSING_FALSE);
		DynamicAppQRCodeReader.onError(ERROR_NO_MATCH_FOUND + "", callbackId);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		final int RESULT_OK = Activity.RESULT_OK;
		
		if (requestCode == ACTIVITY_REQUEST_CD_QR) {
			if (resultCode == RESULT_OK) {
				Uri selectedImage = intent.getData();
	            String[] filePathColumn = {MediaStore.Images.Media.DATA};

	            Cursor cursor = dynamicApp.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
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
