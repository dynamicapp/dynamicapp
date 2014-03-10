var LoadingScreen = {
    startLoad: function(successCallback, errorCallback, options) {
        if(!options) {
            options = {};
        }
        var _options = {
            label: options.label ? options.label : 'Loading...',
            style: options.style ? options.style : LoadingScreen.STYLEWHITELARGE, 
            bgColor: options.isBlackBackground ? 'black' : 'clear'
        };
    
        if(options.frame) {
            jQuery.extend(_options, {frame: options.frame});
        }
        
        DynamicApp.exec(successCallback, errorCallback, 'LoadingScreen', 'startLoad', [_options]);
    },
    stopLoad: function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, 'LoadingScreen', 'stopLoad', [{}]);
    }
};

LoadingScreen.STYLEWHITELARGE = 0;
LoadingScreen.STYLEWHITE = 1;
LoadingScreen.STYLEGRAY = 2;