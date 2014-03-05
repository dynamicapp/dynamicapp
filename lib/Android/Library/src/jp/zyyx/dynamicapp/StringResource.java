package jp.zyyx.dynamicapp;

import java.io.IOException;

import jp.zyyx.dynamicapp.utilities.Constant;
import jp.zyyx.dynamicapp.utilities.DebugLog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;

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
public class StringResource {
	private static final String TAG = "StringResource";

	private static final String TYPE_XML = "xml";
	private static final String TYPE_STRING = "string";
	private static final String KEY_VALUE = "value";
	private static final String KEY_NAME = "name";

	public static String load(Context context) {
		int id = context.getResources().getIdentifier(Constant.STRING_RESOURCE, TYPE_XML, context.getPackageName());
		JSONObject tempResource = new JSONObject();

		if (id == 0) {
			DebugLog.w(TAG, "missing dynamicapp resource");
		} else {
			DebugLog.w(TAG, "dynamicapp resource is found.");
			XmlResourceParser xml = context.getResources().getXml(id);
			int eventType = -1;
			String stringValue = "", stringName = "";
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				if (eventType == XmlResourceParser.START_TAG) {
					String strNode = xml.getName();
					DebugLog.w(TAG, "xml tag: " + strNode);
					if (strNode.equals(TYPE_STRING)) {
						stringValue = xml.getAttributeValue(null, KEY_VALUE);
						stringName = xml.getAttributeValue(null, KEY_NAME);
						try {
							tempResource.put(stringName, stringValue);
						} catch (JSONException e) {
							tempResource = new JSONObject();
						}
						DebugLog.w(TAG, "String: " + stringName + " => " + stringValue);
					}
				}
				try {
					eventType = xml.next();
				} catch (XmlPullParserException e) {
					tempResource = new JSONObject();
				} catch (IOException e) {
					tempResource = new JSONObject();
				}
			}

		}
		return tempResource.toString();
	}
}
