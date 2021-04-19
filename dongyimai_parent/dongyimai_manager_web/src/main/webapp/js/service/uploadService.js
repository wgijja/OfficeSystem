app.service('uploadService', function ($http) {

    this.uploadFile = function () {
        //1、创建表单数据对象
        var formData = new FormData();
        formData.append("file",file.files[0]);
        return $http({
            'method':'POST',
            'url':'../uploadFile.do',
            'data':formData,
            'headers': {'Content-Type':undefined},  //angular默认请求格式为json，设置为默认浏览器会自动匹配上传文件格式
            'transformRequest':angular.identity     //将上传的数据通过angularJS序列化
        });
    }
})