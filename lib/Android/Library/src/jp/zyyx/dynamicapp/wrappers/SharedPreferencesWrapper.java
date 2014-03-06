package jp.zyyx.dynamicapp.wrappers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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
public class SharedPreferencesWrapper {
	private static final String KEY_VERSION = "Version";
	private static final String KEY_SERVER_ADDRESS = "ServerAddress";
	private static final String KEY_USERID = "UserId";
	private static final String KEY_PASSWORD = "Password";

	private static SharedPreferences getSharedPreferencesMethod(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}


	/** Public Methods */
	public static boolean setVersion(Context context, String version) {
		return getSharedPreferencesMethod(context).edit()
				.putString(KEY_VERSION, version).commit();
	}

	public static String getVersion(Context context) {
		return getSharedPreferencesMethod(context).getString(KEY_VERSION, "0");
	}

	public static String getServerAddress(Context context) {
		return getSharedPreferencesMethod(context).getString(KEY_SERVER_ADDRESS, "");
	}

	public static String getUserId(Context context) {
		return getSharedPreferencesMethod(context).getString(KEY_USERID, "");
	}

	public static String getPassword(Context context) {
		return getSharedPreferencesMethod(context).getString(KEY_PASSWORD, "");
	}
}

