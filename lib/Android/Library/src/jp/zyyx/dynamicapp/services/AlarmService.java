/**
 * 
 */
package jp.zyyx.dynamicapp.services;

import jp.zyyx.dynamicapp.plugins.CustomNotification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * <p>
 * AlarmService class fires an alarm to the view
 * without redirecting to another view.
 * </p>
 * 
 * @author Zyyx
 * @version 1.0
 * @since 1.0
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

