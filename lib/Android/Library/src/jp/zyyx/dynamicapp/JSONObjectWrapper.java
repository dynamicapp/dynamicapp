package jp.zyyx.dynamicapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSONObjectのwrapper
 */
public class JSONObjectWrapper extends JSONObject {
	/**
	 * コンストラクタはスーパークラスを呼び出すだけ
	 */
	public JSONObjectWrapper(String json) throws JSONException {
		super(json);
	}

	/**
	 * Boolean値の取得
	 */
	public Boolean get(String key, Boolean value) {
		// JSONに設定されていないkeyの場合はデフォルト値を返す
		if (has(key)) {
			try {
				return getBoolean(key);
			} catch (JSONException e) {
				// 例外が発生した場合は、型が合わない可能性が高い
				// デフォルト値を返す
			}
		}

		return value;
	}

	/**
	 * int値の取得
	 */
	public int get(String key, int value) {
		// JSONに設定されていないkeyの場合はデフォルト値を返す
		if (has(key)) {
			try {
				return getInt(key);
			} catch (JSONException e) {
				// 例外が発生した場合は、型が合わない可能性が高い
				// デフォルト値を返す
			}
		}

		return value;
	}

	/**
	 * String値の取得
	 */
	public String get(String key, String value) {
		// JSONに設定されていないkeyの場合はデフォルト値を返す
		if (has(key)) {
			try {
				return getString(key);
			} catch (JSONException e) {
				// 例外が発生した場合は、型が合わない可能性が高い
				// デフォルト値を返す
			}
		}

		return value;
	}

	/**
	 * JSONArrayの取得
	 */
	public JSONArray get(String key, JSONArray value) {
		// JSONに設定されていないkeyの場合はデフォルト値を返す
		if (has(key)) {
			try {
				return getJSONArray(key);
			} catch (JSONException e) {
				// 例外が発生した場合は、型が合わない可能性が高い
				// デフォルト値を返す
			}
		}

		return value;
	}
	
	/**
	 * double
	 */
	public double get(String key, double value) {
		if (has(key)) {
			try {
				return getDouble(key);
			} catch (JSONException e) {
				
			}
		}

		return value;
	}
}
