var AppVersion = {
    get : function(callback) {
        DynamicApp.exec(callback, null, "AppVersion", "get", [{}]);
    }
};