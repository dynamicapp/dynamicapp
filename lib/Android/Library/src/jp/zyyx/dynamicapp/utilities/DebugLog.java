package jp.zyyx.dynamicapp.utilities;

import android.util.Log;

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
public class DebugLog {

	public static int v(String tag, String msg) {
		if (Utilities.isDebuggable) {
			return Log.v(tag, msg);
		}
		return 0;
	}

	public static int d(String tag, String msg) {
		if (Utilities.isDebuggable) {
			return Log.d(tag, msg);
		}
		return 0;
	}

	public static int i(String tag, String msg) {
		if (Utilities.isDebuggable) {
			return Log.i(tag, msg);
		}
		return 0;
	}

	public static int w(String tag, String msg) {
		if (Utilities.isDebuggable) {
			return Log.w(tag, msg);
		}
		return 0;
	}

	public static int e(String tag, String msg) {
		if (Utilities.isDebuggable) {
			return Log.e(tag, msg);
		}
		return 0;
	}
}

