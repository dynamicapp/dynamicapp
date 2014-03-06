package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE;

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
public class Peripheral {

	private String deviceName = null; // The name of a android device.
	private String id = null; // MAC Address of android bluetooth device.

	private static final int STATE_UNKNOWN = -1;
	private int state = STATE_UNKNOWN;

	public Peripheral(String deviceName, String id) {
		this.setDeviceName(deviceName);
		this.setId(id);
	}

	public interface OnWriteCharacteristicListener {
		/**
		 * The callback that is called after finishing to write a value to the
		 * characteristic.
		 */
		void onSuccess();

		/**
		 * The callback that is called if an error occurs when attempting to
		 * write a value to the characteristic.
		 */
		void onError();
	}

	public interface OnReadCharacteristicListener {
		/**
		 * The callback that is called after finishing to read a value from the
		 * characteristic
		 * 
		 * @param value
		 *            The value that is read from the characteristic. (String)
		 */
		void onSuccess(String value);

		/**
		 * The callback that is called if an error occurs when attempting to
		 * read a value from the characteristic.
		 */
		void onError();
	}

	public interface OnWriteDescriptorListener {
		/**
		 * The callback that is called after finishing to write a value to the
		 * descriptor.
		 */
		void onSuccess();

		/**
		 * The callback that is called if an error occurs when attempting to
		 * write a value to the descriptor.
		 */
		void onError();
	}

	public interface OnReadDescriptorListener {
		/**
		 * The callback that is called after finishing to read a value from the
		 * descriptor.
		 * 
		 * @param value
		 *            The value that is read from the descriptor. (String)
		 */
		void onSuccess(String value);

		/**
		 * The callback that is called if an error occurs when attempting to
		 * read a value from the descriptor.
		 */
		void onError();
	}

	/**
	 * Writes a value to the Characteristic. Whether it can write a value to the
	 * characteristic depends on Bluetooth4 LE Specifications.
	 * 
	 * @param value
	 *            The value that is written to characteristic.(String)
	 * @param valueType
	 *            The type of value.
	 * @param characteristic
	 *            The characteristic that is written the value.
	 * @param writeType
	 *            The type of write. <br/>
	 *            &nbsp;&nbsp;- WriteWithResponse : 0 <br/>
	 *            &nbsp;&nbsp;- WriteWithoutResponse : 1
	 * @param listener
	 *            The OnWriteCharacteristicListener that will handle success and
	 *            error callback when writing characteristic
	 */
	public void writeValueForCharacteristic(final String value,
			final int valueTyoe,
			final int characteristic, final int writeType,
			OnWriteCharacteristicListener listener) {

	}

	/**
	 * Reads a value from the Characteristic. Whether it can read a value from
	 * the characteristic depends on Bluetooth4 LE Specifications.
	 * 
	 * @param characteristic
	 *            The characteristic that reads the value from.
	 * @param valueType
	 *            The type of value.
	 * @param listener
	 *            The OnReadCharacteristicListener that will handle success and
	 *            error callback when reading characteristic
	 */
	public void readValueForCharacteristic(final int characteristic,
			final int valueType,
			OnReadCharacteristicListener listener) {

	}

	/**
	 * Writes a value to the Descriptor. Whether it can write a value to the
	 * descriptor depends on Bluetooth4 LE Specifications.
	 * 
	 * @param value
	 *            The value that is written to descriptor.
	 * @param value
	 *            The type of value.
	 * @param characteristic
	 *            The characteristic that has the descriptor.
	 * @param descriptor
	 *            The descriptor that is written a value to.
	 * @param listener
	 *            The OnWriteDescriptorListener that will handle success and
	 *            error callback when writing descriptor
	 */
	public void writeValueForDescriptor(final String value,
			final int valueType,
			final int characteristic, final int descriptor,
			OnWriteDescriptorListener listener) {

	}

	/**
	 * Reads a value from the Characteristic. Whether it can read a value from
	 * descriptor depends on Bluetooth4 LE Specifications.
	 * 
	 * @param characteristic
	 *            The characteristic that has the descriptor.
	 * @param descriptor
	 *            The descriptor that reads the value from.
	 * @param valueType
	 *            The type of value.
	 * @param listener
	 *            The OnReadDescriptorListener that will handle success and
	 *            error callback when reading descriptor
	 */
	public void readValueForDescriptor(final int characteristic,
			final int descriptor, 
			final int valueType, OnReadDescriptorListener listener) {

	}

	/**
	 * @return the deviceName
	 */
	public String getDeviceName() {
		return deviceName;
	}

	/**
	 * @param deviceName
	 *            the deviceName to set
	 */
	public void setDeviceName(final String deviceName) {
		this.deviceName = deviceName;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(final int state, boolean notifyJS) {
		this.state = state;
		if (notifyJS) {
			String script = "Bluetooth4LE.onStateChanged(\"" + this.deviceName + "\","
					+ "\"" + this.id + "\"," + this.state + ");";
			((DynamicAppActivity)Utilities.dynamicAppActivityRef).callJsEvent(script);
		}
	}

}
