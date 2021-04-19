app.service('seckillService', function ($http) {

    this.findSecKillGoodsList = function () {
        return $http.get('../seckillGoods/findSecKillGoodsList.do');
    }

    this.findItemFromRedis = function (id) {
        return $http.get('../seckillGoods/findItemFromRedis.do?id=' + id);
    }

    this.submitOrder = function (itemId) {
        return $http.get('../seckillOrder/submitOrder.do?itemId=' + itemId);
    }

    this.createNative = function () {
        return $http.get('../aliPay/createNative.do');
    }

    //查询订单状态
    this.queryPayStatus=function (outTradeNo){
        return $http.get('/aliPay/queryPayStatus.do?outTradeNo='+outTradeNo);
    }
});