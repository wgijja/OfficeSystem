package com.offcn.shop.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.pojo.TbSeller;
import com.offcn.sellergoods.service.SellerService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全认证框架的自定义认证类
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Reference
    private SellerService sellerService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //1、创建权限集合
        List<GrantedAuthority> list = new ArrayList<>();
        //2、添加自定义权限
        list.add(new SimpleGrantedAuthority("ROLE_SELLER"));
        //根据用户ID查询商家信息
        TbSeller seller = sellerService.findOne(username);
        if (seller != null) {
            //判断审核状态是否为审核通过
            if ("1".equals(seller.getStatus())) {
                //3、完成用户名和密码的匹配
                return new User(username, seller.getPassword(), list);
            } else {
                return null;
            }

        } else {
            return null;
        }
    }
}
