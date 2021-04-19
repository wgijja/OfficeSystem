package com.offcn.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.group.Cart;
import com.offcn.mapper.TbOrderItemMapper;
import com.offcn.mapper.TbOrderMapper;
import com.offcn.mapper.TbPayLogMapper;
import com.offcn.order.service.OrderService;
import com.offcn.pojo.TbOrder;
import com.offcn.pojo.TbOrderExample;
import com.offcn.pojo.TbOrderExample.Criteria;
import com.offcn.pojo.TbOrderItem;
import com.offcn.pojo.TbPayLog;
import com.offcn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbOrder> findAll() {
        return orderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbOrder order) {
        //1、根据当前登陆人在缓存中取得购物车列表
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());
        if (!CollectionUtils.isEmpty(cartList)) {
            double totalAmount = 0.00;
            List<String> orderList = new ArrayList<>();
            //2、遍历购物车列表保存订单信息
            for (Cart cart : cartList) {
                long orderId = idWorker.nextId();
                System.out.println(orderId);
                orderList.add(orderId + "");
                TbOrder tbOrder = new TbOrder();
                tbOrder.setOrderId(orderId);                                   //订单编号
                tbOrder.setPaymentType(order.getPaymentType());                //支付类型，1、在线支付，2、货到付款
                tbOrder.setStatus("1");                                        //订单状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
                tbOrder.setCreateTime(new Date());                             //创建时间
                tbOrder.setUpdateTime(new Date());                             //更新时间
                tbOrder.setUserId(order.getUserId());                          //用户
                tbOrder.setReceiver(order.getReceiver());                      //收货人
                tbOrder.setReceiverMobile(order.getReceiverMobile());          //联系人电话
                tbOrder.setReceiverAreaName(order.getReceiverAreaName());      //收货地址
                tbOrder.setSourceType("2");                                    //订单来源：1:app端，2：pc端，3：M端，4：微信端，5：手机qq端
                tbOrder.setSellerId(order.getSellerId());
                double totalFee = 0.00;
                //3、遍历订单详情集合，保存订单详情
                for (TbOrderItem orderItem : cart.getOrderItemList()) {
                    orderItem.setId(idWorker.nextId());
                    orderItem.setOrderId(orderId);
                    totalFee += orderItem.getTotalFee().doubleValue();
                    orderItemMapper.insert(orderItem);
                }
                totalAmount += totalFee;
                tbOrder.setPayment(new BigDecimal(totalFee));
                orderMapper.insert(tbOrder);
                //4、清空缓存中的购物车列表
                redisTemplate.boundHashOps("cartList").delete(order.getUserId());
            }
            //如果支付订单类型为线上支付，则需要保存支付日志
            if ("1".equals(order.getPaymentType())) {
                TbPayLog tbPayLog = new TbPayLog();
                tbPayLog.setOutTradeNo(idWorker.nextId() + "");//支付订单号
                tbPayLog.setCreateTime(new Date());//创建日期
                //元转分
                BigDecimal total_big = new BigDecimal(totalAmount);
                BigDecimal multiplier = new BigDecimal(100L);
                BigDecimal total_fee = total_big.multiply(multiplier);
                tbPayLog.setTotalFee(total_fee.longValue());//支付金额 单位：分
                tbPayLog.setUserId(order.getUserId());
                tbPayLog.setTradeState("0");//未支付
                String orderIds = orderList.toString().replace("[", "").replace("]", "").replace(" ", "");
                tbPayLog.setOrderList(orderIds);//订单编号列表
                tbPayLog.setPayType("1");
                //保存到数据库
                payLogMapper.insert(tbPayLog);
                //保存到redis
                redisTemplate.boundHashOps("payLog").put(order.getUserId(), tbPayLog);
            }
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(TbOrder order) {
        orderMapper.updateByPrimaryKey(order);
    }

    /**
     * 根据ID获取实体
     *
     * @return
     */
    @Override
    public TbOrder findOne(Long orderId) {
        return orderMapper.selectByPrimaryKey(orderId);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] orderIds) {
        for (Long orderId : orderIds) {
            orderMapper.deleteByPrimaryKey(orderId);
        }
    }


    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example = new TbOrderExample();
        Criteria criteria = example.createCriteria();

        if (order != null) {
            if (order.getPaymentType() != null && order.getPaymentType().length() > 0) {
                criteria.andPaymentTypeLike("%" + order.getPaymentType() + "%");
            }
            if (order.getPostFee() != null && order.getPostFee().length() > 0) {
                criteria.andPostFeeLike("%" + order.getPostFee() + "%");
            }
            if (order.getStatus() != null && order.getStatus().length() > 0) {
                criteria.andStatusLike("%" + order.getStatus() + "%");
            }
            if (order.getShippingName() != null && order.getShippingName().length() > 0) {
                criteria.andShippingNameLike("%" + order.getShippingName() + "%");
            }
            if (order.getShippingCode() != null && order.getShippingCode().length() > 0) {
                criteria.andShippingCodeLike("%" + order.getShippingCode() + "%");
            }
            if (order.getUserId() != null && order.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + order.getUserId() + "%");
            }
            if (order.getBuyerMessage() != null && order.getBuyerMessage().length() > 0) {
                criteria.andBuyerMessageLike("%" + order.getBuyerMessage() + "%");
            }
            if (order.getBuyerNick() != null && order.getBuyerNick().length() > 0) {
                criteria.andBuyerNickLike("%" + order.getBuyerNick() + "%");
            }
            if (order.getBuyerRate() != null && order.getBuyerRate().length() > 0) {
                criteria.andBuyerRateLike("%" + order.getBuyerRate() + "%");
            }
            if (order.getReceiverAreaName() != null && order.getReceiverAreaName().length() > 0) {
                criteria.andReceiverAreaNameLike("%" + order.getReceiverAreaName() + "%");
            }
            if (order.getReceiverMobile() != null && order.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + order.getReceiverMobile() + "%");
            }
            if (order.getReceiverZipCode() != null && order.getReceiverZipCode().length() > 0) {
                criteria.andReceiverZipCodeLike("%" + order.getReceiverZipCode() + "%");
            }
            if (order.getReceiver() != null && order.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + order.getReceiver() + "%");
            }
            if (order.getInvoiceType() != null && order.getInvoiceType().length() > 0) {
                criteria.andInvoiceTypeLike("%" + order.getInvoiceType() + "%");
            }
            if (order.getSourceType() != null && order.getSourceType().length() > 0) {
                criteria.andSourceTypeLike("%" + order.getSourceType() + "%");
            }
            if (order.getSellerId() != null && order.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + order.getSellerId() + "%");
            }
        }

        Page<TbOrder> page = (Page<TbOrder>) orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 在缓存中查询支付日志
     *
     * @param userId
     * @return
     */
    @Override
    public TbPayLog searchPayLogFromRedis(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps("payLog").get(userId);
    }

    /**
     * 修改交易状态
     *
     * @param out_trade_no  订单支付编号
     * @param transactionId 支付宝一站通返回的流水号
     */
    @Override
    public void updateStatus(String out_trade_no, String transactionId) {
        TbPayLog tbPayLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        tbPayLog.setPayTime(new Date());//支付时间
        tbPayLog.setTransactionId(transactionId);//流水号
        tbPayLog.setTradeState("1");//已支付
        payLogMapper.updateByPrimaryKey(tbPayLog);
        String[] orderIds = tbPayLog.getOrderList().split(",");
        for (String orderId : orderIds) {
            TbOrder tbOrder = orderMapper.selectByPrimaryKey(Long.parseLong(orderId));
            tbOrder.setStatus("2");//已付款
            tbOrder.setPaymentTime(new Date());
            orderMapper.updateByPrimaryKey(tbOrder);
        }
        //清空缓存中的支付日志
        redisTemplate.boundHashOps("payLog").delete(tbPayLog.getUserId());
    }

}
