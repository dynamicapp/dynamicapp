package jp.zyyx.dynamicapp.core;

import jp.zyyx.dynamicapp.plugins.Ads;
import jp.zyyx.dynamicapp.plugins.AddressBook;
import jp.zyyx.dynamicapp.plugins.AppVersion;
import jp.zyyx.dynamicapp.plugins.BasePlugin;
import jp.zyyx.dynamicapp.plugins.CustomNotification;
import jp.zyyx.dynamicapp.plugins.Database;
import jp.zyyx.dynamicapp.plugins.DynamicAppBluetooth;
import jp.zyyx.dynamicapp.plugins.DynamicAppCamera;
import jp.zyyx.dynamicapp.plugins.DynamicAppFile;
import jp.zyyx.dynamicapp.plugins.DynamicAppQRCodeReader;
import jp.zyyx.dynamicapp.plugins.Encryptor;
import jp.zyyx.dynamicapp.plugins.Felica;
import jp.zyyx.dynamicapp.plugins.ImageDecrypt;
import jp.zyyx.dynamicapp.plugins.LoadingScreen;
import jp.zyyx.dynamicapp.plugins.Movie;
import jp.zyyx.dynamicapp.plugins.ResourceCache;
import jp.zyyx.dynamicapp.plugins.Sound;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.plugins.Contacts;

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
public final class PluginsFactory {
	private static final String TAG = "PluginsFactory";

	public static final String PLUGIN_NOTIFICATION = "Notification";
	public static final String PLUGIN_ENCRYPTOR = "Encryptor";
	public static final String PLUGIN_CAMERA = "Camera";
	public static final String PLUGIN_SOUND = "Sound";
	public static final String PLUGIN_MOVIE = "Movie";
	public static final String PLUGIN_FILE = "File";
	public static final String PLUGIN_FILE_READER = "FileReader";
	public static final String PLUGIN_FILE_WRITER = "FileWriter";
	public static final String PLUGIN_IMAGE_DECRYPTOR = "ImageDecrypt";
	public static final String PLUGIN_QRCODE_READER = "QRReader";
	public static final String PLUGIN_LOADING_SCREEN = "LoadingScreen";
	public static final String PLUGIN_RESOURCE_CACHE = "ResourceCache";
	public static final String PLUGIN_DATABASE = "Database";
	public static final String PLUGIN_ADDRESSBOOK = "AddressBook";
	public static final String PLUGIN_BLUETOOTH = "Bluetooth";
	public static final String PLUGIN_BLUETOOTH4LE = "Bluetooth4LE";
	public static final String PLUGIN_FELICA = "Felica";
	public static final String PLUGIN_APP_VERSION = "AppVersion";
	public static final String PLUGIN_CONTACTS = "Contacts";
	public static final String PLUGIN_AD = "Ad";

	/**
	 * @param className		plug-in class name to be instantiated
	 * @param methodName	plug-in method name to be executed
	 * @param params		json format option for the method
	 * @param callbackId	javascript method callback id
	 * @return				DynamicAppPlugin
	 */
	public static Command instantiate(String className, String methodName, String params, String callbackId) {
		DebugLog.w(TAG, "instantiate className: " + className + "|methodName:" + methodName +
				"|params:" + params + "|callbackId:" + callbackId + "***");
		Command command = getPlugin(className);
		command.init(methodName, params, callbackId);
		return command;
	}

	private static Command getPlugin(String className) {
		if (className.equalsIgnoreCase(PLUGIN_NOTIFICATION)) {
			return CustomNotification.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_ENCRYPTOR)) {
			return Encryptor.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_CAMERA)) {
			return DynamicAppCamera.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_SOUND)) {
			return Sound.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_MOVIE)) {
			return Movie.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_FILE) || 
				 className.equalsIgnoreCase(PLUGIN_FILE_READER) ||
				 className.equalsIgnoreCase(PLUGIN_FILE_WRITER)) {
			return DynamicAppFile.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_IMAGE_DECRYPTOR)) {
			return ImageDecrypt.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_QRCODE_READER)) {
			return DynamicAppQRCodeReader.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_LOADING_SCREEN)) {
			return LoadingScreen.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_RESOURCE_CACHE)) {
			return ResourceCache.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_DATABASE)) {
			return Database.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_ADDRESSBOOK)) {
			return AddressBook.getInstance();
		} else if (className.equalsIgnoreCase(PLUGIN_BLUETOOTH)) {
			return DynamicAppBluetooth.getInstance();
		/* For now, comment out this code.
		} else if (className.equalsIgnoreCase(PLUGIN_BLUETOOTH4LE)) {
			return DynamicAppBluetooth4LE.getInstance();
		*/
 		} else if (className.equalsIgnoreCase(PLUGIN_FELICA)) {
			return Felica.getInstance();
 		} else if (className.equalsIgnoreCase(PLUGIN_APP_VERSION)) {
			return AppVersion.getInstance();
 		} else if (className.equalsIgnoreCase(PLUGIN_CONTACTS)) {
 			return Contacts.getInstance();
 		} else if (className.equalsIgnoreCase(PLUGIN_AD)) {
 			return Ads.getInstance();
 		} else {
			return BasePlugin.getInstance();
 		}
	}
}

