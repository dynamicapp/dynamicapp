package jp.zyyx.dynamicapp.plugins;

import jp.zyyx.dynamicapp.core.Plugin;
import android.content.pm.PackageManager.NameNotFoundException;

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
public class AppVersion extends Plugin {
	private static AppVersion instance = null;
	
	private AppVersion() {
		super();
	}
	
	public static synchronized AppVersion getInstance() {
        if (instance == null) {
	            instance = new AppVersion();
	    }
	    return instance;
	}

	@Override
	public void execute() {
		if (methodName.equalsIgnoreCase("get")) {
			String version = "";
			try {
				version = mainActivity.getPackageManager().getPackageInfo(mainActivity.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			if(version.length() < 1) {
				version = "";
			}
			mainActivity.callJsEvent(PROCESSING_FALSE);
			onSuccess(version, callbackId, false);
		}
	}
}

