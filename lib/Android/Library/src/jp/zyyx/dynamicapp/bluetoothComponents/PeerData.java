package jp.zyyx.dynamicapp.bluetoothComponents;

import jp.zyyx.dynamicapp.DynamicAppActivity;
import jp.zyyx.dynamicapp.utilities.Utilities;

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
public class PeerData {
	
	private static final int STATE_UNKNOWN = -1;
	
	private String deviceName = null;
	private String id = null;
	private int state = STATE_UNKNOWN;
	
	public PeerData(String deviceName, String id) {
		this.setDeviceName(deviceName);
		this.setId(id);
		//this.setState(STATE_UNKNOWN);
	}
	
	public PeerData(String deviceName, String id, int state) {
		this.setDeviceName(deviceName);
		this.setId(id);
		this.setState(state);
	}
	
	/**
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}
	/**
	 * @param deviceName the deviceName to set
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(int state) {
		this.state = state;
		this.onStateChange();
	}
	
	protected void onStateChange() {
		String script = "Bluetooth.onStateChanged(\"" + this.deviceName + "\","
				+ "\"" + this.id + "\"," + this.state + ");";
		if(this.state != STATE_UNKNOWN)
			((DynamicAppActivity)Utilities.dynamicAppActivityRef).callJsEvent(script);
	}

}
