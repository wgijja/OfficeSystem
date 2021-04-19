package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.offcn.cart.service.CartService;
import com.offcn.entity.Result;
import com.offcn.group.Cart;
import com.offcn.utils.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    /**
     * 查询购物车列表
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response) {

        String jsonStr = CookieUtil.getCookieValue(request, "cartList", "UTF-8");
        if (StringUtils.isEmpty(jsonStr)) {
            jsonStr = "[]";//这里是为了确保JSON转换的时候格式正确
        }
        List<Cart> cookieCartList = JSON.parseArray(jsonStr, Cart.class);

        //获取用户名
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if ("anonymousUser".equals(userName)) {
            //从cookie中返回列表
            return cookieCartList;
        } else {
            List<Cart> redisCartList = cartService.findCartListFromRedis(userName);
            //1、如果cookie中有购物车列表，则执行合并操作
            if (!CollectionUtils.isEmpty(cookieCartList)) {
                redisCartList = cartService.mergeCartList(cookieCartList, redisCartList);
                //2、更新缓存中的购物车列表
                cartService.saveCartListToRedis(userName, redisCartList);
                //3、清空cookie中的购物车列表
                CookieUtil.deleteCookie(request, response, "cartList");
            }
            //从缓存中返回列表
            return redisCartList;
        }
    }

    @RequestMapping("/addGoodsToCartList")
    @CrossOrigin(origins = "http://localhost:9105", allowCredentials = "true")//参数二可以不用写，默认是允许的
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        try {
            //配置跨域解决方案 CORS    可以使用springMVC注解
            //response.setHeader("Access-Control-Allow-Origin","http://localhost:9105");
            //response.setHeader("Access-Control-Allow-Credentials","true");

            //取得购物车列表
            List<Cart> cartList = this.findCartList(request, response);
            //将商品加入购物车中
            cartList = cartService.addGoodsToCartList(cartList, itemId, num);

            //获取用户名
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            if ("anonymousUser".equals(userName)) {
                //将购物车列表重新存入cookie中
                CookieUtil.setCookie(request, response, "cartList", JSON.toJSONString(cartList), 60 * 60 * 12, "UTF-8");
            } else {
                //将购物车列表重新存入缓存中
                cartService.saveCartListToRedis(userName, cartList);
            }
            return new Result(true, "添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "添加失败");
        }
    }
}
