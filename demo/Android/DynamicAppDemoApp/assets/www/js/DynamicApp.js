if ("-ms-user-select" in document.documentElement.style && 
		navigator.userAgent.match(/IEMobile\/10\.0/)) {
	var msViewportStyle = document.createElement("style");
	msViewportStyle.appendChild(
		document.createTextNode("@-ms-viewport{width:device-width !important;user-zoom: fixed;max-zoom: 1;min-zoom: 1}")
	);
            
	msViewportStyle.appendChild(
		document.createTextNode("html, div {-ms-text-size-adjust: 150%;}")
	);
            
	msViewportStyle.appendChild(
		document.createTextNode("input[type='text'], input[type='input'], input[type='button']{font-size: 1.5em}")
	);
            
	document.getElementsByTagName("head")[0].appendChild(msViewportStyle);
}

var DynamicApp = {
    callbacks : {},
    callbackId : 0,
    processing : false,

    callbackStatus : {
    		NO_RESULT: 0,
    		OK: 1,
    		CLASS_NOT_FOUND_EXCEPTION: 2,
    		ILLEGAL_ACCESS_EXCEPTION: 3,
    		INSTANTIATION_EXCEPTION: 4,
    		MALFORMED_URL_EXCEPTION: 5,
    		IO_EXCEPTION: 6,
    		INVALID_ACTION: 7,
    		JSON_EXCEPTION: 8,
    		ERROR: 9
    },

    callbackSuccess : function(callbackId, args) {
        if (this.callbacks[callbackId]) {
            // If result is to be sent to callback
            if (args.status == DynamicApp.callbackStatus.OK) {
                try {
                    if (this.callbacks[callbackId].success) {
                        this.callbacks[callbackId].success(args.message);
                    }
                } catch (e) {
                }
            }
            
            if(!args.keepCallback) {
                delete this.callbacks[callbackId];
            }
        }
    },

    callbackError : function(callbackId, args) {
        if (this.callbacks[callbackId]) {
            try {
                if (this.callbacks[callbackId].fail) {
                    this.callbacks[callbackId].fail(args.message);
                }
            } catch (e) {
            }
        
            if(!args.keepCallback) {
                delete this.callbacks[callbackId];
            }
        }
    },

    exec : function() {
        if(this.processing) {
            return;
        }
        
        var successCallback, failCallback, service, action, actionArgs;
        var callbackId = null;
        
        successCallback = arguments[0];
        failCallback = arguments[1];
        service = arguments[2];
        action = arguments[3];
        actionArgs = arguments[4];
        
        if (successCallback || failCallback) {
            callbackId = service + this.callbackId++;
            this.callbacks[callbackId] = {success:successCallback, fail:failCallback};
        }
        
        var options = null;
        for (var i = 0; i < actionArgs.length; ++i) {
            var arg = actionArgs[i];
            if (arg == undefined || arg == null) {
                continue;
            } else if (typeof(arg) == 'object') {
                options = arg;
            } else {
                //arguments.push(arg);
            }
        }

        var location = "dynamicapp://" + service + "." + action + "/" + encodeURIComponent(JSON.stringify(options)) + "?callbackId=" + callbackId;
		if(navigator.userAgent.match(/Windows Phone/i)) {
			setTimeout(function(){window.external.Notify(location);}, 10);
        } else {
			setTimeout(function(){document.location = location;}, 10);
		}
	
		this.processing = true;
    }, 
    
    /**
     * Create a UUID
     *
     * @return
     */
    createUUID : function() {
        return DynamicApp.UUIDcreatePart(4) + '-' +
        DynamicApp.UUIDcreatePart(2) + '-' +
        DynamicApp.UUIDcreatePart(2) + '-' +
        DynamicApp.UUIDcreatePart(2) + '-' +
        DynamicApp.UUIDcreatePart(6);
    },
    
    UUIDcreatePart : function(length) {
        var uuidpart = "";
        for (var i=0; i<length; i++) {
            var uuidchar = parseInt((Math.random() * 256)).toString(16);
            if (uuidchar.length == 1) {
                uuidchar = "0" + uuidchar;
            }
            uuidpart += uuidchar;
        }
        return uuidpart;
    },
    
    addOnloadEvent : function(func) {
		var oldonload = window.onload;
		if (typeof window.onload != 'function') {
			window.onload = func;
		} else {
			window.onload = function() {
				if (oldonload) {
					oldonload();
				}
				func();
			}
		}
	}
};

document.write("<script type='text/javascript' src='js/appVersion.js'></script>");
document.write("<script type='text/javascript' src='js/camera.js'></script>");
document.write("<script type='text/javascript' src='js/sound.js'></script>");
document.write("<script type='text/javascript' src='js/movie.js'></script>");
document.write("<script type='text/javascript' src='js/file.js'></script>");
document.write("<script type='text/javascript' src='js/image-decrypt.js'></script>");
document.write("<script type='text/javascript' src='js/encryptor.js'></script>");
document.write("<script type='text/javascript' src='js/notification.js'></script>");
document.write("<script type='text/javascript' src='js/loadingScreen.js'></script>");
document.write("<script type='text/javascript' src='js/cache.js'></script>");
document.write("<script type='text/javascript' src='js/qrReader.js'></script>");
document.write("<script type='text/javascript' src='js/database.js'></script>");
document.write("<script type='text/javascript' src='js/addressBook.js'></script>");
document.write("<script type='text/javascript' src='js/contacts.js'></script>");
document.write("<script type='text/javascript' src='js/bluetooth.js'></script>");
document.write("<script type='text/javascript' src='js/bluetooth4LE.js'></script>");
document.write("<script type='text/javascript' src='js/felica.js'></script>");
document.write("<script type='text/javascript' src='js/pdfViewer.js'></script>");
document.write("<script type='text/javascript' src='js/ad.js'></script>");
document.write("<script type='text/javascript' src='js/events.js'></script>");