/**
 * 
 */
package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile;

import java.util.ArrayList;

import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile.BaseClientProfile;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientService.AlertNotificationServiceClient;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;

import com.broadcom.bt.le.api.BleGattID;
import com.broadcom.bt.le.api.BleClientService;

/**
 * @author Zyyx
 * @version %I%, %G%
 * @since 1.0
 * 
 */
public class AlertNotificationProfileClient extends BaseClientProfile {
	private static final String TAG = "AlertNotificationProfileClient";

	private static final BleGattID myUuid = new BleGattID("015f613f-fe1d-475b-b0da-dd947ead9c2a");
	private Context mContext = null;
	private AlertNotificationServiceClient mAlertService = new AlertNotificationServiceClient();

	//private static final BleGattID ALERT_LEVEL_CHARACTERISTIC = new BleGattID("00002a06-0000-1000-8000-00805f9b34fb");
	public static final String FINDME_CONNECTED = "com.broadcom.action.findme_connected";
    public static final String FINDME_DISCONNECTED = "com.broadcom.action.findme_disconnected";

    public final static int ALERT_LEVEL_NONE = 0;
    public final static int ALERT_LEVEL_LOW = 1;
    public final static int ALERT_LEVEL_HIGH = 2;

//    private static final BleGattID CHARACTERISTICS_ALERT_CATEGORY_ID = new BleGattID(0x2A43);
//    private static final BleGattID CHARACTERISTICS_ALERT_CATEGORY_ID_BIT_MASK = new BleGattID(0x2A42);
//    private static final BleGattID CHARACTERISTICS_ALERT_LEVEL = new BleGattID(0x2A06);
//    private static final BleGattID CHARACTERISTICS_ALERT_NOTIFICATION_CONTROL_POINT = new BleGattID(0x2A44);
//    private static final BleGattID CHARACTERISTICS_ALERT_STATUS = new BleGattID(0x2A3F);

//    private Peripheral peripheral = null;
    
	public AlertNotificationProfileClient(Context context) {
		super(context, myUuid);
		mContext = context;

		DebugLog.i(TAG, "AlertNotificationProfileClient");

		ArrayList<BleClientService> services = new ArrayList<BleClientService>();
		services.add(mAlertService);

		init(services, null);
	}

	public synchronized void deregister() throws InterruptedException {
		deregisterProfile();
		wait(5000);
	}

	public void alert(BluetoothDevice device) {
		/*BleCharacteristic alertLevelCharacteristic = mAlertService
				.getCharacteristic(device, ALERT_LEVEL_CHARACTERISTIC);

		byte[] value = { AlertNotificationProfileClient.ALERT_LEVEL_HIGH };
		alertLevelCharacteristic.setValue(value);

		mAlertService.writeCharacteristic(device, 0,
				alertLevelCharacteristic);*/
		
//		BleCharacteristic alertCategoryIdCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_CATEGORY_ID);
		
//		BleCharacteristic alertCategoryIdBitMaskCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_CATEGORY_ID_BIT_MASK);
		
//		BleCharacteristic alertLevelCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_LEVEL);
		
//		BleCharacteristic alertNotificationControlPointCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_NOTIFICATION_CONTROL_POINT);
		
//		BleCharacteristic alertStatusCharacteristic = mAlertService
//				.getCharacteristic(device, CHARACTERISTICS_ALERT_STATUS);
	}

	public void onInitialized(boolean success) {
		DebugLog.i(TAG, "onInitialized");
		if (success) {
			registerProfile();
		}
	}

	public void onDeviceConnected(BluetoothDevice device) {
		DebugLog.i(TAG, "onDeviceConnected");
		refresh(device);
		
//		String deviceName = device.getName();
//		String macAddress = device.getAddress();
//		peripheral = new Peripheral(deviceName, macAddress);
	}

	public void onDeviceDisconnected(BluetoothDevice device) {
		DebugLog.i(TAG, "onDeviceDisconnected");

		Intent intent = new Intent();
		intent.setAction(FINDME_DISCONNECTED);
		intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
		mContext.sendBroadcast(intent);

		connectBackground(device);
	}

	public void onRefreshed(BluetoothDevice device) {
		DebugLog.i(TAG, "onRefreshed");

		Intent intent = new Intent();
		intent.setAction(FINDME_CONNECTED);
		intent.putExtra(BluetoothDevice.EXTRA_DEVICE, device.getAddress());
		mContext.sendBroadcast(intent);
	}

	public void onProfileRegistered() {
		DebugLog.i(TAG, "onProfileRegistered");
	}

	public void onProfileDeregistered() {
		DebugLog.i(TAG, "onProfileDeregistered");
		notifyAll();
	}

}
