//自定义模块
var app = angular.module('dongyimai', ['pagination']);


//angularJS的过滤器
app.filter('trustHtml',['$sce',function ($sce) {
    return function (data) {
        return $sce.trustAsHtml(data);
    }
}]);