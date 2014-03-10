var PDFViewer = {
    showWithFileObject: function(file) {
        if(file && file.fullPath && file.size > 0 && file.type == 'application/pdf') {
            //alert(JSON.stringify(file));
            //PDFViewer.showWithFilePath(file.parentPath + (file.parentPath.charAt(file.parentPath.length-1) == '/' ? '' : '/') + file.filename);
            var filePath = file.parentPath + (file.parentPath.charAt(file.parentPath.length-1) == '/' ? '' : '/') + file.filename;
            alert(filePath);
            DynamicApp.exec(null, null, "PDFViewer", "show", [{path:filePath}]);
        }
    },
    showWithFilePath: function(filePath) {
        filePath = 'media_resources' + (filePath.charAt(0) == '/' ? '' : '/') + filePath;
        DynamicApp.exec(null, null, "PDFViewer", "show", [{path:filePath}]);
    },
    showWithURL: function(url) {
        alert(url);
        DynamicApp.exec(null, null, "PDFViewer", "show", [{path:url}]);
    }
};