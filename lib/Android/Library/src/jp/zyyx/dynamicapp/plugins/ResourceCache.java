package jp.zyyx.dynamicapp.plugins;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import jp.zyyx.dynamicapp.core.Plugin;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

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
public class ResourceCache extends Plugin {
	private static final String TAG = "ResourceCache";

	private static final int RESOURCE_REMOVED = 1;
	private static final int ERROR_RESOURCE_NOT_FOUND = 2;
	private static final int ERROR_RESOURCE_NOT_REMOVED = 3;
	private static final int ERROR_RESOURCE_NOT_CLEARED = 4;
	private static final int ERROR_UNKNOWN = 5;
	private static final int ERROR_MALFORMED_URL = 5;

	private static final long MAX_SIZE = 5242880L; // 5MB
	private static final int DOWNLOAD_BUFFER_SIZE = 4096;
	private static final String CLEAR = "CLEAR_RESOURCE_CACHE";

	private static ResourceCache instance = null;

	private boolean DEBUG = false;
	private String resourceURL = null;
	private String resourceId = null;
	private String resourceExpiryDate = null;
	private String resourceFullPath = null;
	private File cacheDir = mainActivity.getCacheDir();

	private ResourceCache() {
		super();
	}

	/**
	 * @return ResourceCache plugin instance
	 */
	public static synchronized ResourceCache getInstance() {
		if (instance == null) {
			instance = new ResourceCache();
		}
		return instance;
	}

