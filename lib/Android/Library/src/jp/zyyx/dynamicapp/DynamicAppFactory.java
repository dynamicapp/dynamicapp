/**
 * 
 */
package jp.zyyx.dynamicapp;

import jp.zyyx.dynamicapp.core.Command;
import jp.zyyx.dynamicapp.plugins.Ad;
import jp.zyyx.dynamicapp.plugins.AddressBook;
import jp.zyyx.dynamicapp.plugins.AppVersion;
import jp.zyyx.dynamicapp.plugins.BasePlugin;
import jp.zyyx.dynamicapp.plugins.CustomNotification;
import jp.zyyx.dynamicapp.plugins.Database;
import jp.zyyx.dynamicapp.plugins.DynamicAppBluetooth;
import jp.zyyx.dynamicapp.plugins.DynamicAppCamera;
import jp.zyyx.dynamicapp.plugins.DynamicAppFile;
import jp.zyyx.dynamicapp.plugins.DynamicAppQRCodeReader;
import jp.zyyx.dynamicapp.plugins.Encryptor;
import jp.zyyx.dynamicapp.plugins.Felica;
import jp.zyyx.dynamicapp.plugins.ImageDecrypt;
import jp.zyyx.dynamicapp.plugins.LoadingScreen;
import jp.zyyx.dynamicapp.plugins.Movie;
import jp.zyyx.dynamicapp.plugins.ResourceCache;
import jp.zyyx.dynamicapp.plugins.Sound;
import jp.zyyx.dynamicapp.utilities.Constant;
import jp.zyyx.dynamicapp.utilities.DebugLog;
import android.os.Build;
import jp.zyyx.dynamicapp.plugins.Contacts;

/**
 * @author Zyyx
 *
 */
final class DynamicAppFactory {
	private static final String TAG = "DynamicAppFactory";

	/**
	 * @param className		plug-in class name to be instantiated
	 * @param methodName	plug-in method name to be executed
	 * @param params		json format option for the method
	 * @param callbackId	javascript method callback id
	 * @return				DynamicAppPlugin
	 */
	public static Command instantiate(String className, String methodName,
			String params, String callbackId) {

		DebugLog.i(TAG, "Device: " + Build.DEVICE);
		DebugLog.i(TAG, "OS version: " + System.getProperty("os.version"));
		DebugLog.i(TAG, "Build version: " + Build.VERSION.RELEASE);
		DebugLog.i(TAG, "SDK version: " + Build.VERSION.SDK_INT);
		DebugLog.i(TAG, "DynamicAppPlugin: " + className);
		
		Command command = getPlugin(className);
		command.init(methodName, params, callbackId);
		
		return command;
	}
	
	public static Command getPlugin(String className) {
		Command command;
		
		if (className.equalsIgnoreCase(Constant.PLUGIN_NOTIFICATION))
			command = CustomNotification.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_ENCRYPTOR))
			command = Encryptor.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_CAMERA))
			command = DynamicAppCamera.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_SOUND))
			command = Sound.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_MOVIE))
			command = Movie.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_FILE) || 
				 className.equalsIgnoreCase(Constant.PLUGIN_FILE_READER) ||
				 className.equalsIgnoreCase(Constant.PLUGIN_FILE_WRITER))
			command = DynamicAppFile.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_IMAGE_DECRYPTOR))
			command = ImageDecrypt.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_QRCODE_READER))
			command = DynamicAppQRCodeReader.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_LOADING_SCREEN))
			command = LoadingScreen.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_RESOURCE_CACHE))
			command = ResourceCache.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_DATABASE))
			command = Database.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_ADDRESSBOOK))
			command = AddressBook.getInstance();
		else if (className.equalsIgnoreCase(Constant.PLUGIN_BLUETOOTH))
			command = DynamicAppBluetooth.getInstance();
		/* For now, comment out this code.
		else if (className.equalsIgnoreCase(Constant.PLUGIN_BLUETOOTH4LE))
			command = DynamicAppBluetooth4LE.getInstance();
		*/
 		else if (className.equalsIgnoreCase(Constant.PLUGIN_FELICA))
			command = Felica.getInstance();
 		else if (className.equalsIgnoreCase(Constant.PLUGIN_APP_VERSION))
			command = AppVersion.getInstance();
 		else if (className.equalsIgnoreCase(Constant.PLUGIN_CONTACTS))
 			command = Contacts.getInstance();
 		else if (className.equalsIgnoreCase(Constant.PLUGIN_AD))
 			command = Ad.getInstance();
 		else
			command = BasePlugin.getInstance();

		return command;
	}
}
