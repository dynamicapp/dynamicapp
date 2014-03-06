package jp.zyyx.dynamicapp.plugins;

import java.io.ByteArrayOutputStream;
import java.io.File;

import jp.zyyx.dynamicapp.DynamicAppActivity;
import jp.zyyx.dynamicapp.core.Plugin;
import jp.zyyx.dynamicapp.plugins.view.DynamicAppVideoView;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;
import jp.zyyx.dynamicapp.wrappers.JSONObjectWrapper;

import org.json.JSONException;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.ViewGroup;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.MediaController;

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
public class Movie extends Plugin {
	private static final String TAG = "Movie";

	// Movie messages
	private static final int MOVIE_STATE = 1;
	private static final int MOVIE_DURATION = 2;
	//private static final int MOVIE_POSITION = 3;
	private static final int MOVIE_ERROR = 9;

	// Movie states
	private static final int MOVIE_NONE = 0;
	private static final int MOVIE_STARTING = 1;
	private static final int MOVIE_RUNNING = 2;
	private static final int MOVIE_PAUSED = 3;
	private static final int MOVIE_STOPPED = 4;
	
	private static final int ERROR_ABORTED = 1;
	private static final int ERROR_NETWORK = 2;
	private static final int ERROR_DECODE = 3;
	private static final int ERROR_NONE_SUPPORTED = 4;

	private static Movie instance = null;
	private DynamicAppVideoView mVideoView = null;
	private MediaPlayer mBGMPlayer = null;

	private String movieFile = null;
	private String mediaId = null;
	private String jsonVideoParam = null;

	private boolean isSettingVideo = false;
	private boolean isSourceURL = false;
	private boolean keepScreenOn = true;
	private boolean isPaused = false;
	private int position = 0;
	private int duration = -1;
	//private int scalingMode = 1;
	private int controlStyle = 0;
	
	public static int frameX = -1;
	public static int frameY = -1;

	final SharedPreferences sharedPreferences = PreferenceManager
			.getDefaultSharedPreferences(mainActivity);

	private Movie() {
		super();
	}

	public static synchronized Movie getInstance() {
		if (instance == null) {
			instance = new Movie();
		}
		return instance;
	}

	public void init(String methodName, String params, String callbackId) {
		super.init(methodName, params, callbackId);
		if(mVideoView == null) {
			mVideoView = Utilities.VideoViewRef;
			mVideoView.setOnErrorListener(new OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					mainActivity.callJsEvent(PROCESSING_FALSE);
					DebugLog.e(TAG, "Error: " + what + "," + extra);
					int error = 8;
				
					if(mp != null) {
						try{
							mp.reset();
						} catch(Exception ex) {
							ex.printStackTrace();
						}
					}
					mVideoView.setVisibility(DynamicAppVideoView.GONE);
					switch(what) {
						case MediaPlayer.MEDIA_ERROR_UNKNOWN:
							if(isSettingVideo && isSourceURL) {
								error = ERROR_NONE_SUPPORTED;
							} else {
								error = ERROR_DECODE;
							}
						break;
						case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
							error = ERROR_NETWORK;
						break;
					}
					if (mainActivity.getWindow() != null) {
						String script = "DynamicApp.Movie.onStatus(\"" + mediaId + "\","
								+ Movie.MOVIE_ERROR + ",\"" + error + "\");";
						mainActivity.callJsEvent(script);
					}
					
					return true;
				}
	
			});
	
			mVideoView.setOnPreparedListener(new OnPreparedListener() {
				public void onPrepared(MediaPlayer mediaPlayer) {
					mBGMPlayer = mediaPlayer;
					DebugLog.e(TAG, "[video] @onPrepared");
					String script = "DynamicApp.Movie.onStatus(\"" + mediaId
							+ "\"," + Movie.MOVIE_DURATION + ","
							+ getVideoDuration() + ");";
					mainActivity.callJsEvent(script);
					startVideo();
				}
			});
	
