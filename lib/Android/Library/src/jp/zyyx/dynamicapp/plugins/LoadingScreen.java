package jp.zyyx.dynamicapp.plugins;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.zyyx.dynamicapp.core.Plugin;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;
import jp.zyyx.dynamicapp.wrappers.JSONObjectWrapper;

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
public class LoadingScreen extends Plugin {
	private static final String TAG = "LoadingScreen";

	private static final int ERROR_DIALOG_NOT_SHOWN = 1;
	private static final int ERROR_DIALOG_UNSTOPPABLE = 2;
	
	private static final int STYLEWHITELARGE = 0;
	private static final int STYLEWHITE = 1;
	private static final int STYLEGRAY = 2;
	
	private static final int LARGE_SCALE = 1;
	private static final int MEDIUM_SCALE = 2;
	
	private static LoadingScreen instance = null;

	private Dialog progressDialog;
	private String msg = null;
	private String bgColor = null;
	private int iconStyle = 0;
	private int frameWidth = -1;
	private int frameHeight = -1;
	LinearLayout content = null;
	ProgressBar loaderIcon = null;
	
	private LoadingScreen() {
		super();
	}

	/**
	 * @return LoadingScreen instance
	 */
	public static synchronized LoadingScreen getInstance() {
		if (instance == null) {
			instance = new LoadingScreen();
		}
		return instance;
	}

