package jp.zyyx.dynamicapp.plugins.activity;

import jp.zyyx.dynamicapp.plugins.view.DynamicAppCameraView;
import android.R;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

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
public class CameraActivity extends Activity implements OnClickListener {
	private static CameraActivity instance;
	private DynamicAppCameraView cameraView;
	private ImageButton captureBtn;
	private static ImageButton saveBtn;
	private static ImageButton cancelBtn;
	private ImageButton retakeBtn;
	protected OrientationEventListener myOrientationEventListener;
	public static int orientation = 0;
	
	private int quality = 0;
	private int destinationType = 0;
	private int sourceType = 0;
	private int encodingType = 0;
	private int targetWidth = 0;
	private int targetHeight = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent i = getIntent();
		this.setQuality(i.getIntExtra("quality", 100));
		this.setDestinationType(i.getIntExtra("destinationType", 0));
		this.setSourceType(i.getIntExtra("sourceType", 1));
		this.setEncodingType(i.getIntExtra("encodingType", 0));
		this.setTargetHeight(i.getIntExtra("targetHeight", 100));
		this.setTargetWidth(i.getIntExtra("targetWidth", 100));

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setInstance(this);

		if (Build.VERSION.SDK_INT <= 10 && Build.VERSION.SDK_INT >= 7) {
			this.setLandscapeLayout();
		} else {
			this.setPortraitLayout();
		}

		myOrientationEventListener = new OrientationEventListener(this,
				SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(int orientation) {
				CameraActivity.orientation = orientation;
			}
		};

