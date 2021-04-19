app.service('payService', function ($http) {

    //生成支付二维码
    this.createNative = function () {
        return $http.get('/aliPay/createNative.do');
    }

    //查询订单状态
    this.queryPayStatus = function (outTradeNo) {
        return $http.get('/aliPay/queryPayStatus.do?outTradeNo=' + outTradeNo);
    }
});