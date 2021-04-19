app.controller('itemPageController', function ($scope, $http) {

    //购物车加减数量
    $scope.addNum = function (num) {
        $scope.num += num;
        if ($scope.num < 1) {
            $scope.num = 1;
        }
    }

    //初始化规格对象的数据结构 {'规格名称':'规格选项'}
    $scope.specification = {};

    //选择规格
    $scope.selectSpecification = function (key, value) {
        $scope.specification[key] = value;
        searchSku();
    }

    //是否选中
    $scope.isSelected = function (key, value) {
        return $scope.specification[key] === value;
    }

    //加载SKU数据
    $scope.loadSku = function () {
        $scope.sku = skuList[0];
        $scope.specification = JSON.parse(JSON.stringify($scope.sku.spec));//深克隆
    }

    //选中规格，比较规格信息
    searchSku = function () {
        for (var i = 0; i < skuList.length; i++) {
            if (matchObject($scope.specification, skuList[i].spec)) {
                $scope.sku = skuList[i];
                return;
            }
        }
        $scope.sku = {'id': 0, 'title': '----', 'price': 0, 'spec': {}}
    }

    //比较两个map是否一致
    matchObject = function (map1, map2) {
        if (map1.size === map2.size) {
            for (let k in map1) {
                if (map1[k] !== map2[k]) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    //加入购物车
    $scope.addToCart = function () {
        // alert('id' + $scope.sku.id);
        $http.get('http://localhost:9108/cart/addGoodsToCartList.do?itemId=' + $scope.sku.id + '&num=' + $scope.num, {'withCredentials': true}).success(
            function (response) {
                if (response.success) {
                    location.href = "http://localhost:9108/cart.html"
                }
            }
        );
    }
});