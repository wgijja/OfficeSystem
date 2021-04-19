package com.offcn.task.service;

import com.offcn.mapper.TbSeckillGoodsMapper;
import com.offcn.pojo.TbSeckillGoods;
import com.offcn.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class TaskService {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 每30查询数据库
     * 将符合条件的数据同步到缓存中
     */
    @Scheduled(cron = "0/30 * * * * ?")
    public void refreshSeckillGoods() {
        System.out.println("增量更新秒杀商品任务执行中...");

        Set set = redisTemplate.boundHashOps("seckillGoods").keys();

        TbSeckillGoodsExample seckillGoodsExample = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = seckillGoodsExample.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStockCountGreaterThan(0);
        criteria.andStartTimeLessThan(new Date());
        criteria.andEndTimeGreaterThan(new Date());

        if (!CollectionUtils.isEmpty(set)) {
            criteria.andIdNotIn(new ArrayList<Long>(set));
        }
        List<TbSeckillGoods> seckillGoodsList = seckillGoodsMapper.selectByExample(seckillGoodsExample);
        if (!CollectionUtils.isEmpty(seckillGoodsList)) {
            Map<Long, Object> map = new HashMap<>();
            for (TbSeckillGoods skg : seckillGoodsList) {
                map.put(skg.getId(), skg);
            }
            redisTemplate.boundHashOps("seckillGoods").putAll(map);
            System.out.println("已向缓存中保存商品");
        }
    }


    @Scheduled(cron = "* * * * * ?")
    public void removeSeckillGoodsFromRedis() {
        System.out.println("移除秒杀商品任务执行中...");
        List<TbSeckillGoods> seckillGoods = redisTemplate.boundHashOps("seckillGoods").values();
        if (!CollectionUtils.isEmpty(seckillGoods)) {
            for (TbSeckillGoods skg : seckillGoods) {
                if (skg.getEndTime().getTime() < (new Date()).getTime()) {
                    redisTemplate.boundHashOps("seckillGoods").delete(skg.getId());
                    System.out.println("移除秒杀商品");
                }
            }
        }
    }
}
