app.controller('seckillController', function ($scope, $location, $interval, seckillService) {

    //查询秒杀商品列表
    $scope.findSecKillGoodsList = function () {
        seckillService.findSecKillGoodsList().success(
            function (response) {
                $scope.list = response;
            }
        );
    }

    $scope.picHref=function (id){
        location.href="seckill-item.html#?id="+id;
    }

    //秒杀商品详情
    $scope.findItemFromRedis = function () {
        var id = $location.search()['id'];
        if (id == null) {
            return;
        }
        seckillService.findItemFromRedis(id).success(
            function (response) {
                $scope.item = response;
                var seconds = Math.floor(((new Date(response.endTime).getTime()) - (new Date().getTime())) / 1000);
                //倒计时
                $interval(function () {
                    if (seconds > 0) {
                        seconds -= 1;
                        $scope.timeStr = formatTime(seconds);
                    } else {
                        $interval.cancel();
                    }
                }, 1000);
            }
        );
    }

    //格式化时间
    formatTime = function (seconds) {

        //天
        var day = Math.floor(seconds / (60 * 60 * 24));
        //时
        var hour = Math.floor((seconds - (day * 60 * 60 * 24)) / (60 * 60));
        //分
        var minute = Math.floor((seconds - (day * 60 * 60 * 24) - (hour * 60 * 60)) / 60);
        //秒
        var s = Math.floor((seconds - (day * 60 * 60 * 24) - (hour * 60 * 60) - (minute * 60)));

        var restOfTime = "";
        if (day > 0) {
            restOfTime += day + "天 ";
        }
        restOfTime += " " + hour + ":" + minute + ":" + s + "";
        return restOfTime;
    }

    $scope.submitOrder = function () {
        seckillService.submitOrder($scope.item.id).success(
            function (response) {
                if (response.success) {
                    location.href = "pay.html";
                } else {
                    if (response.message === "请先登陆再抢购哦") {
                        //设置跳板页
                        location.href = "login.html";
                    } else {
                        alert(response.message);
                    }
                }
            }
        );
    }

    $scope.createNative = function () {
        seckillService.createNative().success(
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
        seckillService.queryPayStatus(outTradeNo).success(
            function (response) {
                if (response.success) {
                    //交易成功
                    location.href = "paysuccess.html#?money="+$scope.totalAmount;
                } else {
                    if (response.message === "二维码超时") {
                        location.href = "payfail.html";
                    } else {
                        location.href = "payfail.html";
                    }
                }
            }
        );
    }

    //获取路由过来的信息
    $scope.getMoney = function (){
        return $location.search()['money'];
    }

});