app.controller('baseController', function ($scope) {

    //设置分页参数
    $scope.paginationConf = {
        'currentPage': 1,
        'itemsPerPage': 10,
        'perPageOptions': [10, 15, 20, 30],
        'totalItems': 10,
        onChange: function () {
            //执行分页查询
            $scope.reloadList();
        }
    }

    //为了方便，无参调用分页查询方法
    $scope.reloadList = function () {
        $scope.selectIds = [];
        $scope.all = false;
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage);
    }

    //设置选中的值
    $scope.selectIds = [];//初始化选中集合中的数据结构

    //选中,取消选中
    $scope.updateSelection = function ($event, id) {
        if ($event.target.checked == true) {
            $scope.selectIds.push(id);
        } else {
            var index = $scope.selectIds.indexOf(id);
            $scope.selectIds.splice(index, 1);
        }
    }

    //全选，取消全选
    $scope.updateSelectionAll = function ($event) {
        if ($event.target.checked) {
            angular.forEach($scope.list, function (obj) {
                $scope.selectIds.push(obj.id);
            })
            $scope.selectIds.push(id);
        } else {
            $scope.selectIds = [];
        }
    }

    //将JSON转换成String字符串
    $scope.jsonToString = function (jsonString, key) {
        //1、将json字符串转换成json对象
        var json = JSON.parse(jsonString);
        var value = "";
        //2、遍历json
        for (var i = 0; i < json.length; i++) {
            if (i > 0) {
                value += ","
            }
            value += json[i][key]
        }
        //3、根据key取得value，进行字符串拼接
        return value;
    }

    //判断对象在集合中是否存在 list:待判断的集合 key：集合中的属性 value:属性值
    $scope.searchObjectByKey = function (list, key, value) {
        for (var i = 0; i < list.length; i++) {
            if (list[i][key] == value) {
                return list[i];
            }
        }
        return null;
    }

})