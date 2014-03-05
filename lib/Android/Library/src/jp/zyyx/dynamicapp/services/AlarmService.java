package jp.zyyx.dynamicapp.services;

import jp.zyyx.dynamicapp.plugins.CustomNotification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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
public class AlarmService extends Service {

	public static final String INTENT_NOTIFY = "INTENT_NOTIFY";
	 
	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent.getBooleanExtra(INTENT_NOTIFY, false))
            showNotification(intent);
        return START_NOT_STICKY;
    }
	
	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}
	
	/**
     * Creates a notification and shows it in the OS drag-down status bar
     */
    private void showNotification(Intent intent) {
        CustomNotification.showNotification(intent, this);
        stopSelf();
    }
}

