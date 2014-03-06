var CACHE_PREFIX = 'DynamicApp:';
var CACHED_FILESLIST = 'CachedFilesList';
var DYNAMICAPP_CACHED_FILESLIST = CACHE_PREFIX + CACHED_FILESLIST;
var storage = window.localStorage;

DynamicApp.addOnloadEvent(function(){
  if(!storage.getItem(DYNAMICAPP_CACHED_FILESLIST)) {
  storage.setItem(DYNAMICAPP_CACHED_FILESLIST, JSON.stringify({id:CACHED_FILESLIST,cacheList:[]}));
    }
});

var Cache = {
    // expireDate format is yyyy-MM-dd hh:mm:ss
    addData: function(data, expireDate, successCallback, errorCallback) {
        if(expireDate != null && expireDate.length > 0 
           && new Date(expireDate.replace(/-/g,"/")) == 'Invalid Date') {
            if(errorCallback) {
                errorCallback();
                return;
            }
        }
        
        var cacheObject = {
            id: DynamicApp.createUUID(),
            data: data,
            expireDate: expireDate ? expireDate : '2099-12-31 23:59:59'
        };
        var key = CACHE_PREFIX + cacheObject.id;
        storage.setItem(key, JSON.stringify(cacheObject));
        
        if(storage.getItem(key)) {
            if(successCallback) {
                successCallback();
            }
        } else {
            if(errorCallback) {
                errorCallback();
            }
        }
    },
    getList: function() {
        var cacheList = new Array();
        if(storage.length > 0) {
            for (var key in storage){
                if (/DynamicApp:\w+/.test(key)) {
                    var cacheObject = JSON.parse(storage.getItem(key));
                    
                    if(Cache._checkIfExpired(cacheObject.expireDate)) {
                        storage.removeItem(cacheObject.id);
                        continue;
                    }
                    
                    cacheList.push(cacheObject);
                }
            }
        }
        return cacheList;
    },
    removeData: function(id, successCallback, errorCallback) {
        var key = CACHE_PREFIX + id;
        storage.removeItem(key);
        
        if(storage.getItem(key)) {
            if(errorCallback) {
                errorCallback();
            }
        } else {
            if(successCallback) {
                successCallback();
            }
        }
    },
    resetDataCache: function(successCallback, errorCallback) {
        if(storage.length > 0) {
            try{
                for (var key in storage){
                    if(key == DYNAMICAPP_CACHED_FILESLIST) {
                        continue;
                    }
                    if (/DynamicApp:\w+/.test(key) || true) {
                        storage.removeItem(key);
                    }
                }
                if(successCallback) {
                    successCallback();
                }
            } catch(error) {
                if(errorCallback) {
                    errorCallback();
                }
            }
        }
    },
    // expireDate format is yyyy-MM-dd hh:mm:ss
    addResource: function(remoteContentsPath, expireDate, successCallback, errorCallback) {
        if(expireDate != null && expireDate.length > 0  
           && new Date(expireDate.replace(/-/g,"/")) == 'Invalid Date') {
            if(errorCallback) {
                errorCallback();
                return;
            }
        }
        
        var options = {
            id: DynamicApp.createUUID(),
            resourceURL: remoteContentsPath,
            expireDate: expireDate ? expireDate : '2099-12-31 23:59:59'
        }
        
        var _successCallback = function(result) {
            var cacheListData = JSON.parse(storage.getItem(DYNAMICAPP_CACHED_FILESLIST));
            cacheListData.cacheList.push(result);
            storage.setItem(DYNAMICAPP_CACHED_FILESLIST, JSON.stringify(cacheListData));
            
            if(successCallback) {
                successCallback(result);
            }
        };
        
        DynamicApp.exec(_successCallback, errorCallback, 'ResourceCache', 'add', [options]);
    },
    // nextExpireDate format is yyyy-MM-dd hh:mm:ss
    updateResource: function(id, nextExpireDate, successCallback, errorCallback) {
        if(nextExpireDate != null && nextExpireDate.length > 0  
           && new Date(nextExpireDate.replace(/-/g,"/")) == 'Invalid Date') {
            if(errorCallback) {
                errorCallback();
                return;
            }
        }
        
        var resourceCache = JSON.parse(storage.getItem(DYNAMICAPP_CACHED_FILESLIST));
        var resource = null;
        for(var index in resourceCache.cacheList) {
            if(resourceCache.cacheList[index].id == id) {
                resource = resourceCache.cacheList[index];
                break;
            }
        }
        if(resource) {
            try {
                if(nextExpireDate || Cache._checkIfExpired(resource.expireDate) ) {
                    var options = {
                        id: resource.id,
                        resourceURL: resource.resourceURL,
                        expireDate: nextExpireDate ? nextExpireDate : '2099-12-31 23:59:59'
                    };
                    var removeSuccessCallback = function() {
                        //result = {id:, expireDate:, fullPath:}
                        var addSuccessCallback = function(result) {
                            var cacheListData = JSON.parse(storage.getItem(DYNAMICAPP_CACHED_FILESLIST));
                            cacheListData.cacheList.push(result);
                            storage.setItem(DYNAMICAPP_CACHED_FILESLIST, JSON.stringify(cacheListData));
                            
                            if(successCallback) {
                                successCallback(result);
                            }
                        };
                        DynamicApp.exec(addSuccessCallback, errorCallback, 'ResourceCache', 'add', [options]);
                    };
                    
                    Cache.removeResource(id, removeSuccessCallback, errorCallback);
                } else {
                    successCallback(resource);
                }
            } catch (error) { if(errorCallback) {errorCallback();} }
        }
    },
    getResourceList: function() {
        var resourceList = new Array();
        var resourceCache = JSON.parse(storage.getItem(DYNAMICAPP_CACHED_FILESLIST));
        if(resourceCache) {
            for (var resource in resourceCache.cacheList){
                resourceList.push(resourceCache.cacheList[resource]);
            }
        }
        return resourceList;
    },
    removeResource: function(id, successCallback, errorCallback) {
        var resourceCache = JSON.parse(storage.getItem(DYNAMICAPP_CACHED_FILESLIST));

        var _successCallback = function() {
            if(resourceCache) {
                resourceCache.cacheList = resourceCache.cacheList.filter(function(obj) {return obj.id != id;});
                storage.setItem(DYNAMICAPP_CACHED_FILESLIST, JSON.stringify(resourceCache));
            }
            
            if(successCallback) {
                successCallback();
            }
        };
        
        var options;
        for(var index in resourceCache.cacheList) {
            if(resourceCache.cacheList[index].id == id) {
                options = resourceCache.cacheList[index];
                break;
            }
        }
        
        DynamicApp.exec(_successCallback, errorCallback, 'ResourceCache', 'remove', [options]);
    },
    resetResourceCache: function(successCallback, errorCallback) {
        var _successCallback = function() {
            storage.removeItem(DYNAMICAPP_CACHED_FILESLIST);
            storage.setItem(DYNAMICAPP_CACHED_FILESLIST, JSON.stringify({id:CACHED_FILESLIST,cacheList:[]}));
            if(successCallback) {
                successCallback();
            }
        }
        var options = {fullPath: 'CLEAR_RESOURCE_CACHE'};
        
        DynamicApp.exec(_successCallback, errorCallback, 'ResourceCache', 'remove', [options]);
    },
    _checkIfExpired: function(dateTimeString) {
        try {
            var dateTimeParts = dateTimeString.split(' ');
            
            var dateParts = dateTimeParts[0].split('-');
            var timeParts = dateTimeParts[1].split(':');
            
            var expireDate = new Date(dateParts[0], dateParts[1]-1, dateParts[2], timeParts[0], timeParts[1], timeParts[2]);
            
            if(expireDate < new Date()) {
                return true;
            }
        } catch (error) {
            return true;
        }
        return false;
    }
};