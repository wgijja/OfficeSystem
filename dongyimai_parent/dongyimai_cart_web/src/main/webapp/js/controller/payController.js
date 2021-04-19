app.controller('payController', function ($scope, $location, payService) {

    //生成二维码
    $scope.createNative = function () {
        payService.createNative().success(
            function (response) {
                $scope.outTradeNo = response.outTradeNo;
                $scope.totalAmount = (response.totalAmount / 100).toFixed(2);
                //生成二维码
                var qrcode = new QRious({
                    'element': document.getElementById("erweima"),
                    'level': "H",
                    'size': '300',
                    'value': response.qrCode
                });
                $scope.queryPayStatus($scope.outTradeNo);
            }
        );
    }

    //查询订单状态
    $scope.queryPayStatus = function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(
            function (response) {
                if (response.success) {
                    //交易成功
                    location.href = "paysuccess.html#?money=" + $scope.totalAmount;
                } else {
                    if (response.message === "二维码超时") {
                        document.getElementById("timeOut").innerHTML = "二维码已过期，刷新页面重新获取二维码。";
                    } else {
                        location.href = "payfail.html";
                    }
                }
            }
        );
    }

    //获取路由过来的信息
    $scope.getMoney = function () {
        return $location.search()['money'];
    }
});