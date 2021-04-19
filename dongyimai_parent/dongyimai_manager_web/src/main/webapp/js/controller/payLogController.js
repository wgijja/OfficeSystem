app.controller('payLogController', function ($scope, $controller, payLogService) {

    $controller('baseController', {$scope: $scope});//继承

    $scope.searchEntity = {};//定义搜索对象

    $scope.search = function (page, rows) {
        payLogService.search(page, rows, $scope.searchEntity).success(
            function (response) {
                $scope.list = response.rows;
                $scope.paginationConf.totalItems = response.total;//更新总记录数
            }
        );
    }

    $scope.formatTradeState = ["未支付", "已支付"];
    $scope.formatPayType = ["暂无", "在线支付", "货到付款"];
});