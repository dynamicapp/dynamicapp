DynamicApp.movieObjects = {};
DynamicApp.Movie = function () {};

var Movie = function(src, successCallback, errorCallback, options) {
    this.mediaId = DynamicApp.createUUID();
    DynamicApp.movieObjects[this.mediaId] = this;
    this.src = src;
    this.successCallback = successCallback;
    this.errorCallback = errorCallback;
    this.duration = -1;
    this.position = -1;
    this.state = Movie.MOVIE_NONE;
    if(options) {
        this.frame = options.frame;
        this.scalingMode = options.scalingMode;
        this.controlStyle = options.controlStyle;
    }
}

Movie.prototype.play = function(otherOptions) {
    var me = this;
    var successCallback = function(result) {
        me.state = result;
        //me.position = 0;
        me.successCallback();
    };
    
    var errorCallback = function(error) {
        me.errorCallback(error);
    }
    
    var options = {
        movieFile:this.src, 
        mediaId:this.mediaId,
        frame:this.frame,
        scalingMode:this.scalingMode,
        controlStyle:this.controlStyle
    };
    
    if(otherOptions) {
        jQuery.extend(options, otherOptions);
    }
    
    DynamicApp.exec(successCallback, errorCallback, "Movie", "play", [options]);
};
    
Movie.prototype.pause = function() {
    if(this.state == Movie.MOVIE_STOPPED) {
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
    DynamicApp.exec(successCallback, errorCallback, "Movie","pause", [{mediaId:this.mediaId}]);
};

Movie.prototype.stop = function() {
    if(this.state == Movie.MOVIE_NONE) {
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
    DynamicApp.exec(successCallback, errorCallback, "Movie","stop", [{mediaId:this.mediaId}]);
};

Movie.prototype.getCurrentPosition = function() {
	if(this.state == Movie.MOVIE_NONE || this.state == Movie.MOVIE_STOPPED || this.state == Movie.MOVIE_PAUSED) {
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

    DynamicApp.exec(successCallback, errorCallback, "Movie","getCurrentPosition", [{mediaId:this.mediaId}]);
};

Movie.prototype.setCurrentPosition = function(pos, callback) {
    if(this.state == Movie.MOVIE_NONE) {
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

    DynamicApp.exec(successCallback, errorCallback, "Movie","setCurrentPosition", [{mediaId:this.mediaId, position:pos}]);
};

Movie.prototype.release = function() {
    if(this.state == Movie.MOVIE_NONE) {
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
    
    DynamicApp.exec(successCallback, errorCallback, "Movie","release", [{mediaId:this.mediaId}]);
};
    
Movie.prototype.getDuration = function() {
    return this.duration;
};

Movie.prototype.getThumbnail = function(successCallback, errorCallback, options) {
    var _offset = 0;
    var _quality = 0.5;
    if(options) {
        if(!isNaN(options.offset)) {
            if(options.offset < 0) {
                _offset = 0;
            } else if(options.offset > this.duration) {
                _offset = this.duration > 0 ? this.duration : 0;
            } else {
                _offset = options.offset;
            }
        }
        if(!isNaN(options.quality)) {
            _quality = options.quality;
        }
    }
    
	// getThumbnail should not wait from stop to finish
	DynamicApp.processing = false;
    DynamicApp.exec(successCallback, errorCallback, "Movie", "getThumbnail", [{movieFile:this.src, mediaId:this.mediaId, offset:_offset, quality:_quality, width:options.width, height:options.height}]);
};

DynamicApp.Movie.onStatus = function(id, msg, value) {
    var movie = DynamicApp.movieObjects[id];
    
    if(msg == Movie.MOVIE_STATE) {
        movie.state = value;
        if(movie.successCallback) {
            movie.successCallback();
        }
    } else if(msg == Movie.MOVIE_POSITION) {
    	movie.position = value;
        if(movie.successCallback) {
            movie.successCallback();
        }
    } else if(msg == Movie.MOVIE_DURATION) {
        movie.duration = value;
    } else if(msg == Movie.MOVIE_ERROR) {
        if(movie.errorCallback) {
            movie.errorCallback(value);
        }
    }
}

// Scaling mode
Movie.SCALING_NONE = 0;
Movie.SCALING_ASPECT_FIT = 1;
Movie.SCALING_ASPECT_FILL = 2;
Movie.SCALING_FILL = 3;

// Control style
Movie.CONTROL_NONE = 0;
Movie.CONTROL_EMBEDDED = 1;
Movie.CONTROL_FULLSCREEN = 2;
Movie.CONTROL_DEFAULT = Movie.CONTROL_FULLSCREEN;

// Movie messages
Movie.MOVIE_STATE = 1;
Movie.MOVIE_DURATION = 2;
Movie.MOVIE_POSITION = 3;
Movie.MOVIE_ERROR = 9;

// Movie states
Movie.MOVIE_NONE = 0;
Movie.MOVIE_STARTING = 1;
Movie.MOVIE_RUNNING = 2;
Movie.MOVIE_PAUSED = 3;
Movie.MOVIE_STOPPED = 4;
Movie.MOVIE_MSG = ["None", "Starting", "Running", "Paused", "Stopped"];

// Movie error codes
Movie.ERROR_ABORTED = 1,
Movie.ERROR_NETWORK = 2,
Movie.ERROR_DECODE = 3,
Movie.ERROR_NONE_SUPPORTED = 4