		if (myOrientationEventListener.canDetectOrientation()) {
			myOrientationEventListener.enable();
		}

	}

	@Override
	public void onClick(View v) {
		if (v.equals(captureBtn)) {
			captureBtn.setEnabled(false);
			cameraView.capture();
			retakeBtn.setVisibility(LinearLayout.VISIBLE);
			captureBtn.setVisibility(LinearLayout.GONE);
		} else if (v.equals(saveBtn)) {
			cameraView.save();
		} else if (v.equals(retakeBtn)) {
			cameraView.retake();
			retakeBtn.setVisibility(LinearLayout.GONE);
			captureBtn.setVisibility(LinearLayout.VISIBLE);
			captureBtn.setEnabled(true);
			saveBtn.setEnabled(false);
		} else
			cameraView.cancel();
	}

	public void setPortraitLayout() {
		FrameLayout cameraFrameLayout = new FrameLayout(this);
		cameraView = new DynamicAppCameraView(this);
		LinearLayout CameraWidgets = new LinearLayout(this);

		CameraWidgets.setGravity(Gravity.BOTTOM);
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1f);
		params1.width = 70;

		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params1.width = 70;

		captureBtn = new ImageButton(this);
		captureBtn.setImageResource(R.drawable.ic_menu_camera);
		captureBtn.setLayoutParams(params1);
		captureBtn.setEnabled(true);

		setSaveBtn(new ImageButton(this));
		setCancelBtn(new ImageButton(this));

		saveBtn.setEnabled(false);
		saveBtn.setImageResource(R.drawable.ic_menu_save);
		saveBtn.setLayoutParams(params2);
		cancelBtn.setEnabled(true);
		cancelBtn.setImageResource(R.drawable.ic_menu_close_clear_cancel);
		cancelBtn.setLayoutParams(params2);

		retakeBtn = new ImageButton(this);
		retakeBtn.setImageResource(R.drawable.ic_menu_rotate);
		retakeBtn.setLayoutParams(params1);
		retakeBtn.setVisibility(LinearLayout.GONE);

		CameraWidgets.addView(cancelBtn);
		CameraWidgets.addView(captureBtn);
		CameraWidgets.addView(retakeBtn);
		CameraWidgets.addView(saveBtn);

		cameraFrameLayout.addView(cameraView);
		cameraFrameLayout.addView(CameraWidgets);

		setContentView(cameraFrameLayout);
		captureBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		retakeBtn.setOnClickListener(this);
	}

	private void setLandscapeLayout() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		FrameLayout cameraFrameLayout = new FrameLayout(this);
		cameraView = new DynamicAppCameraView(this);
		LinearLayout CameraWidgets = new LinearLayout(this);
		LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f);
		params1.gravity = Gravity.RIGHT;
		params1.width = 70;

		LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params2.gravity = Gravity.RIGHT;
		params2.width = 70;

		CameraWidgets.setGravity(Gravity.CENTER);
		CameraWidgets.setOrientation(LinearLayout.VERTICAL);

		captureBtn = new ImageButton(this);
		captureBtn.setImageResource(R.drawable.ic_menu_camera);
		captureBtn.setLayoutParams(params1);
		captureBtn.setEnabled(true);

		setSaveBtn(new ImageButton(this));
		setCancelBtn(new ImageButton(this));

		retakeBtn = new ImageButton(this);
		retakeBtn.setImageResource(R.drawable.ic_menu_rotate);
		retakeBtn.setLayoutParams(params1);
		retakeBtn.setVisibility(LinearLayout.GONE);

		saveBtn.setEnabled(false);
		saveBtn.setImageResource(R.drawable.ic_menu_save);
		saveBtn.setLayoutParams(params2);
		cancelBtn.setEnabled(true);
		cancelBtn.setImageResource(R.drawable.ic_menu_close_clear_cancel);
		cancelBtn.setLayoutParams(params2);

		CameraWidgets.addView(cancelBtn);
		CameraWidgets.addView(captureBtn);
		CameraWidgets.addView(retakeBtn);
		CameraWidgets.addView(saveBtn);

		cameraFrameLayout.addView(cameraView);
		cameraFrameLayout.addView(CameraWidgets);

		setContentView(cameraFrameLayout);
		captureBtn.setOnClickListener(this);
		saveBtn.setOnClickListener(this);
		cancelBtn.setOnClickListener(this);
		retakeBtn.setOnClickListener(this);
	}

	/**
	 * @return the saveBtn
	 */
	public static ImageButton getSaveBtn() {
		return saveBtn;
	}

	/**
	 * @param saveBtn
	 *            the saveBtn to set
	 */
	public static void setSaveBtn(ImageButton saveBtn) {
		CameraActivity.saveBtn = saveBtn;
	}

	/**
	 * @return the cancelBtn
	 */
	public static ImageButton getCancelBtn() {
		return cancelBtn;
	}

	/**
	 * @param cancelBtn
	 *            the cancelBtn to set
	 */
	public static void setCancelBtn(ImageButton cancelBtn) {
		CameraActivity.cancelBtn = cancelBtn;
	}

	/**
	 * @return the quality
	 */
	public int getQuality() {
		return quality;
	}

	/**
	 * @param quality
	 *            the quality to set
	 */
	public void setQuality(int quality) {
		this.quality = quality;
	}

	/**
	 * @return the destinationType
	 */
	public int getDestinationType() {
		return destinationType;
	}

	/**
	 * @param destinationType
	 *            the destinationType to set
	 */
	public void setDestinationType(int destinationType) {
		this.destinationType = destinationType;
	}

	public int getSourceType() {
		return sourceType;
	}

	public void setSourceType(int sourceType) {
		this.sourceType = sourceType;
	}

	public int getEncodingType() {
		return encodingType;
	}

	public void setEncodingType(int encodingType) {
		this.encodingType = encodingType;
	}

	public int getTargetWidth() {
		return targetWidth;
	}

	public void setTargetWidth(int targetWidth) {
		this.targetWidth = targetWidth;
	}

	public int getTargetHeight() {
		return targetHeight;
	}

	public void setTargetHeight(int targetHeight) {
		this.targetHeight = targetHeight;
	}

	/**
	 * @return the instance
	 */
	public static CameraActivity getInstance() {
		return instance;
	}

	/**
	 * @param instance
	 *            the instance to set
	 */
	public static void setInstance(CameraActivity instance) {
		CameraActivity.instance = instance;
	}
}
