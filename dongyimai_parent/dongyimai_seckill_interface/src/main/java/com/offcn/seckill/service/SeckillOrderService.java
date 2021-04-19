package com.offcn.seckill.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbSeckillOrder;

import java.util.List;

/**
 * 服务层接口
 *
 * @author Administrator
 */
public interface SeckillOrderService {

    /**
     * 返回全部列表
     *
     * @return
     */
    public List<TbSeckillOrder> findAll();


    /**
     * 返回分页列表
     *
     * @return
     */
    public PageResult findPage(int pageNum, int pageSize);


    /**
     * 增加
     */
    public void add(TbSeckillOrder seckill_order);


    /**
     * 修改
     */
    public void update(TbSeckillOrder seckill_order);


    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    public TbSeckillOrder findOne(Long id);


    /**
     * 批量删除
     *
     * @param ids
     */
    public void delete(Long[] ids);

    /**
     * 分页
     *
     * @param pageNum  当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbSeckillOrder seckill_order, int pageNum, int pageSize);

    /**
     * 商家订单过滤查询
     *
     * @param seckillOrder
     * @param pageNum
     * @param pageSize
     * @param sellerId
     * @return
     */
    PageResult shopFilter(TbSeckillOrder seckillOrder, int pageNum, int pageSize, String sellerId);


    /**
     * 提交秒杀订单
     *
     * @param itemId
     * @param userId
     */
    void submitOrder(Long itemId, String userId);

    /**
     * 从缓存中查询订单
     *
     * @param userId
     * @return
     */
    TbSeckillOrder searchSeckillOrderFromRedis(String userId);

    /**
     * 保存订单到缓存中
     * @param orderId 订单编号
     * @param userId 用户id
     * @param transactionId 交易流水号
     */
    void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId);

    /**
     * 订单超时删除订单
     * @param userId 用户
     * @param orderId 订单ID
     */
    void deleteOrderFromRedis(String userId,Long orderId);

}
