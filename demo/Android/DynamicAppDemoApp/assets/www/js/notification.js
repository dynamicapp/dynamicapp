var Notification = {
    // date format is yyyy-MM-dd hh:mm:ss
    notify: function(date, message, successCallback, errorCallback, options) {
        if(date == null || date.length == 0) {
            var d = new Date();
            
            date = d.getFullYear() + '-'
                + (d.getMonth() < 9 ? '0' : '') + (d.getMonth()+1) + '-'
                + (d.getDate() < 10 ? '0' : '') + d.getDate() + ' '
                + (d.getHours() < 10 ? '0' : '') + d.getHours() + ':'
                + (d.getMinutes() < 10 ? '0' : '') + d.getMinutes() + ':'
                + (d.getSeconds() < 10 ? '0' : '') + (d.getSeconds()+5);
        } else if (new Date(date.replace(/-/g,"/")) == 'Invalid Date') {
            if(errorCallback) {
                errorCallback();
                return;
            }
        }
        
        if(message == null) {
            message = '';
        }
        
        var _options = {
            date: date,
            message: message,
            badge: options.badge,
            hasAction: options.hasAction ? true : false,
            action: options.action ? options.action : 'View'
        };
        
        DynamicApp.exec(successCallback, errorCallback, 'Notification', 'notify', [_options]);
    }, 
    cancelNotification: function(date, successCallback, errorCallback) {
        var options = {
            date: date
        }
        DynamicApp.exec(successCallback, errorCallback, 'Notification', 'cancelNotification', [options]);
    }
};