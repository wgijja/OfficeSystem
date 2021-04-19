app.controller('cartController', function ($scope, cartService, addressService) {

    //查询全部商品
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;
                $scope.total = this.sum($scope.cartList);
            }
        );
    }

    //增加、删除商品
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();
                } else {
                    alert(response.message);
                }

            }
        );
    }

    //计算总数
    sum = function (cartList) {
        var total = {'totalNum': 0, 'totalMoney': 0.00}
        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];
                total.totalNum += orderItem.num;//总数量
                total.totalMoney += orderItem.totalFee;//总价格
            }
        }
        return total;
    }

    $scope.findAddressListByUserId = function () {
        cartService.findAddressListByUserId().success(
            function (response) {
                $scope.addressList = response;
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault === '1') {
                        $scope.address = $scope.addressList[i];
                        break;
                    }
                }
            }
        );
    }

    //选中地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }

    //判断是否选中
    $scope.isSelected = function (address) {
        return $scope.address === address;
    }

    //初始化订单的数据结构
    $scope.order = {'paymentType': '1'}

    //选中支付方式
    $scope.selectPaymentType = function (paymentType) {
        $scope.order.paymentType = paymentType;
    }

    //提交订单
    $scope.submitOrder = function () {
        $scope.order.receiverAreaName = $scope.address.address; //地址
        $scope.order.receiverMobile = $scope.address.mobile;    //手机
        $scope.order.receiver = $scope.address.contact;         //联系人

        cartService.submitOrder($scope.order).success(
            function (response) {
                if ($scope.order.paymentType === '1') {
                    location.href = "pay.html";
                } else {
                    location.href = "paysuccess.html";
                }
            }
        );
    }

    //有关地址的
    //查询实体
    $scope.findOne = function (id) {
        addressService.findOne(id).success(
            function (response) {
                $scope.entity = response;
            }
        );
    }

    //保存
    $scope.save = function () {
        var serviceObject;//服务层对象
        if ($scope.entity.id != null) {//如果有ID
            serviceObject = addressService.update($scope.entity); //修改
        } else {
            serviceObject = addressService.add($scope.entity);//增加
        }
        serviceObject.success(
            function (response) {
                if (response.success) {
                    //重新查询
                    $scope.findAddressListByUserId();
                } else {
                    alert(response.message);
                }
            }
        );
    }

    //删除
    $scope.dele = function (id) {
        //获取选中的复选框
        addressService.dele(id).success(
            function (response) {
                if (response.success) {
                    $scope.findAddressListByUserId();
                }
            }
        );
    }


});