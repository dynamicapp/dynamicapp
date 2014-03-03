package jp.zyyx.dynamicapp.plugins;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

import jp.zyyx.dynamicapp.DynamicAppActivity;
import jp.zyyx.dynamicapp.JSONObjectWrapper;
import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.services.AlarmService;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import jp.zyyx.dynamicapp.utilities.DynamicAppUtils;

import org.json.JSONException;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * <p>
 * Class for handling android notifications
 * </p>
 * 
 * <p>
 * Public Methods:
 * <ul>
 * <li>notify
 * <li>cancelNotification
 * </ul>
 * </p>
 * 
 * <p>
 * For example:
 * 
 * <pre>
 * CustomNotification.notify(mContext, getApplicationContext(), date, message,
 * 		notificationCtr++, hasAction, actionTitle, icon);
 * CustomNotification.cancelNotification();
 * </pre>
 * 
 * </p>
 * 
 * @author Zyyx
 * @version 1.0
 * @since 1.0
 */
public class CustomNotification extends DynamicAppPlugin {
	private static final String TAG = "CustomNotification";

	private static int icon;
	private static int badge;
	private static long time;
//	private static boolean hasAction;

	private static String message;
	private static String date;
	private static String action;
	private static Context context;
	private PendingIntent mAlarmSender;
	private boolean cancelled = true;

	private static CustomNotification instance = null;
	private static boolean statusBarIsShown = false;
	
	private Map<Integer, PendingIntent> alarms = null;

	private CustomNotification() {
	}

	/**
	 * @return	the CustomNotification instance
	 */
	public static synchronized CustomNotification getInstance() {
		if (instance == null) {
			instance = new CustomNotification();
		}
		return instance;
	}

