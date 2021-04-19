package com.offcn.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.Result;
import com.offcn.pay.service.AliPayService;
import com.offcn.pojo.TbSeckillOrder;
import com.offcn.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/aliPay")
public class AliPayController {

    @Reference
    private AliPayService aliPayService;

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/createNative")
    public Map<String, Object> createNative() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbSeckillOrder tbSeckillOrder = seckillOrderService.searchSeckillOrderFromRedis(userId);
        if (tbSeckillOrder != null) {
            return aliPayService.createNative(tbSeckillOrder.getId()+"", (long)(tbSeckillOrder.getMoney().doubleValue()*100)+"");
        }else {
            return new HashMap<>();
        }

    }

    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String outTradeNo) {
        Result result = null;
        int i = 0;//计数器
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        while (true) {
            try {
                Map<String, Object> map = aliPayService.queryPayStatus(outTradeNo);
                if (map == null) {
                    System.out.println("查询失败");
                    result = new Result(false, "查询失败");
                    break;
                }
                if (map.get("status") != null && "TRADE_SUCCESS".equals(map.get("status"))) {
                    //orderService.updateStatus(outTradeNo, (String) map.get("tradeNo"));
                    seckillOrderService.saveOrderFromRedisToDb(userId,Long.parseLong(outTradeNo), (String) map.get("tradeNo"));
                    result = new Result(true, "支付成功");
                    break;
                }
                if (map.get("status") != null && "TRADE_CLOSED".equals(map.get("status"))) {
                    result = new Result(true, "未付款交易超时关闭，或支付完成后全额退款");
                    break;
                }
                if (map.get("status") != null && "TRADE_FINISHED".equals(map.get("status"))) {
                    result = new Result(true, "交易结束，不可退款");
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = new Result(false, "查询失败");
                break;
            }
            try {
                Thread.sleep(3000);
                if (++i >= 10) {//每三秒计数一次
                    result = new Result(false, "二维码超时");
                    //关闭交易
                    aliPayService.cancelOrder(outTradeNo);
                    //从缓存中删除订单
                    seckillOrderService.deleteOrderFromRedis(userId,Long.parseLong(outTradeNo));
                    break;
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}


