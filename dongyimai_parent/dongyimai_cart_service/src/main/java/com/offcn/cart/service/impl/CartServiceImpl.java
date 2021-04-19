package com.offcn.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.offcn.cart.service.CartService;
import com.offcn.group.Cart;
import com.offcn.mapper.TbItemMapper;
import com.offcn.pojo.TbItem;
import com.offcn.pojo.TbOrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class  CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 向购物车增加商品
     *
     * @param srcCartList 原购物车列表
     * @param itemId      SKUId
     * @param num         购买数量
     */
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> srcCartList, Long itemId, Integer num) {
        //1、根据SKUID查询SKU信息
        TbItem item = itemMapper.selectByPrimaryKey(itemId);
        //判断是否存在
        if (item == null) {
            throw new RuntimeException("商品不存在");
        }
        //判断商品是否审核
        if (!"1".equals(item.getStatus())) {
            throw new RuntimeException("商品未审核，不能购买！");
        }
        //2、根据商品中的商家ID获取商家购物车对象
        Cart cart = searchCartBySellerId(srcCartList, item.getSellerId());
        //判断商家购物车对象是否存在
        if (cart == null) {
            cart = new Cart();
            cart.setSellerId(item.getSellerId());
            cart.setSellerName(item.getSeller());
            //构建订单 详情列表
            List<TbOrderItem> orderItemList = new ArrayList<>();
            orderItemList.add(this.createOrderItem(item, num));
            cart.setOrderItemList(orderItemList);
            //将商家购物车对象设置回购物车列表
            srcCartList.add(cart);
        } else {
            //根据SKUID获取订单详情
            TbOrderItem orderItem = this.searchOrderItemByItemId(cart.getOrderItemList(), itemId, num);
            //判断订单详情是否存在
            if (orderItem == null) {
                orderItem = createOrderItem(item, num);
                cart.getOrderItemList().add(orderItem);
            } else {
                orderItem.setNum(orderItem.getNum() + num);
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //判断是否数量还为正数
                if (orderItem.getNum() == 0) {
                    cart.getOrderItemList().remove(orderItem);
                }
                //判断购物车中是否还有东西
                if (cart.getOrderItemList().size() == 0) {
                    srcCartList.remove(cart);
                }
            }
        }
        return srcCartList;
    }

    /**
     * 根从缓存中查询购物车列表
     *
     * @param userName 当前登陆人
     * @return
     */
    @Override
    public List<Cart> findCartListFromRedis(String userName) {
        List<Cart> cartList =  (List<Cart>) redisTemplate.boundHashOps("cartList").get(userName);
        if (CollectionUtils.isEmpty(cartList)){
            return new ArrayList<>();
        }else {
            return cartList;
        }
    }

    /**
     * 向缓存中保存购物车列表
     *
     * @param userName
     * @param cartList
     */
    @Override
    public void saveCartListToRedis(String userName, List<Cart> cartList) {
        redisTemplate.boundHashOps("cartList").put(userName,cartList);
        System.out.println("已存入缓存");
    }

    /**
     * 合并购物车
     *
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    @Override
    public List<Cart> mergeCartList(List<Cart> cookieCartList, List<Cart> redisCartList) {
        for (Cart cart :cookieCartList) {
            for (TbOrderItem orderItem :cart.getOrderItemList()) {
                redisCartList = this.addGoodsToCartList(redisCartList, orderItem.getItemId(), orderItem.getNum());
            }
        }
        return redisCartList;
    }

    //根据商品中的商家ID获取商家购物车对象
    private Cart searchCartBySellerId(List<Cart> srcCartList, String sellerId) {
        for (Cart cart : srcCartList) {
            if (sellerId.equals(cart.getSellerId())) {
                return cart;
            }
        }
        return null;
    }

    //构建订单详情对象
    private TbOrderItem createOrderItem(TbItem item, Integer num) {
        if (num < 1) {
            throw new RuntimeException("购买数量不合法");
        }
        TbOrderItem orderItem = new TbOrderItem();
        orderItem.setItemId(item.getId());//SKUID
        orderItem.setGoodsId(item.getGoodsId());//SPUID
        orderItem.setTitle(item.getTitle());//标题
        orderItem.setSellerId(item.getSellerId());//商家ID
        orderItem.setPicPath(item.getImage());//图片
        orderItem.setPrice(item.getPrice());//单价
        orderItem.setNum(num);//数量
        orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * num));//总金额
        return orderItem;
    }

    //根据SKUID获取订单详情
    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId, Integer num) {
        for (TbOrderItem orderItem : orderItemList) {
            if (orderItem.getItemId().longValue() == itemId.longValue()) {
                return orderItem;
            }
        }
        return null;
    }
}
