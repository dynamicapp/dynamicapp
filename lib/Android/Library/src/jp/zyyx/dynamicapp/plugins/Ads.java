package jp.zyyx.dynamicapp.plugins;

import org.json.JSONObject;

import com.google.ads.*;
import com.google.ads.AdRequest.ErrorCode;

import android.content.res.Configuration;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import jp.zyyx.dynamicapp.DynamicAppActivity;
import jp.zyyx.dynamicapp.core.Plugin;
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
public class Ads extends Plugin implements AdListener {
	private static final String TAG = "Ads";

	private static Ads instance = null;
	private static final String METHOD_CREATE = "create";
	private static final String METHOD_SHOW = "show";
	private static final String METHOD_HIDE = "hide";
	private static final int POSITION_TOP = 0;

	private int position = POSITION_TOP;
	private String AD_UNIT_ID = "";
	private AdView adView = null;
	private AdView adView_PORTRAIT = null;
	private AdView adView_LANDSCAPE = null;
	private LinearLayout adLayout = null;
	private boolean isShown = false;
	private boolean isAdded = false;
	private boolean isCreated = false;
	
	AdRequest request;

	private Ads() {
		super();
	}

	public static synchronized Ads getInstance() {
		if (instance == null) {
			instance = new Ads();
	    }
	    return instance;
	}

	@Override
	public void execute() {
		DebugLog.w(TAG, "method " + methodName + " is called.");
		DebugLog.w(TAG, "parameters are: " + params);

		if (methodName.equalsIgnoreCase(METHOD_CREATE)) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.createAd();
		} else if(methodName.equalsIgnoreCase(METHOD_SHOW)) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.showAd();
		} else if(methodName.equalsIgnoreCase(METHOD_HIDE)) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.hideAd();
		}
	}

	private void createAdContainer() {
		DebugLog.w(TAG, "createAdContainer");
		adLayout = new LinearLayout(mainActivity);
		adLayout.setId(1234);
		adLayout.setVisibility(View.VISIBLE);
		adLayout.setBackgroundColor(Color.BLACK);
	}
	
	private void layoutAdContainer() {
		DebugLog.w(TAG, "layoutAdContainer");
		int orientation = this.getScreenOrientation();
		
		float density = mainActivity.getResources().getDisplayMetrics().density;
		int heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
		
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			heightOriention = AdSize.LANDSCAPE_AD_HEIGHT;
	    } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
	    	heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
	    }

		int height = Math.round(heightOriention  * density);
		if (position == POSITION_TOP) {
			DynamicAppActivity.mainLayout.removeAllViews();
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
			DynamicAppActivity.mainLayout.addView(adLayout, params);
			DynamicAppActivity.mainLayout.addView(mainActivity.getWebView(), this.getPosition());
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getScreenHeight() - height);
			mainActivity.getWebView().setLayoutParams(params);
			DynamicAppActivity.mainLayout.addView(adLayout, this.getPosition());
		}
		
		DynamicAppActivity.mainLayout.requestLayout();
		DynamicAppActivity.mainLayout.invalidate();
	}
	
	private void setAdInstance() {
		DebugLog.w(TAG, "setAdInstance");
		int orientation = this.getScreenOrientation();

		if(Configuration.ORIENTATION_LANDSCAPE == orientation &&
				null != adView_LANDSCAPE) {
			DebugLog.w(TAG, "Ad: orientation -> landscape");
			DebugLog.w(TAG, "Ad: adView -> has value");
			adView = adView_LANDSCAPE;
		} else if(Configuration.ORIENTATION_PORTRAIT == orientation &&
				null != adView_PORTRAIT) {
			DebugLog.w(TAG, "Ad: orientation -> portrait");
			DebugLog.w(TAG, "Ad: adView -> has value");
			adView = adView_PORTRAIT;
		} else {
			DebugLog.w(TAG, "Ad: adView -> creating...");
			adView = new AdView(mainActivity, AdSize.SMART_BANNER, AD_UNIT_ID);
			adView.setAdListener(this);
			
			if(Configuration.ORIENTATION_LANDSCAPE == orientation)
				adView_LANDSCAPE = adView;
			else
				adView_PORTRAIT = adView;
		}
		
		adLayout.removeView(adView);
		adLayout.removeAllViews();
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
		adLayout.addView(adView, params1);
		DebugLog.w(TAG, "Ad is added");
		adView.loadAd(request);
		DebugLog.w(TAG, "Ad is loaded");
	}
	
	private void createAd() {
		DebugLog.w(TAG, "createAd");
		// get screen orientation
		String AD_UNIT_DYNAMICAPP = "a14e62fe30c05d7";
		mainActivity.getWebView().setId(4567);
		position = param.get("position", 0);
		request = new AdRequest();
		
		createAdContainer();
		layoutAdContainer();
		isAdded = true;
		
		AD_UNIT_ID = param.get("id", "");
		if(AD_UNIT_ID.length() <= 0)
			AD_UNIT_ID = AD_UNIT_DYNAMICAPP;
		
		if(Utilities.isEmulator())
			request.addTestDevice(AdRequest.TEST_EMULATOR);

		setAdInstance();
		isCreated = true;
	}
	
	private void showAd() {
		DebugLog.w(TAG, "showAd width: " + adView.getWidth());
		if(adView.isReady()) {
			float density = mainActivity.getResources().getDisplayMetrics().density;
			int heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
			int orientation = this.getScreenOrientation();
			
			if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				DebugLog.w(TAG, "orientation: ORIENTATION_LANDSCAPE");
				heightOriention = AdSize.LANDSCAPE_AD_HEIGHT;
		    } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
		    	DebugLog.w(TAG, "orientation: ORIENTATION_PORTRAIT");
		    	heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
		    }
			
			int height = Math.round(heightOriention  * density);
			if(position == POSITION_TOP) {
				DynamicAppActivity.mainLayout.removeAllViews();
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
				DynamicAppActivity.mainLayout.addView(adLayout, params);
				DynamicAppActivity.mainLayout.addView(mainActivity.getWebView(), this.getPosition());
			} else {
				if(!isAdded) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getScreenHeight() - height);
					mainActivity.getWebView().setLayoutParams(params);
					DynamicAppActivity.mainLayout.addView(adLayout, this.getPosition());
				}
			}
			
			DynamicAppActivity.mainLayout.requestLayout();
			DynamicAppActivity.mainLayout.invalidate();
			
			adLayout.setVisibility(View.VISIBLE);
			isShown = true;
			if(adView.isShown())
				Ads.onSuccess(new JSONObject(), callbackId, false);
			else
				Ads.onError("", callbackId);
		} else {
			Ads.onError("", callbackId);
		}
	}
	
	private void hideAd() {
		DebugLog.w(TAG, "hideAd");
		if(adLayout.isShown()) {
			adLayout.setVisibility(View.GONE);
			DynamicAppActivity.mainLayout.removeView(adLayout);

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			mainActivity.getWebView().setLayoutParams(params);
			
			DynamicAppActivity.mainLayout.requestLayout();
			DynamicAppActivity.mainLayout.invalidate();
			
			isShown = false;
			isAdded = false;
			if(!adView.isShown())
				Ads.onSuccess(new JSONObject(), callbackId, false);
			else
				Ads.onError("", callbackId);
		} else {
			Ads.onError("", callbackId);
		}
	}
	
