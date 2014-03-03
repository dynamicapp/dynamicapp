var AddressBook = {
    show : function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, "AddressBook", "show", [{isSelect:false}]);
    },
    showForSelection: function(successCallback, errorCallback) {
        DynamicApp.exec(successCallback, errorCallback, "AddressBook", "show", [{isSelect:true}]);
    }
};

AddressBook.ERROR_NO_DATA = 1;