package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile;

import android.content.Context;
import com.broadcom.bt.le.api.BleGattID;

public class CyclingSpeedAndCadenceProfileClient extends BaseClientProfile {
	private static final BleGattID myUuid = new BleGattID("015f613f-fe1d-475b-b0da-dd947ead9c2c");

	public CyclingSpeedAndCadenceProfileClient(Context context) {
		super(context, myUuid);
	}
}