			mVideoView.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mediaPlayer) {
					
					mainActivity.callJsEvent(PROCESSING_FALSE);
					mVideoView.setVisibility(DynamicAppVideoView.GONE);
					/*String script = "DynamicApp.Movie.onStatus(\"" + mediaId
							+ "\"," + Movie.MOVIE_POSITION + "," + 0 + ");";
					dynamicApp.callJsEvent(script);*/
	
					String script = "DynamicApp.Movie.onStatus(\"" + mediaId + "\","
							+ Movie.MOVIE_STATE + "," + Movie.MOVIE_STOPPED + ");";
					mainActivity.callJsEvent(script);
					DebugLog.w(TAG, "video player onCompletion");
				}
			});
		}
	}

	@Override
	public void execute() {
		mVideoView.setKeepScreenOn(keepScreenOn);

		DebugLog.w(TAG, "method " + methodName + " is executed.");
		DebugLog.w(TAG, "parameters are: " + params);
		this.mediaId = param.get("mediaId", "");

		if (methodName.equalsIgnoreCase("getThumbnail")) {
			this.movieFile = param.get("movieFile", "");
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.getThumbnail();

		} else if (methodName.equalsIgnoreCase("play")) {
			this.movieFile = param.get("movieFile", "");
			this.jsonVideoParam = param.get("frame", "");
			//this.scalingMode = param.get("scalingMode", 1);
			this.controlStyle = param.get("controlStyle", 0);
		
			mainActivity.callJsEvent(PROCESSING_FALSE);
			isSettingVideo = false;
			
			if (!isPaused)
				this.playVideo();
			else {
				mainActivity.callJsEvent(PROCESSING_FALSE);
				String script = "DynamicApp.Movie.onStatus(\"" + mediaId
						+ "\"," + Movie.MOVIE_DURATION + ","
						+ getVideoDuration() + ");";
				mainActivity.callJsEvent(script);
				this.startVideo();
			}
		} else if (methodName.equalsIgnoreCase("getCurrentPosition")) {
			this.getVideoPosition();
		} else if (methodName.equalsIgnoreCase("setCurrentPosition")) {
			this.position = param.get("position", 1);
			this.seekVideo(position);
		} else if (methodName.equalsIgnoreCase("pause")) {
			this.pauseVideo();
		} else if (methodName.equalsIgnoreCase("stop")) {
			this.stopVideo();
		} else if (methodName.equalsIgnoreCase("release")) {
			this.mediaId = param.get("mediaId", "");
			this.release();
		}

	}

	public void getThumbnail() {
		mVideoView.setVisibility(DynamicAppVideoView.GONE);
		new Thread() {
			@Override
			public void run() {
				Bitmap thumb = null;
				String filename = null;
				if (movieFile.indexOf("http://") != -1) {
					isSourceURL = true;
					filename = movieFile;
					String tempThumb = sharedPreferences.getString(movieFile, "");
					if (tempThumb.length() > 0) {
						try {
							jsonObject.put("message", tempThumb);
						} catch (JSONException e) {
							e.printStackTrace();
							Movie.onError(ERROR_ABORTED + "",
									callbackId);
						}
						onSuccess();
						return;
					}
				} else if(movieFile.indexOf("content://") != -1) {
					Uri mVideoUri = Uri.parse(movieFile);
					String videoID =  mVideoUri.getPathSegments().get(3);
					thumb = getContentThumbnail(videoID);
				} else {
					isSourceURL = false;
					String path = Utilities.makePath(movieFile);
					DebugLog.e(TAG, "[video] try:" + path);
					if (new File(path).exists()) {
						filename = path;
					} else {						
						processError(ERROR_ABORTED);
						return;
					}
				}

				try {
					if (thumb == null) {
						thumb = ThumbnailUtils.createVideoThumbnail(filename,
								MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
					}
				} catch (Exception e) {
					Movie.onError(ERROR_NONE_SUPPORTED + "", callbackId);
					return;
				}
		
				if (thumb != null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					thumb.compress(CompressFormat.JPEG, 100, bos);
					byte[] _bArray = bos.toByteArray();
					String thumbContents = "data:image/jpeg;base64," + Base64.encodeToString(_bArray,
							Base64.DEFAULT);
					
					if(isSourceURL) {
						final Editor editor = sharedPreferences.edit();
						editor.putString(movieFile, thumbContents);
						editor.commit();
					}
					
					try {
						jsonObject.put("message", thumbContents);
					} catch (JSONException e) {
						e.printStackTrace();
						Movie.onError(ERROR_NONE_SUPPORTED + "",
								callbackId);
					}
					onSuccess();
					thumb.recycle();
					thumb = null;
					return;
				} else {					
					Movie.onError(ERROR_NONE_SUPPORTED + "", callbackId);
					return;
				}
		}}.start();
	}

	private Bitmap getContentThumbnail(String videoID) {
		long id = Long.parseLong(videoID);
		ContentResolver crThumb = mainActivity.getContentResolver();
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inSampleSize = 1;
		Bitmap curThumb = MediaStore.Video.Thumbnails.getThumbnail(crThumb, id, MediaStore.Video.Thumbnails.MINI_KIND, options);
		
//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
//		curThumb.compress(CompressFormat.JPEG, 100, bos);
//		byte[] _bArray = bos.toByteArray();
//		String thumbContents = "data:image/jpeg;base64," + Base64.encodeToString(_bArray,
//				Base64.DEFAULT);
		
		return curThumb;
	}
	
	/**
	 * Prepare to play the movie.
	 */
	public void setVideo() {
		isSettingVideo = true;
		
		JSONObjectWrapper paramWork = null;

		try {
			paramWork = new JSONObjectWrapper(this.jsonVideoParam);
		} catch (JSONException e) {
			e.printStackTrace();
			processError(ERROR_ABORTED);
		}

		final JSONObjectWrapper param = paramWork;
		
		final ViewGroup.MarginLayoutParams layoutparam = (MarginLayoutParams) mVideoView
				.getLayoutParams();
		final int x = Utilities.getDPI(param.get("posX", 0));
		final int y = Utilities.getDPI(param.get("posY", 0));
		final int w = Utilities.getDPI(param.get("width", 320));
		final int h = Utilities.getDPI(param.get("height", 180));
		layoutparam.leftMargin = x;
		layoutparam.topMargin = y;
		frameX = x;
		frameY = y;

		DynamicAppActivity.handler.post(new Runnable() {
			@Override
			public void run() {
				mVideoView.setLayoutParams(layoutparam);
				mVideoView.changeVideoSize(w, h);
				mVideoView.layout(x, y, x + w, y + h);
				mVideoView.setVisibility(DynamicAppVideoView.VISIBLE);
				mVideoView.requestFocus();

				if (controlStyle > 0) {
					MediaController mediaController = new MediaController(
							mainActivity);
					mediaController.setMediaPlayer(mVideoView);
					mVideoView.setMediaController(mediaController);
				}
				
				if (movieFile.equals("")) {
					processError(ERROR_ABORTED);
				}
				
				if (movieFile.indexOf("http://") != -1) {
					isSourceURL = true;
					mVideoView.setVideoURI(Uri.parse(movieFile));
				} else if (movieFile.indexOf("content://") != -1) {
					//isSourceURL = true;
					mVideoView.setVideoURI(Uri.parse(movieFile));
				} else {
					isSourceURL = false;
					String path = Utilities.makePath(movieFile);
					DebugLog.e(TAG, "[video] try:" + path);
					if (new File(path).exists()) {
						mVideoView.setVideoPath(path);
						DebugLog.w(TAG, "set video path");
					} else {
						processError(ERROR_ABORTED);
					}
				}
			}
		});
	}

	/**
	 * Play the movie.
	 */
	public void playVideo() {
		String script = "DynamicApp.Movie.onStatus(\"" + mediaId + "\","
				+ Movie.MOVIE_STATE + "," + Movie.MOVIE_STARTING + ");";
		mainActivity.callJsEvent(script);
		setVideo();
	}

	/**
	 * Start playing the movie.
	 */
	public void startVideo() {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		mVideoView.start();
		isPaused = false;
		String script = "DynamicApp.Movie.onStatus(\"" + mediaId + "\","
				+ Movie.MOVIE_STATE + "," + Movie.MOVIE_RUNNING + ");";
		mainActivity.callJsEvent(script);
	}

	/**
	 * Stop playing the movie.
	 */
	public void stopVideo() {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		mVideoView.setVisibility(DynamicAppVideoView.GONE);
		new Thread() {
			public void run() {
				if (mVideoView.isPlaying()) {
					mVideoView.seekTo(0);
					mVideoView.stopPlayback();
				}
				isPaused = false;
				String script = "DynamicApp.Movie.onStatus(\"" + mediaId
						+ "\"," + Movie.MOVIE_STATE + "," + Movie.MOVIE_STOPPED
						+ ");";
				mainActivity.callJsEvent(script);
			}
		}.start();
	}

	/**
	 * Pause the movie.
	 */
	public void pauseVideo() {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		mVideoView.pause();
		try {
			jsonObject.put("message", Movie.MOVIE_PAUSED);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.onSuccess();
		isPaused = true;
	}

	public void release() {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		mBGMPlayer.release();
		String script = "DynamicApp.Movie.onStatus(\"" + mediaId + "\","
				+ Movie.MOVIE_STATE + "," + Movie.MOVIE_NONE + ");";
		mainActivity.callJsEvent(script);
	}

	/**
	 * Seek the movie.
	 */
	public void seekVideo(final int time) {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		new Thread() {
			public void run() {
				mVideoView.seekTo(time * 1000);
				int pos = mVideoView.getCurrentPosition()/1000;
				try {
					jsonObject.put("message", pos);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				onSuccess();
		}}.start();
	}

	/**
	 * Duration of the movie.
	 */
	public int getVideoDuration() {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		duration = mVideoView.getDuration() / 1000;
		DebugLog.e(TAG, duration + "");
		return duration;
	}

	/**
	 * Position of the movie.
	 */
	public void getVideoPosition() {
		mainActivity.callJsEvent(PROCESSING_FALSE);
		position = mVideoView.getCurrentPosition() / 1000;
		try {
			jsonObject.put("message", position);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		this.onSuccess();
	}

	private void processError(int err) {
		switch (err) {
		case ERROR_ABORTED:
			String script = "DynamicApp.Movie.onStatus(\"" + mediaId + "\","
					+ Movie.MOVIE_ERROR + ",\"" + ERROR_ABORTED + "\");";
			mainActivity.callJsEvent(script);
			break;
		case ERROR_NONE_SUPPORTED:
			script = "DynamicApp.Movie.onStatus(\"" + mediaId + "\","
					+ Movie.MOVIE_ERROR + ",\"" + ERROR_NONE_SUPPORTED + "\");";
			mainActivity.callJsEvent(script);
			break;
		}
	}

	@Override
	public void onBackKeyDown() {
		this.stopVideo();
		Utilities.currentCommand = null;
		instance = null;
	}

}
