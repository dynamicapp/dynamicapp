package jp.zyyx.dynamicapp.utilities;

import android.util.Log;

public class DebugLog {

	public static int v(String tag, String msg) {
		if (DynamicAppUtils.DEBUG) {
			return Log.v(tag, msg);
		}
		return 0;
	}

	public static int d(String tag, String msg) {
		if (DynamicAppUtils.DEBUG) {
			return Log.d(tag, msg);
		}
		return 0;
	}

	public static int i(String tag, String msg) {
		if (DynamicAppUtils.DEBUG) {
			return Log.i(tag, msg);
		}
		return 0;
	}

	public static int w(String tag, String msg) {
		if (DynamicAppUtils.DEBUG) {
			return Log.w(tag, msg);
		}
		return 0;
	}

	public static int e(String tag, String msg) {
		if (DynamicAppUtils.DEBUG) {
			return Log.e(tag, msg);
		}
		return 0;
	}
}

