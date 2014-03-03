package jp.zyyx.dynamicapp.plugins;

import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import android.content.pm.PackageManager.NameNotFoundException;

public class AppVersion extends DynamicAppPlugin {
	
	private static AppVersion instance = null;
	
	private AppVersion() {}
	
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
				version = dynamicApp.getPackageManager().getPackageInfo(dynamicApp.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}

			if(version.length() < 1) {
				version = "";
			}
			
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			onSuccess(version, callbackId, false);
		}
	}

}
