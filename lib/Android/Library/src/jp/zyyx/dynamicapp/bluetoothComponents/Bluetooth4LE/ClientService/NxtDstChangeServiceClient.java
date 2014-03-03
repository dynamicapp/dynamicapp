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
public class NxtDstChangeServiceClient extends BleClientService {
	private static final String TAG = "NxtDstChangeServiceClient";

	static public BleGattID myUuid = new BleGattID(BtLE4ClientServices.makeUUIDs(BtLE4ClientServices.SERVICE_NEXT_DST_CHANGE));
	
	public NxtDstChangeServiceClient() {
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
