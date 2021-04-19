app.controller('itemSearchController', function ($scope, $location, itemSearchService) {

    $scope.search = function () {
        //处理当前页码的类型
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        itemSearchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap = response;
                //执行构建分页
                buildPageLabel();
            }
        );
    }

    //初始化查询条件对象的数据结构
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        'spec': {},
        'price': '',
        'pageNo': 1,
        'pageSize': 20,
        'sort': '',
        'sortField': ''
    }

    //添加查询条件
    $scope.addSearchItem = function (key, value) {
        if (key === 'category' || key === 'brand' || key === 'price') {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }
        $scope.resultMap.pageNo = 1;
        //执行查询
        $scope.search();
    }

    //移除查询条件
    $scope.removeSearchItem = function (key) {
        if (key === 'category' || key === 'brand' || key === 'price') {
            $scope.searchMap[key] = '';
        } else {
            //在JSON中移除数据
            delete $scope.searchMap.spec[key];
        }
        $scope.resultMap.pageNo = 1;
        $scope.search();
    }

    //构建分页页码数组
    buildPageLabel = function () {
        $scope.pageLabel = [];//初始化页码集合
        let firstPage = 1;//起始页码
        var lastPage = $scope.resultMap.totalPages;//结束页码
        var maxPage = $scope.resultMap.totalPages;//最大页码

        $scope.leftDot = true;
        $scope.rightDot = true;

        if (maxPage > 5) {//总页数大于5页
            if ($scope.searchMap.pageNo <= 3) {
                lastPage = 5;
                $scope.leftDot = false;
            } else if ($scope.searchMap.pageNo >= maxPage - 2) {
                firstPage = maxPage - 4;
                $scope.rightDot = false;
            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        } else {
            $scope.leftDot = false;
            $scope.rightDot = false;
        }
        //设置页码集合元素
        for (var i = firstPage; i <= lastPage; i++) {
            $scope.pageLabel.push(i);
        }
    }

    //提交页码查询
    $scope.queryByPage = function (pageNo) {
        //格式校验
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return;
        }
        $scope.searchMap.pageNo = pageNo;
        //执行查询
        $scope.search();
    }

    //判断是否是当前页
    $scope.isPage = function (pageNo) {
        return $scope.searchMap.pageNo === pageNo;
    }

    //排序查询
    $scope.sortSearch = function (sort, sortField) {
        $scope.searchMap.sort = sort;
        $scope.searchMap.sortField = sortField;
        $scope.search();
    }

    //搜索关键字如果包含品牌信息，则隐藏品牌列表
    $scope.keywordIsBrand = function () {
        for (var i = 0; i < $scope.resultMap.brandList.length; i++) {
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
                return true;
            }
        }
        return false;
    }

    $scope.loadKeyWords = function () {
        $scope.searchMap.keywords = $location.search()['keywords'];
        //执行查询
        $scope.search();
    }

});