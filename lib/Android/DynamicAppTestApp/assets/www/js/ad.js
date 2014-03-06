var Ad = {
	POSITION_TOP : 0,
	POSITION_BOTTOM : 1,

    create : function(position, successCallback, errorCallback, options) {
    	if(position == null) {
    		if(errorCallback) {
    			errorCallback();
    		}
    		return;
    	}
    	
    	var _options = {position: position};
    	if(options.id != null && options.id != undefined)
    		_options.id = options.id;
		if(options.appId != null && options.appId != undefined)
			_options.appId = options.appId;
    	
        DynamicApp.exec(successCallback, errorCallback, "Ad", "create", [_options]);
    },
    
    show : function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, "Ad", "show", [{}]);
    },
    
    hide : function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, "Ad", "hide", [{}]);
    }
    
};

