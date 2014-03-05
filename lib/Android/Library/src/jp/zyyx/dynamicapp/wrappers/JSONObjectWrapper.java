package jp.zyyx.dynamicapp.wrappers;

import org.json.JSONArray;
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
