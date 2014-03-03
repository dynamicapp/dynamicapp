var Encryptor = {
    encrypt: function(text, successCallback, errorCallback) {
        var options = {
            text: text
        };
        
        DynamicApp.exec(successCallback, errorCallback, 'Encryptor', 'encryptText', [options]);
    },
    decrypt: function(text, successCallback, errorCallback) {
        var options = {
            text: text
        };
        DynamicApp.exec(successCallback, errorCallback, 'Encryptor', 'decryptText', [options]);
    }
};