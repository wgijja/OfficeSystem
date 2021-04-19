package com.offcn.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbSeckillGoodsMapper;
import com.offcn.mapper.TbSeckillOrderMapper;
import com.offcn.pojo.TbSeckillGoods;
import com.offcn.pojo.TbSeckillOrder;
import com.offcn.pojo.TbSeckillOrderExample;
import com.offcn.pojo.TbSeckillOrderExample.Criteria;
import com.offcn.seckill.service.SeckillOrderService;
import com.offcn.seckill.utils.RedisLock;
import com.offcn.utils.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private TbSeckillOrderMapper seckillOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisLock redisLock;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillOrder> findAll() {
        return seckillOrderMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.insert(seckillOrder);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillOrder seckillOrder) {
        seckillOrderMapper.updateByPrimaryKey(seckillOrder);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillOrder findOne(Long id) {
        return seckillOrderMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillOrderMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillOrderExample example = new TbSeckillOrderExample();
        Criteria criteria = example.createCriteria();

        if (seckillOrder != null) {
            if (seckillOrder.getUserId() != null && seckillOrder.getUserId().length() > 0) {
                criteria.andUserIdLike("%" + seckillOrder.getUserId() + "%");
            }
            if (seckillOrder.getSellerId() != null && seckillOrder.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillOrder.getSellerId() + "%");
            }
            if (seckillOrder.getStatus() != null && seckillOrder.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillOrder.getStatus() + "%");
            }
            if (seckillOrder.getReceiverAddress() != null && seckillOrder.getReceiverAddress().length() > 0) {
                criteria.andReceiverAddressLike("%" + seckillOrder.getReceiverAddress() + "%");
            }
            if (seckillOrder.getReceiverMobile() != null && seckillOrder.getReceiverMobile().length() > 0) {
                criteria.andReceiverMobileLike("%" + seckillOrder.getReceiverMobile() + "%");
            }
            if (seckillOrder.getReceiver() != null && seckillOrder.getReceiver().length() > 0) {
                criteria.andReceiverLike("%" + seckillOrder.getReceiver() + "%");
            }
            if (seckillOrder.getTransactionId() != null && seckillOrder.getTransactionId().length() > 0) {
                criteria.andTransactionIdLike("%" + seckillOrder.getTransactionId() + "%");
            }
        }

        Page<TbSeckillOrder> page = (Page<TbSeckillOrder>) seckillOrderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 过滤商家的订单
     *
     * @param seckillOrder
     * @param pageNum
     * @param pageSize
     * @param sellerId
     * @return
     */
    public PageResult shopFilter(TbSeckillOrder seckillOrder, int pageNum, int pageSize, String sellerId) {
        PageResult pageResult = findPage(seckillOrder, pageNum, pageSize);
        List<TbSeckillOrder> rows = pageResult.getRows();
        rows.removeIf(tbSeckillOrder -> !seckillOrder.getSellerId().equals(sellerId));
        pageResult.setRows(rows);
        return pageResult;
    }

    /**
     * 提交秒杀订单
     *
     * @param itemId
     * @param userId
     */
    @Override
    public void submitOrder(Long itemId, String userId) {
        //加入分布式锁
        String lockName = "createOrderLock";
        long ex = 1 * 1000L;
        String value = String.valueOf(System.currentTimeMillis() + ex);
        boolean lock = redisLock.lock(lockName, value);
        if (lock) {
            //1、根据itemId在缓存中获取商品信息
            TbSeckillGoods seckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(itemId);
            if (seckillGoods == null) {
                //throw new RuntimeException("该商品不存在");
                System.out.println("该商品不存在咯");
                return;
            }
            if (seckillGoods.getStockCount() == 0) {
                //throw new RuntimeException("商品已经被抢光了哦");
                System.out.println("商品已经被抢光了哦");
                return;
            }
            //2、库存减一处理并更新到缓存中
            seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
            redisTemplate.boundHashOps("seckillGoods").put(itemId, seckillGoods);

            //3、设置秒杀订单的属性，并保存订单在缓存中
            TbSeckillOrder seckillOrder = new TbSeckillOrder();
            seckillOrder.setCreateTime(new Date());                     //创建时间
            seckillOrder.setId(idWorker.nextId());                      //秒杀订单编号
            seckillOrder.setSeckillId(itemId);                          //秒杀商品ID
            seckillOrder.setStatus("0");                                //未支付
            seckillOrder.setMoney(seckillGoods.getCostPrice());         //设置价格
            seckillOrder.setUserId(userId);                             //当前登陆人
            seckillOrder.setSellerId(seckillGoods.getSellerId());       //商家ID
            redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
            //4、判断秒杀商品的库存如果为0，则清空缓存中的商品，并同步回到数据库中
            if (seckillGoods.getStockCount() == 0) {
                redisTemplate.boundHashOps("seckillGoods").delete(itemId);
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            }
            redisLock.unlock(lockName, value);
        }
    }

    /**
     * 从缓存中查询订单
     *
     * @param userId
     * @return
     */
    @Override
    public TbSeckillOrder searchSeckillOrderFromRedis(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }

    /**
     * 保存订单到缓存中
     *
     * @param userId        用户id
     * @param orderId       订单编号
     * @param transactionId 交易流水号
     */
    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        //从缓存中查询订单
        TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if (tbSeckillOrder==null){
            throw new RuntimeException("该订单不存在");
        }
        //判断订单编号是否和缓存中的订单编号一致
        if (tbSeckillOrder.getId().longValue() == orderId.longValue()) {
            //设置订单属性
            tbSeckillOrder.setStatus("1");
            tbSeckillOrder.setPayTime(new Date());
            tbSeckillOrder.setTransactionId(transactionId);
            //保存订单到数据库
            seckillOrderMapper.insert(tbSeckillOrder);
            //清空缓存中的订单信息
            redisTemplate.boundHashOps("seckillOrder").delete(userId);
        }
    }

    /**
     * 订单超时删除订单
     *
     * @param userId  用户
     * @param orderId 订单ID
     */
    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        //1、根据userID查询订单信息
        TbSeckillOrder tbSeckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        //2、根据订单编号校对订单
        if (tbSeckillOrder!=null && tbSeckillOrder.getId().longValue()==orderId.longValue()){
            //3、从缓存中删除订单信息
            redisTemplate.boundHashOps("seckillOrder").delete(userId);
        }
        //4、查询秒杀商品信息
        TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(tbSeckillOrder.getSeckillId());
        if (tbSeckillGoods==null){
            TbSeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(tbSeckillOrder.getSeckillId());
            if (seckillGoods!=null && seckillGoods.getEndTime().getTime()>System.currentTimeMillis()){
                tbSeckillGoods = seckillGoods;
                seckillGoods.setStockCount(seckillGoods.getStockCount()+1);
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            }else {
                throw new RuntimeException("该秒杀商品不存在");
            }
        }
        //5、对库存加一
        tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()+1);
        //6、重新将秒杀商品信息放回缓存
        redisTemplate.boundHashOps("seckillGoods").put(tbSeckillGoods.getId(),tbSeckillGoods);

    }

}
