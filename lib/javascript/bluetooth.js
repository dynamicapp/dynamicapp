var Bluetooth = {
    peerList : [], //peerData = {id, deviceName, state}
    onStateChanged : function(deviceName, id, state) {
        Bluetooth.peerList = Bluetooth.peerList.filter(function(obj) {if (navigator.userAgent.match(/Windows Phone/i)) return obj.deviceName != deviceName; else return obj.id != id;});
        if(state == Bluetooth.STATE_AVAILABLE || state == Bluetooth.STATE_CONNECTED || state == Bluetooth.STATE_CONNECTING) {
            Bluetooth.peerList.push({
                deviceName: deviceName,
                id: id,
                state: state
            });
        }
    },
    discover : function() {
        //Bluetooth.peerList.length = 0;
        DynamicApp.exec(null, null, 'Bluetooth', 'discover', [{}]);
    },
    connect : function(peerData, successCallback, errorCallback) {
        if(peerData.state != Bluetooth.STATE_AVAILABLE) {
            if(errorCallback) {
                errorCallback();
            }
            return;
        }
        Bluetooth.onStateChanged(peerData.deviceName, peerData.id, Bluetooth.STATE_CONNECTING);
        DynamicApp.exec(successCallback, errorCallback, 'Bluetooth', 'connect', [{peerData: peerData}]);
    },
    disconnect : function(peerData, successCallback, errorCallback) {
        if(peerData.state != Bluetooth.STATE_CONNECTED) {
            if(errorCallback) {
                errorCallback();
            }
            return;
        }
        DynamicApp.exec(successCallback, errorCallback, 'Bluetooth', 'disconnect', [{peerData: peerData}]);
    },
    send : function(peerData, sendData, successCallback, errorCallback) {
        if(peerData.state != Bluetooth.STATE_CONNECTED) {
            if(errorCallback) {
                errorCallback();
            }
            return;
        }
        DynamicApp.exec(successCallback, errorCallback, 'Bluetooth', 'send', [{peerData: peerData, sendData: sendData}]);
    },
    onRecvCallback : function(peerData, receiveData) {
    }
};

Bluetooth.STATE_UNKNOWN = -1;
Bluetooth.STATE_AVAILABLE = 0;
Bluetooth.STATE_UNAVAILABLE = 1;
Bluetooth.STATE_CONNECTED = 2;
Bluetooth.STATE_DISCONNECTED = 3;
Bluetooth.STATE_CONNECTING = 4;