//	private LinearLayout getAdLayout() {
//		return this.adLayout;
//	}
	
	private RelativeLayout.LayoutParams getPosition() {
		DebugLog.w(TAG, "getPosition");
		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if(position == POSITION_TOP)
			relativeParams.addRule(RelativeLayout.BELOW, adLayout.getId());
		else
			relativeParams.addRule(RelativeLayout.BELOW, mainActivity.getWebView().getId());
		
		return relativeParams;
	}
	
	/** Called when an ad is clicked and about to return to the application. */
	@Override
	public void onDismissScreen(Ad ad) {
		DebugLog.w(TAG, "onDismissScreen ad:" + ad.toString() + "***");
	}

	/** Called when an ad was not received. */
	@Override
	public void onFailedToReceiveAd(Ad ad, ErrorCode errorCode) {
		DebugLog.e(TAG, "onFailedToReceiveAd errorCode:" + errorCode.name() + "***");
		if(adLayout.isShown())
			adLayout.setVisibility(View.GONE);
		Ads.onError("", callbackId);
	}

	/**
	 * Called when an ad is clicked and going to start a new Activity that will
	 * leave the application (e.g. breaking out to the Browser or Maps
	 * application).
	 */
	@Override
	public void onLeaveApplication(Ad ad) {
		DebugLog.w(TAG, "onLeaveApplication ad:" + ad.toString() + "***");
	}

	/**
	 * Called when an Activity is created in front of the app (e.g. an
	 * interstitial is shown, or an ad is clicked and launches a new Activity).
	 */
	@Override
	public void onPresentScreen(Ad ad) {
		DebugLog.w(TAG, "onPresentScreen ad:" + ad.toString() + "***");
	}

	/** Called when an ad is received. */
	@Override
	public void onReceiveAd(Ad ad) {
		DebugLog.w(TAG, "onReceiveAd ad:" + ad.toString() + "***");
		Ads.onSuccess(new JSONObject(), callbackId, false);
	}

	public int getScreenHeight() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		mainActivity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		DebugLog.w(TAG, "screen height: " + displaymetrics.heightPixels);
		
		return displaymetrics.heightPixels;
	}
	
	@Override
	public void onConfigurationChanged(Configuration config) {
		if(isCreated) { 
			this.setAdInstance();
	
			float density = mainActivity.getResources().getDisplayMetrics().density;
			int heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
			
			if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				DebugLog.w(TAG, "orientation: ORIENTATION_LANDSCAPE");
				heightOriention = AdSize.LANDSCAPE_AD_HEIGHT;
		    } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
		    	DebugLog.w(TAG, "orientation: ORIENTATION_PORTRAIT");
		    	heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
		    }
			
			int height = Math.round(heightOriention  * density);
			
			if(position == POSITION_TOP && isShown) {
				DynamicAppActivity.mainLayout.removeAllViews();
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
				DynamicAppActivity.mainLayout.addView(adLayout, params);
				DynamicAppActivity.mainLayout.addView(mainActivity.getWebView(), this.getPosition());
			} else {
				int webPageHeight = getScreenHeight() - height;
				DebugLog.w(TAG, "Ad screen height: " + webPageHeight);
				DebugLog.w(TAG, "Ad webpage height: " + webPageHeight);
				DebugLog.w(TAG, "Ad height: " + height);
				if(isShown) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, webPageHeight);
					mainActivity.getWebView().setLayoutParams(params);
				}
			}
		
			DynamicAppActivity.mainLayout.requestLayout();
			DynamicAppActivity.mainLayout.invalidate();
			
			if(isShown) {
				adLayout.setVisibility(View.VISIBLE);
				isShown = true;
			}
		}
	}
	
	@Override
	public void onDestroy() {
		System.out.println("on ad destroy");
	    if (adView != null) {
	      adView.destroy();
	      isCreated = false;
	    }
	    super.onDestroy();
	}
	
	private int getScreenOrientation() {
		return mainActivity.getResources().getConfiguration().orientation;
	}

}
