var Camera = {

    DestinationType : {
        DATA_URL: 0,                // Return base64 encoded string
        FILE_URI: 1                 // Return file uri 
    },

    PictureSourceType : {
        PHOTOLIBRARY : 0,           // Choose image from picture library
        CAMERA : 1,                 // Take picture from camera
        SAVEDPHOTOALBUM : 2         // Choose image from saved photos album
    },

    EncodingType : { 
        JPEG: 0,                    // Return JPEG encoded image 
        PNG: 1                      // Return PNG encoded image 
    },

    getPicture : function(successCallback, errorCallback, options) {
        // successCallback required
        if (typeof successCallback != "function") {
            console.log("Camera Error: successCallback is not a function");
            return;
        }

        // errorCallback optional
        if (errorCallback && (typeof errorCallback != "function")) {
            console.log("Camera Error: errorCallback is not a function");
            return;
        }
	
        DynamicApp.exec(successCallback, errorCallback, "Camera", "getPicture", [options]);
    }
};

