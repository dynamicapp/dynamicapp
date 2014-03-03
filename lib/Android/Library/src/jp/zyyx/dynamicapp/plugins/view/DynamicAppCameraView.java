package jp.zyyx.dynamicapp.plugins.view;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import jp.zyyx.dynamicapp.plugins.DynamicAppCamera;
import jp.zyyx.dynamicapp.plugins.activity.CameraActivity;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

/**
 * @author Zyyx
 * 
 */
public class DynamicAppCameraView extends SurfaceView implements Callback, PictureCallback {
	private static final String TAG = "DynamicAppCameraView";

	private static final int DESTINATION_TYPE_DATA_URL = 0;
	private static final int DESTINATION_TYPE_FILE_URI = 1;
	
	private Camera camera;
	private byte[] result;
	private CameraActivity activity;
	private float rotation;
	
	public int capturedOrientation = 0;
	
	/**
	 * @param context
	 *            CameraActivity context
	 */
	@SuppressWarnings("deprecation")
	public DynamicAppCameraView(Context context) {
		super(context);
		activity = (CameraActivity) context;
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		try {
			camera = Camera.open();
			camera.setPreviewDisplay(holder);

		} catch (IOException e) {
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Camera.Parameters p = camera.getParameters();
		if (Build.VERSION.SDK_INT >= 8)
			rotation = DynamicAppUtils.setCameraDisplayOrientation(activity, 0, camera);
		else {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
				p.set("orientation", "portrait");
				p.set("rotation", 90);
			}
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				p.set("orientation", "landscape");
				p.set("rotation", 90);
			}
		}
		
		p.setJpegQuality(activity.getQuality());
		camera.setParameters(p);
		camera.startPreview();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
	}

	@Override
	public void onPictureTaken(byte[] data, Camera c) {
		capturedOrientation = CameraActivity.orientation;
		this.result = data;
		CameraActivity.getSaveBtn().setEnabled(true);
		CameraActivity.getCancelBtn().setEnabled(true);
	}

	/**
	 * 
	 */
	public void capture() {
		camera.takePicture(null, null, this);
		
	}

	/**
	 * saves the captured image to android media storage
	 */
	public void save() {
		if (result != null) {
			Bitmap orientedBitmap = null;
			ByteArrayOutputStream bos = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            
			Bitmap bmp = BitmapFactory.decodeByteArray(this.result, 0,
					this.result.length, options);
			
			Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
			String mCapturedImageURI = null;
			
			if(isSDPresent)
    		{
				mCapturedImageURI = MediaStore.Images.Media.insertImage(getContext()
    					.getContentResolver(), bmp, "", null);
    		}
			
	        Matrix matrix = new Matrix();
	        
	        // image orientation when saved
	        if (capturedOrientation < 60) {
	            matrix.preRotate(90);
	        } else if(capturedOrientation >= 60 && capturedOrientation <180){
	        	matrix.preRotate(180);
	        } else if(capturedOrientation >= 180 && capturedOrientation < 270) {
	        	if(rotation != 0f)
	        		matrix.preRotate(270);
	        	else
	        		matrix.preRotate(0);
	        } else if(capturedOrientation >= 270) {
	        	matrix.preRotate(0);
	        }

	        int destinationType = activity.getDestinationType();
	        int quality = activity.getQuality();
			int encodingType = activity.getEncodingType();
			int targetWidth = activity.getTargetWidth();
			int targetHeight = activity.getTargetHeight();
			
			if((targetHeight > 0 && targetWidth > 0) && (targetHeight <= bmp.getHeight() && targetWidth <= bmp.getWidth())) {
				orientedBitmap = Bitmap.createBitmap(
		        		bmp, 0, 0, targetWidth, targetHeight, matrix, true);
			} else {
				orientedBitmap = Bitmap.createBitmap(
		        		bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			}
			
			Bitmap.CompressFormat format = (encodingType == 0) ? CompressFormat.JPEG :CompressFormat.PNG;
			
	        switch(destinationType) {
	        	case DESTINATION_TYPE_DATA_URL:
	        		bos = new ByteArrayOutputStream();
	    			orientedBitmap.compress(format, quality, bos);
	    			byte[] _bArray = bos.toByteArray();
	    			String contents = Base64.encodeToString(_bArray, Base64.DEFAULT);
	    			contents = "data:image/jpeg;base64," + contents;
	    			DynamicAppCamera.onSuccessResult(contents);
	    			
	        		break;
	        	case DESTINATION_TYPE_FILE_URI:
	        		if(mCapturedImageURI != null)
	        		{
	        			DebugLog.i(TAG, "image uri:"+ mCapturedImageURI);
		        		DynamicAppCamera.onSuccessResult(mCapturedImageURI);
	        		}
	        		else
	        		{
	        			DynamicAppCamera.onError("1");
	        		}

	        		if (orientedBitmap != null) {
	    				orientedBitmap.recycle();
	    				orientedBitmap = null;
	    	        }
	        		
	        		break;
	        }
	        
	        if (orientedBitmap != null) {
				orientedBitmap.recycle();
				orientedBitmap = null;
	        }
	        
	        if (bmp != null) {
	        	bmp.recycle();
	        	bmp = null;
	        }
	        
	        if (bos != null) {
	        	bos = null;
	        }
	        
			returnToParentActivity();
		}
	}

	/**
	 * cancels the camera activity
	 */
	public void cancel() {
		DynamicAppCamera.onCancel();
		returnToParentActivity();
	}

	/**
	 * destroys the camera activity and return
	 */
	public void returnToParentActivity() {
		//camera.stopPreview();
		CameraActivity.getInstance().finish();
	}
	
	public void retake() {
		camera.startPreview();
	}
}
