package jp.zyyx.dynamicapp.plugins;

import jp.zyyx.dynamicapp.core.DynamicAppPlugin;
import jp.zyyx.dynamicapp.utilities.DebugLog;

/**
 * <p>
 * BasePlugin handles unimplemented classes which classes are already
 * implemented in DynamicApp.js. It logs that the class is unimplemented and
 * returns a javascript processing false.
 * </p>
 * 
 * @author Zyyx
 * @version %I%, %G%
 * @since 1.0
 */
public final class BasePlugin extends DynamicAppPlugin {
	private static final String TAG = "BasePlugin";
	private static BasePlugin instance = null;

	/**
	 * Constructor for unimplemented plug-ins
	 */
	public BasePlugin() {}

	/**
	 * @return the base plug-in instance
	 */
	public static synchronized BasePlugin getInstance() {
		if (instance == null) {
			instance = new BasePlugin();
		}
		return instance;
	}

	@Override
	public void execute() {
		dynamicApp.callJsEvent(PROCESSING_FALSE);
		DebugLog.i(TAG, "Class not yet implemented.");
		instance = null;
	}
}
