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
import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;

public class Ad extends DynamicAppPlugin implements AdListener {
	private static final String TAG = "Ad";

	private static Ad instance = null;
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
	
	private Ad() {}
	
	public static synchronized Ad getInstance() {
        if (instance == null) {
	            instance = new Ad();
	    }
	    return instance;
	}
	
	@Override
	public void execute() {
		DebugLog.i(TAG, "method " + methodName + " is called.");
		DebugLog.i(TAG, "parameters are: " + params);

		if (methodName.equalsIgnoreCase(METHOD_CREATE)) {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			this.createAd();
		} else if(methodName.equalsIgnoreCase(METHOD_SHOW)) {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			this.showAd();
		} else if(methodName.equalsIgnoreCase(METHOD_HIDE)) {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			this.hideAd();
		}
	}
	
	private void createAdContainer() {
		adLayout = new LinearLayout(dynamicApp);
		adLayout.setId(1234);
		adLayout.setVisibility(View.VISIBLE);
		adLayout.setBackgroundColor(Color.BLACK);
	}
	
	private void layoutAdContainer() {
		int orientation = this.getScreenOrientation();
		
		float density = dynamicApp.getResources().getDisplayMetrics().density;
		int heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
		
		if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
			heightOriention = AdSize.LANDSCAPE_AD_HEIGHT;
	    } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
	    	heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
	    }
		
		int height = Math.round(heightOriention  * density);
		
		if(position == POSITION_TOP) {
			DynamicAppActivity.layoutWithAd.removeAllViews();
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
			DynamicAppActivity.layoutWithAd.addView(adLayout, params);
			DynamicAppActivity.layoutWithAd.addView(dynamicApp.getWebView(), this.getPosition());
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getScreenHeight() - height);
			dynamicApp.getWebView().setLayoutParams(params);
			DynamicAppActivity.layoutWithAd.addView(adLayout, this.getPosition());
		}
		
		DynamicAppActivity.layoutWithAd.requestLayout();
		DynamicAppActivity.layoutWithAd.invalidate();
	}
	
	private void setAdInstance() {
		int orientation = this.getScreenOrientation();

		if(Configuration.ORIENTATION_LANDSCAPE == orientation &&
				null != adView_LANDSCAPE) {
			DebugLog.i(TAG, "Ad: orientation -> landscape");
			DebugLog.i(TAG, "Ad: adView -> has value");
			adView = adView_LANDSCAPE;
		} else if(Configuration.ORIENTATION_PORTRAIT == orientation &&
				null != adView_PORTRAIT) {
			DebugLog.i(TAG, "Ad: orientation -> portrait");
			DebugLog.i(TAG, "Ad: adView -> has value");
			adView = adView_PORTRAIT;
		} else {
			DebugLog.i(TAG, "Ad: adView -> creating...");
			adView = new AdView(dynamicApp, AdSize.SMART_BANNER, AD_UNIT_ID);
			adView.setAdListener(this);
			
			if(Configuration.ORIENTATION_LANDSCAPE == orientation)
				adView_LANDSCAPE = adView;
			else
				adView_PORTRAIT = adView;
		}
		
		adLayout.removeAllViews();
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 1.0f);
		adLayout.addView(adView, params1);
		DebugLog.i(TAG, "Ad is added");
		adView.loadAd(request);
		DebugLog.i(TAG, "Ad is loaded");
	}
	
	public void createAd() {
		// get screen orientation
		String AD_UNIT_DYNAMICAPP = "a14e62fe30c05d7";
		dynamicApp.getWebView().setId(4567);
		position = param.get("position", 0);
		request = new AdRequest();
		
		createAdContainer();
		layoutAdContainer();
		isAdded = true;
		
		AD_UNIT_ID = param.get("id", "");
		if(AD_UNIT_ID.length() <= 0)
			AD_UNIT_ID = AD_UNIT_DYNAMICAPP;
		
		if(DynamicAppUtils.isEmulator())
			request.addTestDevice(AdRequest.TEST_EMULATOR);

		setAdInstance();
		isCreated = true;
	}
	
	public void showAd() {
		DebugLog.i(TAG, "ad width: " + adView.getWidth());
		if(adView.isReady()) {
			float density = dynamicApp.getResources().getDisplayMetrics().density;
			int heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
			int orientation = this.getScreenOrientation();
			
			if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
				DebugLog.i(TAG, "orientation: ORIENTATION_LANDSCAPE");
				heightOriention = AdSize.LANDSCAPE_AD_HEIGHT;
		    } else if (orientation == Configuration.ORIENTATION_PORTRAIT){
		    	DebugLog.i(TAG, "orientation: ORIENTATION_PORTRAIT");
		    	heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
		    }
			
			int height = Math.round(heightOriention  * density);
			
			if(position == POSITION_TOP) {
				DynamicAppActivity.layoutWithAd.removeAllViews();
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
				DynamicAppActivity.layoutWithAd.addView(adLayout, params);
				DynamicAppActivity.layoutWithAd.addView(dynamicApp.getWebView(), this.getPosition());
			} else {
				if(!isAdded) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, getScreenHeight() - height);
					dynamicApp.getWebView().setLayoutParams(params);
					DynamicAppActivity.layoutWithAd.addView(adLayout, this.getPosition());
				}
			}
			
			DynamicAppActivity.layoutWithAd.requestLayout();
			DynamicAppActivity.layoutWithAd.invalidate();
			
			adLayout.setVisibility(View.VISIBLE);
			isShown = true;
			if(adView.isShown())
				Ad.onSuccess(new JSONObject(), callbackId, false);
			else
				Ad.onError("", callbackId);
		} else {
			Ad.onError("", callbackId);
		}
	}
	
	public void hideAd() {
		if(adLayout.isShown()) {
			adLayout.setVisibility(View.GONE);
			DynamicAppActivity.layoutWithAd.removeView(adLayout);
			
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			dynamicApp.getWebView().setLayoutParams(params);
			
			DynamicAppActivity.layoutWithAd.requestLayout();
			DynamicAppActivity.layoutWithAd.invalidate();
			
			isShown = false;
			isAdded = false;
			if(!adView.isShown())
				Ad.onSuccess(new JSONObject(), callbackId, false);
			else
				Ad.onError("", callbackId);
		} else {
			Ad.onError("", callbackId);
		}
	}
	
	public LinearLayout getAdLayout() {
		return this.adLayout;
	}
	
	public RelativeLayout.LayoutParams getPosition() {
		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if(position == POSITION_TOP)
			relativeParams.addRule(RelativeLayout.BELOW, adLayout.getId());
		else
			relativeParams.addRule(RelativeLayout.BELOW, dynamicApp.getWebView().getId());
		
		return relativeParams;
	}
	
	/** Called when an ad is clicked and about to return to the application. */
	@Override
	public void onDismissScreen(com.google.ads.Ad arg0) {
	}

	/** Called when an ad was not received. */
	@Override
	public void onFailedToReceiveAd(com.google.ads.Ad arg0, ErrorCode arg1) {
		if(adLayout.isShown())
			adLayout.setVisibility(View.GONE);
		Ad.onError("", callbackId);
	}

	/**
	 * Called when an ad is clicked and going to start a new Activity that will
	 * leave the application (e.g. breaking out to the Browser or Maps
	 * application).
	 */
	@Override
	public void onLeaveApplication(com.google.ads.Ad arg0) {
		
	}

	/**
	 * Called when an Activity is created in front of the app (e.g. an
	 * interstitial is shown, or an ad is clicked and launches a new Activity).
	 */
	@Override
	public void onPresentScreen(com.google.ads.Ad arg0) {
	}

	/** Called when an ad is received. */
	@Override
	public void onReceiveAd(com.google.ads.Ad arg0) {
		Ad.onSuccess(new JSONObject(), callbackId, false);
	}

	public int getScreenHeight() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		dynamicApp.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		DebugLog.i(TAG, "screen height: " + displaymetrics.heightPixels);
		
		return displaymetrics.heightPixels;
	}
	
	@Override
	public void onConfigurationChanged(Configuration config) {
		if(isCreated) { 
			this.setAdInstance();
	
			float density = dynamicApp.getResources().getDisplayMetrics().density;
			int heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
			
			if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
				DebugLog.i(TAG, "orientation: ORIENTATION_LANDSCAPE");
				heightOriention = AdSize.LANDSCAPE_AD_HEIGHT;
		    } else if (config.orientation == Configuration.ORIENTATION_PORTRAIT){
		    	DebugLog.i(TAG, "orientation: ORIENTATION_PORTRAIT");
		    	heightOriention = AdSize.PORTRAIT_AD_HEIGHT;
		    }
			
			int height = Math.round(heightOriention  * density);
			
			if(position == POSITION_TOP && isShown) {
				DynamicAppActivity.layoutWithAd.removeAllViews();
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
				DynamicAppActivity.layoutWithAd.addView(adLayout, params);
				DynamicAppActivity.layoutWithAd.addView(dynamicApp.getWebView(), this.getPosition());
			} else {
				int webPageHeight = getScreenHeight() - height;
				DebugLog.i(TAG, "Ad screen height: " + webPageHeight);
				DebugLog.i(TAG, "Ad webpage height: " + webPageHeight);
				DebugLog.i(TAG, "Ad height: " + height);
				if(isShown) {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, webPageHeight);
					dynamicApp.getWebView().setLayoutParams(params);
				}
			}
		
			DynamicAppActivity.layoutWithAd.requestLayout();
			DynamicAppActivity.layoutWithAd.invalidate();
			
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
		return dynamicApp.getResources().getConfiguration().orientation;
	}

}
