var decryptImages = function(flag) {
      if(flag) {
        var successCallback = function(imageData) {
        	document.querySelector('img[src="'+imageData.srcEnc+'"]').src = imageData.srcDec;
        };

        var errorCallback = function(error) {
            alert(error);
        };

		var imageElements = document.querySelectorAll('img[src$=".pkg"]');
        var imageSrcList = new Array();
        for(var i=0;i<imageElements.length;i++) {
            imageSrcList[i] = imageElements[i].getAttribute('src');
        }

        var options = {
            'imgSrcList' : imageSrcList
        };

        if(imageSrcList.length > 0) {
            DynamicApp.exec(successCallback, errorCallback, "ImageDecrypt", "decrypt", [options]);
        }
      } else {
		var imageElements = document.querySelectorAll('img[src$=".pkg"]');
        for(var i=0;i<imageElements.length;i++) {
        	imageElements[i].parentNode.removeChild(imageElements[i]);
        }
      }
};

DynamicApp.addOnloadEvent(function() {
    decryptImages(true);
});