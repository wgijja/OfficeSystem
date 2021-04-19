app.service('cartService', function ($http) {

    this.findCartList = function () {
        return $http.get('../cart/findCartList.do');
    }

    this.addGoodsToCartList = function (itemId, num) {
        return $http.get('../cart/addGoodsToCartList.do?itemId=' + itemId + '&num=' + num);
    }

    this.findAddressListByUserId = function () {
        return $http.get('../address/findAddressListByUserId.do');
    }

    //提交订单
    this.submitOrder = function (order) {
        return $http.post('../order/add.do', order);
    }
});