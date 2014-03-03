/**
 * 
 */
package jp.zyyx.dynamicapp.core;

import android.content.Intent;
import android.content.res.Configuration;

/** 
 * <p>Interface for handling dynamic function calls on
 * URL change. Needs to be implemented in a class
 * to execute its methods dynamically on URL change.
 * </p>
 * 
 * <p>
 * Public Methods:
 * <ul>
 * <li>init
 * <li>execute
 * <li>onSuccess
 * <li>onError
 * <li>onPause
 * <li>onResume
 * </ul>
 * </p>
 * 
 * @author Zyyx
 * @version 1.0
 * @since 1.0
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
	 * method to be called when DynamicAppActivity calls onPause
	 * can be used to interrupt current executing plug-in
	 */
	public void onPause();
	/**
	 * method to be called when DynamicAppActivity calls onResume
	 * can be use to resumed interrupted plug-ins
	 */
	public void onResume();
	
	/**
	 * method to be called when DynamicAppActivity calls onActivityResult
	 * can be use to activity results inside the plug-in
	 */
	void onActivityResult(int requestCode, int resultCode, Intent intent);
	
	/**
	 * method to be called when DynamicAppActivity calls onDestroy
	 * can be use to release resources;
	 */
	public void onDestroy();
	
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
