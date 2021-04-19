package com.offcn.pay.service;

import java.util.Map;

public interface AliPayService {

    /**
     * 生成支付宝二维码链接
     *
     * @param out_trade_no 订单编号，不允许重复
     * @param total_fee    订单总金额 单位：分
     * @return
     */
    Map<String, Object> createNative(String out_trade_no, String total_fee);

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    Map<String, Object> queryPayStatus(String out_trade_no);

    /**
     * 撤消交易
     *
     * @param out_trade_no 订单编号
     * @return
     */
    void cancelOrder(String out_trade_no);
}