	@Override
	public void execute() {
		DebugLog.w(TAG, "method " + methodName + " is executed.");
		DebugLog.w(TAG, "parameters are: " + params);

		if (methodName.equalsIgnoreCase("startLoad")) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.startLoad();
		} else if (methodName.equalsIgnoreCase("stopLoad")) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.stopLoad();
		}
	}

	/**
	 * show the progress dialog with custom label
	 */
	@SuppressLint("InlinedApi")
	public void startLoad() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			progressDialog = new Dialog(mainActivity, android.R.style.Theme_Holo_Light_Panel);
		} else {
			progressDialog = new Dialog(mainActivity, android.R.style.Theme_Translucent_NoTitleBar);
		}
		msg = param.get("label", "Loading...");
		bgColor = param.get("bgColor", "black");
		iconStyle = param.get("style", 0);

		String frameParam = param.get("frame", "");
		JSONObjectWrapper frameParams = null;

		try {
			frameParams = new JSONObjectWrapper(frameParam);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
		
			if(frameParams!= null) {
				WindowManager.LayoutParams layoutParams = progressDialog.getWindow().getAttributes();
				
				int x = Utilities.getDPI(frameParams.get("x", 0));
				int y = Utilities.getDPI(frameParams.get("y", 0));
				frameWidth = Utilities.getDPI(frameParams.get("width", 220));
				frameHeight = Utilities.getDPI(frameParams.get("height", 100));
				layoutParams.x = x;
				layoutParams.y = y;
				layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
				progressDialog.getWindow().setAttributes(layoutParams);
				progressDialog.addContentView(this.getCustomView(frameWidth, frameHeight), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			} else {
				WindowManager.LayoutParams layoutParams = progressDialog.getWindow().getAttributes();
				layoutParams.gravity = Gravity.CENTER;
				progressDialog.getWindow().setAttributes(layoutParams);
				progressDialog.addContentView(this.getCustomView(Utilities.getDPI(220), Utilities.getDPI(100)), new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}
		
			progressDialog.setCancelable(true);
			progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
			progressDialog.show();
			if(frameWidth != -1 && frameHeight != -1) {
				progressDialog.getWindow().setLayout(frameWidth, frameHeight);
			}else {
				progressDialog.getWindow().setLayout(Utilities.getDPI(220), Utilities.getDPI(100));
			}
			
			if (progressDialog.isShowing()) {
				LoadingScreen.onSuccess(new JSONObject(), callbackId, false);
			} else {
				LoadingScreen.onError(ERROR_DIALOG_NOT_SHOWN + "", callbackId);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public RelativeLayout getCustomView(int width, int height){
        RelativeLayout backLayout = new RelativeLayout(mainActivity);
        RelativeLayout frontLayout = new RelativeLayout(mainActivity);
        content = new LinearLayout(mainActivity);
        
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        
        TextView message = new TextView(mainActivity);
        message.setTextColor(Color.GRAY);
        message.setText(msg);
        message.setId(1);
      
        loaderIcon  = new ProgressBar(mainActivity);
        int color = -1;
        int pad2 = Utilities.getDPI(2);
        int pad5 = Utilities.getDPI(5);
        int pad10 = Utilities.getDPI(10);
        width = Utilities.getDPI(width);
        height = Utilities.getDPI(height);
        
        switch(iconStyle) {
        	case STYLEWHITELARGE: 
        		color = Color.parseColor("#FFFFFF");
        		if (Build.VERSION.SDK_INT >= 11) {
        			loaderIcon.setScaleX(1);
        			loaderIcon.setScaleY(1);
        		} else {
        			loaderIcon.setLayoutParams(setScale(LARGE_SCALE, width, height));
        		}
        		
        		message.setTextScaleX(1);
        		message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 25);
        		message.setPadding(pad5, pad5, 0, 0);
        		break;
        	case STYLEWHITE:
        		color = Color.parseColor("#FFFFFF");
        		break;
        	case STYLEGRAY:
        		color = Color.parseColor("#C0C0C0");
        		break;
        }
        
        if(iconStyle ==  STYLEWHITE || iconStyle ==  STYLEGRAY) {
        	if (Build.VERSION.SDK_INT >= 11) {
    			loaderIcon.setScaleX(.65f);
    			loaderIcon.setScaleY(.65f);
    			      		
        		message.setPadding(0, pad10, 0, 0);
    		} else {
    			loaderIcon.setLayoutParams(setScale(MEDIUM_SCALE, width, height));
        		message.setPadding(pad5, pad2, 0, 0);
    		}
        	message.setTextScaleX(1);
        	message.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
        }
        
        loaderIcon.getIndeterminateDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
        content.addView(loaderIcon);
        content.addView(message);
   
        frontLayout.addView(content,lp);
        backLayout.setPadding(pad5, pad5, pad5, pad5);
        backLayout.addView(frontLayout,lp2);
        
        GradientDrawable roundRect = new GradientDrawable ();
        roundRect.setShape(GradientDrawable.RECTANGLE);
        roundRect.setCornerRadii(new float[]{5.0f,5.0f,5.0f,5.0f,5.0f,5.0f,5.0f,5.0f});
        roundRect.setStroke(3, Color.GRAY);

        if(bgColor.equals("black") || bgColor.length() == 0) {
        	frontLayout.setBackgroundColor(Color.BLACK);
        	roundRect.setColor(Color.BLACK);
        } else {
        	frontLayout.setBackgroundColor(Color.WHITE);
        	roundRect.setColor(Color.WHITE);
        	message.setTextColor(Color.BLACK);
        	color = Color.parseColor("#C0C0C0");
        	loaderIcon.getIndeterminateDrawable().setColorFilter(color, android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        
        backLayout.setBackgroundDrawable(roundRect);
        
        return backLayout;
	}
	
	/**
	 * stops the currently shown progress dialog
	 */
	public void stopLoad() {
		progressDialog.dismiss();
		if (progressDialog.isShowing()) {
			LoadingScreen.onError(ERROR_DIALOG_UNSTOPPABLE + "", callbackId);
		} else {
			LoadingScreen.onSuccess(new JSONObject(), callbackId, false);
		}
	}
	
	private LinearLayout.LayoutParams setScale(int scaleType, int fWidth, int fHeight) {
		LinearLayout.LayoutParams scaled_param = null;
		
		int  mediumSize = Utilities.getDPI(30);
		int largeSize = Utilities.getDPI(50);		
		int width = Utilities.getDPI(fWidth - 10);
		int height = Utilities.getDPI(fHeight - 10);
		
		if(width > 0 && (width < mediumSize)) {
			mediumSize = width;
		}
		
		if(width > 0 && width < largeSize) {
			largeSize = width;
		}
		
		if(height > 0 && (height < mediumSize)) {
			mediumSize = height;
		}
		
		if(height > 0 && height < largeSize) {
			largeSize = height;
		}
		
		if(width < 0 || height < 0) {
			mediumSize = 0;
			largeSize = 0;
		}
		
		switch (scaleType) {
			case LARGE_SCALE:
				scaled_param = new LinearLayout.LayoutParams(largeSize, largeSize);
				break;
			case MEDIUM_SCALE:
				scaled_param = new LinearLayout.LayoutParams(mediumSize, mediumSize);
				break;
		}
		
		return scaled_param;
	}
	
}
