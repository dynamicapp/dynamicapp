package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile;

import android.content.Context;
import jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientProfile.BaseClientProfile;
import com.broadcom.bt.le.api.BleGattID;

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
public class HeartRateProfileClient extends BaseClientProfile {
	private static final BleGattID myUuid = new BleGattID("015f613f-fe1d-475b-b0da-dd947ead9c3a");

	public HeartRateProfileClient(Context context) {
		super(context, myUuid);
	}
}
