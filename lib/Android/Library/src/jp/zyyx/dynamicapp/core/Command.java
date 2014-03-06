package jp.zyyx.dynamicapp.core;

import android.content.Intent;
import android.content.res.Configuration;

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
public interface Command {

	/**
	 * @param methodName	method name to be executed
	 * @param params		json format options for the method
	 * @param callbackId	id for the javascript callback method
	 */
	public void init(String methodName, String params, String callbackId);

	/**
	 * method for executing DynamicAppPlugin
	 */
	public void execute();

	/**
	 * method for handling successful method execution
	 */
	public void onSuccess();

	/**
	 * method for handling unsuccessful method execution
	 */
	public void onError();

	/**
	 * method to be called when DynamicAppActivity calls onResume
	 * can be use to resumed interrupted plug-ins
	 */
	public void onResume();

	/**
	 * method to be called when DynamicAppActivity calls onPause
	 * can be used to interrupt current executing plug-in
	 */
	public void onPause();

	/**
	 * method to be called when DynamicAppActivity calls onDestroy
	 * can be use to release resources;
	 */
	public void onDestroy();

	/**
	 * method to be called when DynamicAppActivity calls onActivityResult
	 * can be use to activity results inside the plug-in
	 */
	void onActivityResult(int requestCode, int resultCode, Intent intent);

	/**
	 * method to be called when DynamicAppActivity calls onKeyDown Back
	 * can be use to handle back key down;
	 */
	public void onBackKeyDown();

	/**
	 * method to be called when DynamicAppActivity calls onConfigurationChanged
	 * can be use to handle back key down;
	 */
	public void onConfigurationChanged(Configuration config);
}