	@SuppressLint("UseSparseArrays")
	public void init(String methodName, String params, String callbackId) {
		super.init(methodName, params, callbackId);
		JSONObjectWrapper param = null;

		try {
			param = new JSONObjectWrapper(params);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (param != null) {
			CustomNotification.date = param.get("date", "");
			CustomNotification.message = param.get("message", "");
			CustomNotification.badge = param.get("badge", 0);
//			CustomNotification.hasAction = param.get("hasAction", true);
			CustomNotification.action = param.get("action", "");
			CustomNotification.icon = android.R.drawable.ic_dialog_info;
		}

		context = dynamicApp.getApplicationContext();
		
		if(alarms == null) {
			alarms = new HashMap<Integer, PendingIntent>();
		}
	}

	public CustomNotification(String date, String message, int badge,
			boolean hasAction, String action, int icon) {

		CustomNotification.date = date;
		CustomNotification.message = message;
		CustomNotification.badge = badge;
//		CustomNotification.hasAction = hasAction;
		CustomNotification.action = action;
		CustomNotification.icon = android.R.drawable.ic_dialog_info;

		this.setContext((DynamicAppActivity) DynamicAppUtils.dynamicAppActivityRef);
		context = dynamicApp.getApplicationContext();
	}

	/**
	 * 
	 */
	public void customNotify() {
		new Thread(new Runnable() {
			@SuppressLint("SimpleDateFormat")
			@Override
			public void run() {
				try {
					Thread.sleep(300);
					long time = 0;
					String date1 = CustomNotification.date;
					try {
						SimpleDateFormat defaultDate = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						time = defaultDate.parse(date1).getTime();
					} catch (Exception e) {
						time = System.currentTimeMillis();
						DebugLog.e(TAG, e.getLocalizedMessage());
					}
					CustomNotification.time = time;
					setAlarmNotification();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
		}}).start();
	}

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void showNotification(Intent intent, Context c) {
		if (context == null) {
			context = c;
		}
		NotificationManager nm = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Intent _intent = new Intent();

		/*if (hasAction) {
			if (action.equalsIgnoreCase("View")) {
				_intent = context.getPackageManager()
						.getLaunchIntentForPackage(context.getPackageName());
			}
		}*/
		_intent = context.getPackageManager()
				.getLaunchIntentForPackage(context.getPackageName());

		int _icon = icon;
		String _message = message;
		int _badge = badge;
		long _time = time;
		String _action = action;

		if (intent != null) {
			_icon = intent.getIntExtra("icon", 0);
			_message = intent.getStringExtra("message");
			_badge = intent.getIntExtra("badge", 0);
			_time = intent.getLongExtra("time", new Date().getTime());
			_action = intent.getStringExtra("action");
		}

		Notification n = null;
		PendingIntent pi = PendingIntent.getActivity(context, 0, _intent, 0);
		if (Build.VERSION.SDK_INT >= 11) {
			Notification.Builder builder = new Notification.Builder(context);
			builder.setContentIntent(pi).setSmallIcon(_icon)
					.setTicker(_message).setNumber(_badge).setWhen(_time)
					.setAutoCancel(true).setContentTitle(_action)
					.setContentText(_message);
			n = builder.getNotification();
		} else {
			n = new Notification();
			n.contentIntent = pi;
			n.icon = android.R.drawable.ic_dialog_info;
			n.tickerText = _message;
			n.number = _badge;
			n.when = _time;
			n.flags |= Notification.FLAG_AUTO_CANCEL;
			n.setLatestEventInfo(context, _action, _message, pi);
		}

		nm.notify((int) (_time / 1000), n);
		showStatusBar();
	}

	private void setAlarmNotification() {
		boolean ok = true;
		try {
			AlarmManager alarmManager = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);
			Intent myIntent = new Intent(context, AlarmService.class);
			myIntent.setData(Uri.parse("ztime:" + time));
			myIntent.putExtra(AlarmService.INTENT_NOTIFY, true);
			myIntent.putExtra("icon", icon);
			myIntent.putExtra("message", message);
			myIntent.putExtra("badge", badge);
			myIntent.putExtra("time", time);
			myIntent.putExtra("action", action);

			mAlarmSender = PendingIntent.getService(context, 0, myIntent, 0);
	
			alarmManager.set(AlarmManager.RTC_WAKEUP, CustomNotification.time, mAlarmSender);
			alarms.put(Integer.valueOf((int)(time/1000)), PendingIntent.getService(context, 0, myIntent, 0));
		} catch(Exception e) {
			ok = false;
		} finally {
			if(ok)
				this.onSuccess();
			else {
				this.onError();
			}
		}
	}

	/**
	 * 
	 */
	@SuppressLint("SimpleDateFormat")
	public void cancelNotification() {
		cancelled = true;
		try {
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) context
					.getSystemService(ns);
			int id = (int)(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(param.get("date", "2012-01-01 00:00:00")).getTime()/1000);
			mNotificationManager.cancel(id);

			AlarmManager am = (AlarmManager) context
					.getSystemService(Context.ALARM_SERVICE);

			PendingIntent pi = alarms.get(Integer.valueOf(id));
			if(pi != null) {
				am.cancel(pi);
				alarms.remove(id);
			}

			hideStatusBar();
		} catch (Exception e) {
			cancelled = false;
		}

		if (cancelled)
			this.onSuccess();
		else
			this.onError();
	}

	/**
	 * This function shows the notification bar when there is a new notification
	 */
	private static void showStatusBar() {
		try {
			new Thread(new Runnable() {
				public void run() {
					if(!statusBarIsShown) {
						dynamicApp.runOnUiThread(new Runnable() {
							public void run() {
								statusBarIsShown = true;
								dynamicApp.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
							}
						});
					}
				}
			}).start();
		} catch (Exception ex) {
			DebugLog.e(TAG, "error:" + ex);
		}
	}

	/**
	 * This function hides the notification bar
	 */
	public static void hideStatusBar() {
		try {
			new Thread(new Runnable() {
				@Override
				public void run() {
					if(statusBarIsShown) {
						dynamicApp.runOnUiThread(new Runnable() {
							public void run() {
								statusBarIsShown = false;
								dynamicApp.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
								dynamicApp.getWebView().getLayoutParams().height = RelativeLayout.LayoutParams.MATCH_PARENT;
							}
						});
					}
				}
			}).start();
		} catch (Exception ex) {
			DebugLog.e(TAG, "error:" + ex);
		}
	}

	@Override
	public void execute() {
		DebugLog.i(TAG, "parameters are: " + params);

		if (methodName.equalsIgnoreCase("notify")) {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			this.customNotify();
		} else {
			dynamicApp.callJsEvent(PROCESSING_FALSE);
			this.cancelNotification();
		}
		
		
	}
	
	@Override
	public void onBackKeyDown() {
		statusBarIsShown = false;
		DynamicAppUtils.currentCommandRef = null;
		instance = null;
	}
}
