var Contacts = {
    create: function() {
        var _contact = new function() {
            this.save = function(successCallback, errorCallback) {
                var canSave = false;
                
                for(var i=0;i<Contacts.CONTACT_PROPERTIES.length;i++) {
                    if(this.hasOwnProperty(Contacts.CONTACT_PROPERTIES[i])) {
                        canSave = true;
                        break;
                    }
                }
                
                var options = {
                    contact: this
                }
                
                if(canSave) {
                    DynamicApp.exec(successCallback, errorCallback, "Contacts", "save", [options]);
                } else {
                    if(errorCallback) {
                        errorCallback(0);
                    }
                }
            };
            this.remove = function(successCallback, errorCallback) {
                if(!this.id) {
                    if(errorCallback) {
                        errorCallback(0);
                    }
                    return;
                }
                
                var options = {
                    contact: this
                }
                
                DynamicApp.exec(successCallback, errorCallback, "Contacts", "remove", [options]);
            };
        };
        return _contact;
    },
    search: function(conditions, sort, successCallback, errorCallback) {
        if(conditions.length == 0) {
            if(errorCallback) {
                errorCallback(0);
            }
            return;
        }
    
        var options = {
            conditions: conditions,
            sort: sort
        };
        
        DynamicApp.exec(successCallback, errorCallback, "Contacts", "search", [options]);
    }
};

Contacts.CONTACT_PROPERTIES = ['id', 'displayName', 'name', 'nickname', 'phoneNumbers', 'emails', 'addresses', 'ims', 'organizations', 'birthday', 'note', 'photos', 'categories', 'urls'];

Contacts.SEARCH_ORDER_ASC = 1;
Contacts.SEARCH_ORDER_DESC = 0;