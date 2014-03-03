package jp.zyyx.dynamicapp.plugins.activity;

import jp.zyyx.dynamicapp.plugins.DynamicAppQRCodeReader;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.zxing.Result;
import com.google.zxing.client.android.CaptureActivity;

/**
 * <p>
 * Starts the QR Code scanning activity
 * </p>
 * 
 * <p>
 * Extends from zxing CaptureActivity class
 * and handle decode by passing result to 
 * onSuccessResult handler
 * </p>
 * 
 * @author 		Zyyx
 * @version     %I%, %G%
 * @since       1.0
 * 
 */
public class QRScannerActivity extends CaptureActivity 
{

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    @Override 
    public void handleDecode(Result rawResult, Bitmap barcode) 
    {
    	// passes result to a javascript success callback
    	DynamicAppQRCodeReader.onSuccessResult(rawResult.getText());
    	this.finish();
    }
}
