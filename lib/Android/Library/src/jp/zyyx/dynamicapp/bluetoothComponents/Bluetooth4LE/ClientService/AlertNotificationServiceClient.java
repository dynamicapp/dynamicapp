package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientService;

import jp.zyyx.dynamicapp.utilities.DebugLog;
import android.bluetooth.BluetoothDevice;

import com.broadcom.bt.le.api.BleCharacteristic;
import com.broadcom.bt.le.api.BleClientService;
import com.broadcom.bt.le.api.BleGattID;

/**
 * @author		Zyyx
 * @version     %I%, %G%
 * @since       1.0
 *
 */
public class AlertNotificationServiceClient extends BleClientService {
	private static final String TAG = "AlertNotificationServiceClient";

	static public BleGattID myUuid = new BleGattID(BtLE4ClientServices.makeUUIDs(BtLE4ClientServices.SERVICE_ALERT_NOTIFICATION));
	//static public BleGattID myUuid = new BleGattID("a60f35f0-b93a-11de-8a39-08002009c666");
	
	
	public AlertNotificationServiceClient() {
		super(myUuid);
	}
	
	public void onWriteCharacteristicComplete(int status, BluetoothDevice d,
            BleCharacteristic characteristic) {
        DebugLog.i(TAG, "onWriteCharacteristicComplete");
    }

    public void characteristicsRetrieved(BluetoothDevice d) {
    	DebugLog.i(TAG, "characteristicsRetrieved");
    }

    public void onRefreshComplete(BluetoothDevice d) {
    	DebugLog.i(TAG, "onRefreshComplete");
    }

    public void onSetCharacteristicAuthRequirement(BluetoothDevice d,
            BleCharacteristic characteristic, int instanceID) {
    	DebugLog.i(TAG, "onSetCharacteristicAuthRequirement");
    }

    public void onReadCharacteristicComplete(BluetoothDevice d, BleCharacteristic characteristic) {
    	DebugLog.i(TAG, "refreshOneCharacteristicComplete");
    }

    public void onCharacteristicChanged(BluetoothDevice d, BleCharacteristic characteristic) {
    	DebugLog.i(TAG, "onCharacteristicChanged");
    }
}
