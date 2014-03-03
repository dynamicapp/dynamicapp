package jp.zyyx.dynamicapp.wrappers;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

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