	@Override
	public void execute() {
		DebugLog.w(TAG, "method " + methodName + " is called.");
		DebugLog.w(TAG, "parameters are: " + params);
		resourceURL = param.get("resourceURL", "");
		resourceId = param.get("id", "");
		resourceExpiryDate = param.get("expireDate", "");

		if (methodName.equalsIgnoreCase("add")) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.add();
		} else if (methodName.equalsIgnoreCase("remove")) {
			mainActivity.callJsEvent(PROCESSING_FALSE);
			this.remove();
		}
		instance = null;
	}

	/**
	 * Adds resource to cache folder
	 */
	public void add() {
		String testURL = "http://cache.kotaku.com/assets/images/9/2009/01/akinaigames.jpg";

		resourceURL = (DEBUG) ? testURL : resourceURL;
		DebugLog.w(TAG, "resourceURL : " + resourceURL);
		downloadResource(resourceURL);
	}

	/**
	 * Removes resource from the cache folder
	 */
	public void remove() {
		resourceFullPath = param.get("fullPath", "");
		DebugLog.w(TAG, "resource fullpath:" + resourceFullPath);

		if (CLEAR.equalsIgnoreCase(resourceFullPath)) {
			this.clearResourceCache();
		} else {
			this.removeResource();
		}
	}

	/**
	 * Removes resource file or directory and is called within
	 * jp.zyyx.dynamicapp.plugins.ResourceCache.remove
	 */
	private void removeResource() {
		int state;
		boolean removed = false;
		resourceFullPath = param.get("fullPath", "");
		String encodeURL = resourceFullPath.trim().replaceAll("\\s+", "%20");
		DebugLog.w(TAG, "encodeURL:" + encodeURL);

		File file = new File(encodeURL);
		DebugLog.w(TAG, "File: " + file);

		if (file.exists()) {
			Utilities.deletefile(file);
			// check if file is removed
			removed = !file.exists();
			state = (removed) ? RESOURCE_REMOVED : ERROR_RESOURCE_NOT_REMOVED;
		} else {
			removed = false;
			state = ResourceCache.ERROR_RESOURCE_NOT_FOUND;
		}

		if (removed) {
			ResourceCache.onSuccess(new JSONObject(), callbackId, false);
			DebugLog.w(TAG, "method " + methodName + " is executed successfully.");
		} else {
			ResourceCache.onError(state + "", callbackId);
			DebugLog.e(TAG, "error code: " + state);
			DebugLog.e(TAG, "method " + methodName + " failed to execute.");
		}
	}

	private void clearResourceCache() {
		Utilities.deletefile(cacheDir);
		boolean cleared = !cacheDir.exists();

		if (cleared) {
			// recreate cache directory
			cacheDir.mkdir();
			ResourceCache.onSuccess(new JSONObject(), callbackId, false);
			DebugLog.w(TAG, "method " + methodName + " is executed successfully.");
		} else {
			ResourceCache.onError(ERROR_RESOURCE_NOT_CLEARED + "", callbackId);
			DebugLog.e(TAG, "error code: " + ERROR_RESOURCE_NOT_CLEARED);
			DebugLog.e(TAG, "method " + methodName + " failed to execute.");
		}
	}

	private void downloadResource(final String downloadUrl) {
		new Thread() {
			public void run() {
				URL url;
				URLConnection conn;
				BufferedInputStream inStream = null;
				BufferedOutputStream outStream = null;
				File outFile;
				FileOutputStream fileStream = null;
				try {
					// url = new URL(URLEncoder.encode(downloadUrl, "UTF-8"));
					String encodeURL = downloadUrl.trim().replaceAll("\\s+",
							"%20");
					url = new URL(encodeURL);
					conn = url.openConnection();
					conn.setUseCaches(false);
					// get the filename

					String filename = Utilities.getFilenameFromURL(url);
					DebugLog.w(TAG, "filename:" + filename);
					String filenameParts[] =  filename.split("\\.");
					
					String tempfilename = filenameParts[0] + "_" + resourceId + "." + filenameParts[1];
					filename = tempfilename;
					DebugLog.w(TAG, "temp filename: " + tempfilename);
					
					// start download
					inStream = new BufferedInputStream(conn.getInputStream());
					DebugLog.w(TAG, "cacheDir:" + cacheDir);
					outFile = new File(cacheDir, filename);
					fileStream = new FileOutputStream(outFile);
					outStream = new BufferedOutputStream(fileStream,
							DOWNLOAD_BUFFER_SIZE);
					byte[] data = new byte[DOWNLOAD_BUFFER_SIZE];
					int bytesRead = 0;
					while (!isInterrupted()
							&& (bytesRead = inStream.read(data, 0, data.length)) >= 0) {
						if (isExceeding5MB(data))
							DebugLog.e(TAG, "exceeds 5MB");

						outStream.write(data, 0, bytesRead);
					}

					if (isInterrupted()) {
						// the download was canceled, so let's delete the
						// partially
						// downloaded file
						outFile.delete();
						onError();
					} else {
						// notify completion
						try {
							message = new JSONObject();
							jsonObject = new JSONObject();
							message.put("id", resourceId);
							message.put("fullPath", outFile);
							// message.put("filename", filename);
							message.put("resourceURL", resourceURL);
							message.put("expireDate", resourceExpiryDate);
							jsonObject.put("message", message);
						} catch (JSONException e) {
							onError();
							e.printStackTrace();
						}
						onSuccess();
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					ResourceCache.onError(ERROR_MALFORMED_URL + "", callbackId);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					ResourceCache.onError(ERROR_RESOURCE_NOT_CLEARED + "",
							callbackId);
				} catch (Exception e) {
					e.printStackTrace();
					ResourceCache.onError(ERROR_UNKNOWN + "", callbackId);
				} finally {
					try {
						if (outStream != null)
							outStream.close();
						if (fileStream != null)
							fileStream.close();
						if (inStream != null)
							inStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}
		}.start();
	}

// TODO handle download resource if interrupted
//	private boolean isInterrupted() {
//		return false;
//	}

	private boolean isExceeding5MB(byte[] data) {
		long size = getDirSize(cacheDir);
		long newSize = data.length + size;

		return (newSize > MAX_SIZE);
	}

	private static long getDirSize(File dir) {

		long size = 0;
		File[] files = dir.listFiles();

		for (File file : files) {
			if (file.isFile()) {
				size += file.length();
			}
		}

		return size;
	}

}
