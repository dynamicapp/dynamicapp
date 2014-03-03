package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile;

import android.content.Context;

import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile.BaseClientProfile;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientService.ImmediateAlertServiceClient;

import com.broadcom.bt.le.api.BleGattID;

public class FindMeProfileClient extends BaseClientProfile {
    private static final BleGattID myUuid = new BleGattID("015f613f-fe1d-475b-b0da-dd947ead9c2d");

	private ImmediateAlertServiceClient mImmediateAlertService = new ImmediateAlertServiceClient();
	
	public FindMeProfileClient(Context context) {
		super(context, myUuid);
		
		addService(mImmediateAlertService);

		initialize();		
	}
	
   
}
