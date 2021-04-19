package com.offcn.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradeCancelRequest;
import com.alipay.api.request.AlipayTradePrecreateRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeCancelResponse;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.offcn.pay.service.AliPayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
public class AliPayServiceImpl implements AliPayService {


    @Autowired
    private AlipayClient alipayClient;

    /**
     * 生成支付宝二维码链接
     *
     * @param out_trade_no 订单编号，不允许重复
     * @param total_fee    订单总金额  单位：分
     * @return
     */
    @Override
    public Map<String, Object> createNative(String out_trade_no, String total_fee) {
        Map<String, Object> resultMap = new HashMap<>();

        //单位转换 元转分
        long total_fee_long = Long.parseLong(total_fee);
        BigDecimal total_fee_big = new BigDecimal(total_fee_long);
        BigDecimal divisor = new BigDecimal(100L);
        BigDecimal total_amount = total_fee_big.divide(divisor);

        AlipayTradePrecreateRequest request = new AlipayTradePrecreateRequest(); //创建API对应的request类
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + out_trade_no + "\"," + //商户订单号
                "\"total_amount\":\"" + total_amount.doubleValue() + "\"," +
                "\"subject\":\"Iphone6 16G\"," +
                "\"store_id\":\"NJ_001\"," +
                "\"timeout_express\":\"90m\"}"); //订单允许的最晚付款时间
        try {
            AlipayTradePrecreateResponse response = alipayClient.execute(request);
            System.out.print(response.getBody());
            //根据response中的结果继续业务逻辑处理
            String code = response.getCode();//返回响应状态
            if ("10000".equals(code)) {
                resultMap.put("outTradeNo", response.getOutTradeNo());//订单编号
                resultMap.put("totalAmount", total_fee);//支付金额
                resultMap.put("qrCode", response.getQrCode());//二维码链接
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 查询支付状态
     *
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, Object> queryPayStatus(String out_trade_no) {
        Map<String, Object> resultMap = new HashMap<>();
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + out_trade_no + "\"," +
                "\"trade_no\":\"\"}"); //设置业务参数
        try {
            AlipayTradeQueryResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
            System.out.println(response.getBody());
            //根据response中的结果继续业务逻辑处理
            String code = response.getCode();
            if ("10000".equals(code)) {
                resultMap.put("outTradeNo", response.getOutTradeNo());
                resultMap.put("tradeNo", response.getTradeNo());//支付宝平台返回的交易流水号
                resultMap.put("status", response.getTradeStatus());//交易状态
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    /**
     * 撤消交易
     *
     * @param out_trade_no 订单编号
     * @return
     */
    @Override
    public void cancelOrder(String out_trade_no) {
        AlipayTradeCancelRequest request = new AlipayTradeCancelRequest();//创建API对应的request类
        request.setBizContent("{" +
                "\"out_trade_no\":\""+out_trade_no+"\"," +
                "\"trade_no\":\"\"}"); //设置业务参数
        try {
            AlipayTradeCancelResponse response = alipayClient.execute(request);//通过alipayClient调用API，获得对应的response类
            System.out.print(response.getBody());
            //根据response中的结果继续业务逻辑处理
            //暂无更多需求
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
    }
}
