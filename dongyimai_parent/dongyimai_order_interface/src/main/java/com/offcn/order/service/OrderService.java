package com.offcn.order.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbOrder;
import com.offcn.pojo.TbPayLog;

import java.util.Date;
import java.util.List;

/**
 * 服务层接口
 * @author Administrator
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum,int pageSize);
	
	
	/**
	 * 增加
	*/
	public void add(TbOrder order);
	
	
	/**
	 * 修改
	 */
	public void update(TbOrder order);
	

	/**
	 * 根据ID获取实体
	 * @return
	 */
	public TbOrder findOne(Long orderId);
	
	
	/**
	 * 批量删除
	 */
	public void delete(Long [] orderIds);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbOrder order, int pageNum, int pageSize);

	/**
	 * 在缓存中查询支付日志
	 * @param userId
	 * @return
	 */
	TbPayLog searchPayLogFromRedis(String userId);

	/**
	 * 修改交易状态
	 * @param out_trade_no 订单支付编号
	 * @param transactionId 支付宝一站通返回的流水号
	 */
	void updateStatus(String out_trade_no,String transactionId);
}
