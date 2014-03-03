DynamicApp.soundObjects = {};
DynamicApp.Sound = function () {};

var Sound = function(src, successCallback, errorCallback) {
    this.mediaId = DynamicApp.createUUID();
    DynamicApp.soundObjects[this.mediaId] = this;
    this.src = src;
    this.successCallback = successCallback;
    this.errorCallback = errorCallback;
    this.duration = -1;
    this.position = -1;
    this.state = Sound.SOUND_NONE;
}

Sound.prototype.play = function(otherOptions) {
    var me = this;
    var successCallback = function(result) {
        me.state = result.state;
        me.duration = result.duration;
        me.successCallback();
    };
    
    var errorCallback = function(error) {
        me.errorCallback(error);
    }

    var options = {
        audioFile:this.src, 
        mediaId:this.mediaId
    };
    
    if(otherOptions) {
        jQuery.extend(options, otherOptions);
    }
        
    DynamicApp.exec(successCallback, errorCallback, "Sound", "play", [options]);
};
    
Sound.prototype.pause = function() {
    if(this.state == Sound.SOUND_NONE || this.state == Sound.SOUND_STOPPED) {
        return;
    }
    
    var me = this;
    var successCallback = function(result) {
        me.state = result;
        me.successCallback();
    };
    
    var errorCallback = function(error) {
        me.errorCallback(error);
    }

    // pause should not wait from play to finish
    DynamicApp.processing = false;
    DynamicApp.exec(successCallback, errorCallback, "Sound","pause", [{mediaId:this.mediaId}]);
};

Sound.prototype.stop = function() {
    if(this.state == Sound.SOUND_NONE || this.state == Sound.SOUND_STOPPED) {
        return;
    }
    
    var me = this;
    var successCallback = function(result) {
        me.state = result;
        me.position = 0;
        me.successCallback();
    };
    
    var errorCallback = function(error) {
        me.errorCallback(error);
    }

    // stop should not wait from play to finish
    DynamicApp.processing = false;
    DynamicApp.exec(successCallback, errorCallback, "Sound","stop", [{mediaId:this.mediaId}]);
};
    
Sound.prototype.release = function() {
    if(this.state == Sound.SOUND_NONE) {
        return;
    }
    
    var me = this;
    var successCallback = function(result) {
        me.state = result;
        me.position = -1;
        me.duration = -1;
        me.successCallback();
    };
    
    var errorCallback = function(error) {
        me.errorCallback(error);
    }
    
    DynamicApp.exec(successCallback, errorCallback, "Sound","release", [{mediaId:this.mediaId}]);
};
    
Sound.prototype.getCurrentPosition = function() {
    if(this.state == Sound.SOUND_NONE || this.state == Sound.SOUND_STOPPED) {
        return;
    }
    
    var me = this;
    var successCallback = function(result) {
        me.position = result;
        //me.successCallback();
    };
    
    var errorCallback = function(error) {
        me.errorCallback(error);
    }

    DynamicApp.exec(successCallback, errorCallback, "Sound", "getCurrentPosition", [{mediaId:this.mediaId}]);
};

Sound.prototype.setCurrentPosition = function(pos, callback) {
    if(this.state == Sound.SOUND_NONE) {
        return;
    }
    
    var me = this;
    var successCallback = function(result) {
        me.position = result;
        
        if(callback) {
            callback();
        }
    };
    
    var errorCallback = function(error) {
        me.errorCallback(error);
    }

    DynamicApp.exec(successCallback, errorCallback, "Sound", "setCurrentPosition", [{mediaId:this.mediaId, position:pos}]);
}
    
Sound.prototype.getDuration = function() {
    return this.duration;
};

DynamicApp.Sound.onStatus = function(id, msg, value) {
    var sound = DynamicApp.soundObjects[id];
    
    if(msg == Sound.SOUND_STATE) {
        sound.state = value;
        if(sound.successCallback) {
            sound.successCallback();
        }
    } else if(msg == Sound.SOUND_POSITION) {
    	sound.position = value;
        if(sound.successCallback) {
            sound.successCallback();
        }
    } else if(msg == Sound.SOUND_DURATION) {
        sound.duration = value;
    } else if(msg == Sound.SOUND_ERROR) {
        if(sound.errorCallback) {
            sound.errorCallback(value);
        }
    }
    /*
    // If state update
    if (msg == Sound.SOUND_STATE) {
        if (value == Sound.SOUND_STOPPED) {
            if (media.successCallback) {
                media.successCallback();
            }
        }
        if (media.statusCallback) {
            media.statusCallback(value);
        }
    }
    else if (msg == Sound.SOUND_DURATION) {
        media._duration = value;
    }
    else if (msg == Sound.SOUND_ERROR) {
        if (media.errorCallback) {            
            media.errorCallback(value);
        }
    }
    else if (msg == Sound.SOUND_POSITION || msg == Sound.SOUND_PAUSED) {
    	media._position = value;
    }*/
};

// Media messages
Sound.SOUND_STATE = 1;
Sound.SOUND_DURATION = 2;
Sound.SOUND_POSITION = 3;
Sound.SOUND_ERROR = 9;

// Media states
Sound.SOUND_NONE = 0;
Sound.SOUND_STARTING = 1;
Sound.SOUND_RUNNING = 2;
Sound.SOUND_PAUSED = 3;
Sound.SOUND_STOPPED = 4;
Sound.SOUND_MSG = ["None", "Starting", "Running", "Paused", "Stopped"];

// Media error codes

Sound.ERROR_ABORTED = 1;
Sound.ERROR_NETWORK = 2;
Sound.ERROR_DECODE = 3;
Sound.ERROR_NONE_SUPPORTED = 4;