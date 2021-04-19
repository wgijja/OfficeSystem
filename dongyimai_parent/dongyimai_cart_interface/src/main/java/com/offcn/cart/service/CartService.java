package com.offcn.cart.service;

import com.offcn.group.Cart;

import java.util.List;

public interface CartService {

    /**
     * 向购物车增加商品
     * @param srcCartList 原购物车列表
     * @param itemId SKUId
     * @param num 购买数量
     */
    List<Cart> addGoodsToCartList(List<Cart> srcCartList ,Long itemId,Integer num);

    /**
     * 根从缓存中查询购物车列表
     * @param userName 当前登陆人
     * @return
     */
    List<Cart> findCartListFromRedis(String userName);

    /**
     * 向缓存中保存购物车列表
     * @param userName
     * @param cartList
     */
    void saveCartListToRedis(String userName,List<Cart> cartList);

    /**
     * 合并购物车
     * @param cookieCartList
     * @param redisCartList
     * @return
     */
    List<Cart> mergeCartList(List<Cart> cookieCartList,List<Cart> redisCartList);
}
