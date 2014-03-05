var QRReader = {
    SOURCE_CAMERA: 0,
    SOURCE_PHOTOLIBRARY: 1,
    read: function(successCallback, errorCallback, options) {
        if(!options) {
            options = {source: QRReader.SOURCE_CAMERA};
        }
        
        DynamicApp.exec(successCallback, errorCallback, "QRReader", "scan", [options]);
    }
};