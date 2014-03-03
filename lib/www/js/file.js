DynamicApp.File = function () {};

var File = function(parentPath, filename, successCallback, errorCallback) {
    this.parentPath = parentPath;
    this.filename = filename;
    
    this.fullPath = null;
    this.type = null;
    this.kind = -1;
    this.size = -1;
    
    this.initialized = File.NOT_INIT;
    
    var options = {
        'parentPath' : this.parentPath,
        'filename' : this.filename
    };
    var me = this;
    var fileErrorCallback = function(error) {
        me.initialized = File.INIT_ERROR;
    };
    var _successCallback = function(result) {
        me.setMetadata(result);
        successCallback(result);
    };
    var _errorCallback = function(error) {
        me.create(successCallback, errorCallback);
    };
    
    DynamicApp.exec(_successCallback, _errorCallback, "File", "getMetadata", [options]);
};

File.prototype.setMetadata = function(result) {
    this.initialized = File.INIT_OK;
    this.fullPath = result.fullPath;
    this.kind = result.kind;
    this.type = result.type;
    this.size = result.size ? result.size : 0;
};

File.prototype.create = function(successCallback, errorCallback) {
    if(this.initialized == File.INIT_OK) {
        errorCallback(File.ERROR_INVALID_STATE);
        throw File.ERROR_INVALID_STATE;
    }
    
    var me = this;
    var createSuccessCallback = function(result) {
        me.setMetadata(result);
        
        if(successCallback) {
            successCallback(result);
        }
    };
    
    var options = {
        'parentPath' : this.parentPath,
        'filename' : this.filename
    };
    
    DynamicApp.exec(createSuccessCallback, errorCallback, "File", "create", [options]);
};

File.prototype.isFile = function() {
    if(this.initialized === File.NOT_INIT || this.initialized === File.INIT_ERROR) {
        throw File.ERROR_INVALID_STATE;
    }
    
    return this.kind == File.KIND_FILE;
};

File.prototype.isDirectory = function() {
    if(this.initialized === File.NOT_INIT || this.initialized === File.INIT_ERROR) {
        throw File.ERROR_INVALID_STATE;
    }
     
    return this.kind == File.KIND_DIRECTORY;
};

File.prototype.copy = function(targetDirectory, newName, successCallback, errorCallback) {   
    if(this.initialized === File.NOT_INIT || this.initialized === File.INIT_ERROR) {
        throw File.ERROR_INVALID_STATE;
    }
    
    var options = {
        'fullPath' : this.fullPath,
        'targetDirectory' : targetDirectory,
        'newName' : newName ? newName : this.filename
    };
    
    DynamicApp.exec(successCallback, errorCallback, "File", "copy", [options]);
};

File.prototype.move = function(targetDirectory, newName, successCallback, errorCallback) {
    if(this.initialized === File.NOT_INIT || this.initialized === File.INIT_ERROR) {
        throw File.ERROR_INVALID_STATE;
    }
    
    var me = this;
    var moveSuccessCallback = function(result) {
        me.parentPath = result.newParentPath;
        me.filename = result.newFilename;
        me.fullPath = result.newFullPath;
        
        if(successCallback) {
            successCallback();
        }
    }
    
    var options = {
         'fullPath' : this.fullPath,
         'targetDirectory' : targetDirectory,
         'newName' : newName ? newName : this.filename
    };
    
    DynamicApp.exec(moveSuccessCallback, errorCallback, "File", "move", [options]);    
};

File.prototype.remove = function(successCallback, errorCallback) {
    if(this.initialized === File.NOT_INIT || this.initialized === File.INIT_ERROR) {
        throw File.ERROR_INVALID_STATE;
    }
    
    var me = this;
    var deleteSuccessCallback = function(result) {
	    me.fullPath = null;
	    me.type = null;
	    me.kind = -1;
	    me.size = -1;
        
        if(successCallback) {
            successCallback();
        }
    };
    var options = {
        'fullPath' : this.fullPath
    };
    
    DynamicApp.exec(deleteSuccessCallback, errorCallback, "File", "removeRecursively", [options]);
};

File.INIT_OK = 1;
File.INIT_ERROR = 0;
File.NOT_INIT = -1;


File.KIND_DIRECTORY = 0;
File.KIND_FILE = 1;

File.ERROR_NOT_FOUND = 1;
File.ERROR_SECURITY = 2;
File.ERROR_ABORT = 3;
File.ERROR_NOT_READABLE = 4;
File.ERROR_ENCODING = 5;
File.ERROR_NO_MODIFICATION_ALLOWED = 6;
File.ERROR_INVALID_STATE = 7;
File.ERROR_SYNTAX = 8;
File.ERROR_INVALID_MODIFICATION = 9;
File.ERROR_QUOTA_EXCEEDED = 10;
File.ERROR_TYPE_MISMATCH = 11;
File.ERROR_PATH_EXISTS = 12;


var FileReader = function(file) {
    if(file.initialized === File.NOT_INIT || file.initialized === File.INIT_ERROR) {
        throw File.ERROR_INVALID_STATE;
    }
    
    this.file = file;
    this.readyState = FileReader.INIT;
};

FileReader.prototype.read = function(encoding, successCallback, errorCallback) {
    if(this.readyState === FileWriter.LOADING) {
        throw File.ERROR_INVALID_STATE;
    }
    
    this.readyState = FileReader.LOADING;
    
    var me = this;
    var readSuccessCallback = function(result) {
        me.readyState = FileReader.DONE;
        if(successCallback) {
            successCallback(result);
        }
    };
    
    var options = {
        'fullPath' : this.file.fullPath,
        'encoding' : encoding
    };
    
    DynamicApp.exec(readSuccessCallback, errorCallback, "FileReader", "read", [options]);
};

// States
FileReader.INIT = 0;
FileReader.LOADING = 1;
FileReader.DONE = 2;

var FileWriter = function(file) {
    if(file.initialized === File.NOT_INIT || file.initialized === File.INIT_ERROR) {
        throw File.ERROR_INVALID_STATE;
    }
    
    this.file = file;
    this.readyState = FileWriter.INIT;
    this.position = this.file.size;
}

FileWriter.prototype.write = function(writeData, successCallback, errorCallback) {
    // Throw an exception if we are already writing a file
    if(this.readyState === FileWriter.WRITING) {
        throw File.ERROR_INVALID_STATE;
    }
    
    if(!writeData) {
        return;
    }
    
    if(this.file.kind <= File.KIND_DIRECTORY) { //-1 for non-existing file
        return;
    }
    this.readyState = FileWriter.WRITING;
    var me = this;
    var writeSuccessCallback = function(result) {
        me.readyState = FileWriter.DONE;
        me.file.size = result.size;
        me.position = result.size;
        
        if(successCallback) {
            successCallback(result);
        }
    };
    var options = {
        'fullPath' : this.file.fullPath,
        'data' : writeData,
        'position' : this.position > -1 ? this.position : 0
    };
    
    DynamicApp.exec(writeSuccessCallback, errorCallback, "FileWriter", "write", [options]);
}

FileWriter.prototype.seek = function(offset) {
    // Throw an exception if we are already writing a file
    if(this.readyState === FileWriter.WRITING) {
        throw File.ERROR_INVALID_STATE;
    }
    
    if (!offset) {
        return;
    }
    offset = parseInt(offset);
    if (offset < 0) {
		this.position = 0;
	} else if (offset > this.file.size) {
		this.position = this.file.size;
	} else {
		this.position = offset;
	}	
}

// States
FileWriter.INIT = 0;
FileWriter.WRITING = 1;
FileWriter.DONE = 2;