var Felica = {
    getIdM : function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, "Felica", "getIdM", [{}]);
    },
    getPMm: function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, "Felica", "getPMm", [{}]);
    },
    requestSystemCode: function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, "Felica", "requestSystemCode", [{}]);
    },
    requestServiceCode: function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, "Felica", "requestServiceCode", [{}]);
    },
    requestResponse: function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, "Felica", "requestResponse", [{}]);
    },
    readWithoutEncryption: function(serviceCode, address, successCallback, errorCallback) {
    	if(address == null || address.length == 0) {
    		if(errorCallback) {
    			errorCallback();
    		}
    		return;
    	}
    	
    	var options = {
    		serviceCode: serviceCode,
    		address: address
    	}
        DynamicApp.exec(successCallback, errorCallback, "Felica", "readWithoutEncryption", [options]);
    },
    writeWithoutEncryption: function(serviceCode, address, buffer, successCallback, errorCallback) {
    	if(address == null || address.length == 0) {
    		if(errorCallback) {
    			errorCallback();
    		}
    		return;
    	}
    	
    	var options = {
    		serviceCode: serviceCode,
    		address: address,
    		buffer: buffer
    	}
        DynamicApp.exec(successCallback, errorCallback, "Felica", "writeWithoutEncryption", [options]);
    }
};