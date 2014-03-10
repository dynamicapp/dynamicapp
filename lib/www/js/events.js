function createEvent(type, data) {
    var event = document.createEvent('Events');
    event.initEvent(type, false, false);
    if (data) {
        for (var i in data) {
            if (data.hasOwnProperty(i)) {
                event[i] = data[i];x
            }
        }
    }
    return event;
}

var Events = {
	addedbackpress : false,

    addListener : function(type, callback) {
		if(type == null) {
			return;
		}
		
    	if(type != Events.TYPE_BACK_PRESS && 
			type != Events.TYPE_PAUSE && 
			type != Events.TYPE_RESUME) {
    		return;
    	}
		
		console.log("addEventListener");
		document.addEventListener(type, callback, false);
		
		if(type == Events.TYPE_BACK_PRESS) {
			this.addedbackpress = true;
		}
    },

	removeListener : function(type, callback) {
    	if(type != Events.TYPE_BACK_PRESS && 
			type != Events.TYPE_PAUSE && 
			type != Events.TYPE_RESUME) {
    		return;
    	}
		
		console.log("removeEventListener");
		document.removeEventListener(type, callback, false);
		if(type == Events.TYPE_BACK_PRESS) {
			this.addedbackpress = false
		}
	},
	
	fireEvent: function(type, data) {
		console.log("fireEvent");
        var evt = createEvent(type, data);
        document.dispatchEvent(evt);
    }
	
};

Events.TYPE_BACK_PRESS = "backpress";
Events.TYPE_PAUSE = "pause";
Events.TYPE_RESUME = "resume";

