package com.offcn.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.pojo.TbAddress;
import com.offcn.user.service.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

/**
 * controller
 *
 * @author Administrator
 */
@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findAll")
    public List<TbAddress> findAll() {
        return addressService.findAll();
    }


    /**
     * 返回全部列表
     *
     * @return
     */
    @RequestMapping("/findPage")
    public PageResult findPage(int page, int rows) {
        return addressService.findPage(page, rows);
    }

    /**
     * 增加
     *
     * @param address
     * @return
     */
    @RequestMapping("/add")
    public Result add(@RequestBody TbAddress address) {
        try {
            String userName = SecurityContextHolder.getContext().getAuthentication().getName();
            address.setUserId(userName);
            address.setCreateDate(new Date());
            address.setIsDefault("0");
            addressService.add(address);
            return new Result(true, "增加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加失败");
        }
    }

    /**
     * 修改
     *
     * @param address
     * @return
     */
    @RequestMapping("/update")
    public Result update(@RequestBody TbAddress address) {
        try {
            addressService.update(address);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 获取实体
     *
     * @param id
     * @return
     */
    @RequestMapping("/findOne")
    public TbAddress findOne(Long id) {
        return addressService.findOne(id);
    }

    /**
     * 批量删除
     *
     * @return
     */
    @RequestMapping("/delete")
    public Result delete(Long id) {
        try {
            addressService.delete(id);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 查询+分页
     *
     * @param page
     * @param rows
     * @return
     */
    @RequestMapping("/search")
    public PageResult search(@RequestBody TbAddress address, int page, int rows) {
        return addressService.findPage(address, page, rows);
    }

    /**
     * 根据当前登陆人查询收货地址列表
     *
     * @return
     */
    @RequestMapping("/findAddressListByUserId")
    public List<TbAddress> findAddressListByUserId() {
        //获取当前登陆人
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findAddressListByUserId(userId);
    }

}
