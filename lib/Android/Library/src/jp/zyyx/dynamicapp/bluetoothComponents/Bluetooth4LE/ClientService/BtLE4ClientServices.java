/**
 * 
 */
package jp.zyyx.dynamicapp.bluetoothComponents.Bluetooth4LE.ClientService;

/**
 * @author		Zyyx
 * @version     %I%, %G%
 * @since       1.0
 *
 */
public class BtLE4ClientServices {
	
	public static final String SERVICE_ALERT_NOTIFICATION = "1811";
	public static final String SERVICE_BATTERY_SERVICE = "180F";
	public static final String SERVICE_BLOOD_PRESSURE = "1810";
	public static final String SERVICE_CURRENT_TIME = "1805";
	public static final String SERVICE_CYCLINGSPEED_AND_CADENCE = "1816";
	public static final String SERVICE_DEVICE_INFORMATION = "180A";
	public static final String SERVICE_GENERIC_ACCESS = "1800";
	public static final String SERVICE_GENERIC_ATTRIBUTE = "1801";
	public static final String SERVICE_GLUCOSE = "1808";
	public static final String SERVICE_HEALTH_THERMOMETER = "1809";
	public static final String SERVICE_HEART_RATE = "180D";
	public static final String SERVICE_HUMAN_INTERFACE_DEVICE = "1812";
	public static final String SERVICE_IMMEDIATE_ALERT = "1802";
	public static final String SERVICE_HUMAN_LINK_LOSS = "1803";
	public static final String SERVICE_NEXT_DST_CHANGE = "1807";
	public static final String SERVICE_PHONE_ALERT_STATUS = "180E";
	public static final String SERVICE_REFERENCE_TIME_UPDATE = "1806";
	public static final String SERVICE_SCAN_PARAMETERS = "1813";
	public static final String SERVICE_TX_POWER = "1804";
	
	public static String makeUUIDs(String serviceId) {
		String sUUID = "0000";
		sUUID += serviceId;
		sUUID += "-0000-1000-8000-00805f9b34fb";
		
		return sUUID; 		
	}
	
	public static boolean hasService(String service) {
		boolean isPresent = false;
	
		if(SERVICE_ALERT_NOTIFICATION.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_BATTERY_SERVICE.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_BLOOD_PRESSURE.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_CURRENT_TIME.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_CYCLINGSPEED_AND_CADENCE.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_DEVICE_INFORMATION.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_GENERIC_ACCESS.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_GENERIC_ATTRIBUTE.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_GLUCOSE.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_HEALTH_THERMOMETER.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_HEART_RATE.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_HUMAN_INTERFACE_DEVICE.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_IMMEDIATE_ALERT.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_HUMAN_LINK_LOSS.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_NEXT_DST_CHANGE.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_PHONE_ALERT_STATUS.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_REFERENCE_TIME_UPDATE.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_SCAN_PARAMETERS.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		} else if(SERVICE_TX_POWER.compareToIgnoreCase(service) == 0) {
			isPresent = true;
		}		
		
		return isPresent;
	}
}
