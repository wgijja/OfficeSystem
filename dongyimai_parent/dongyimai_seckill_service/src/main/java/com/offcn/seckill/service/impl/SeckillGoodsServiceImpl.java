package com.offcn.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbSeckillGoodsMapper;
import com.offcn.pojo.*;
import com.offcn.pojo.TbSeckillGoodsExample.Criteria;
import com.offcn.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbGoodsMapper goodsMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbSeckillGoods> findAll() {
        return seckillGoodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbSeckillGoods seckillGoods) {
        seckillGoods.setCreateTime(new Date());
        seckillGoods.setStatus("0");
        TbGoods goods = goodsMapper.selectByPrimaryKey(seckillGoods.getGoodsId());
        seckillGoods.setSellerId(goods.getSellerId());
        seckillGoodsMapper.insert(seckillGoods);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbSeckillGoods seckillGoods) {
        seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbSeckillGoods findOne(Long id) {
        return seckillGoodsMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            seckillGoodsMapper.deleteByPrimaryKey(id);
        }
    }


    @Override
    public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbSeckillGoodsExample example = new TbSeckillGoodsExample();
        Criteria criteria = example.createCriteria();

        if (seckillGoods != null) {
            if (seckillGoods.getTitle() != null && seckillGoods.getTitle().length() > 0) {
                criteria.andTitleLike("%" + seckillGoods.getTitle() + "%");
            }
            if (seckillGoods.getSmallPic() != null && seckillGoods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + seckillGoods.getSmallPic() + "%");
            }
            if (seckillGoods.getSellerId() != null && seckillGoods.getSellerId().length() > 0) {
                criteria.andSellerIdLike("%" + seckillGoods.getSellerId() + "%");
            }
            if (seckillGoods.getStatus() != null && seckillGoods.getStatus().length() > 0) {
                criteria.andStatusLike("%" + seckillGoods.getStatus() + "%");
            }
            if (seckillGoods.getIntroduction() != null && seckillGoods.getIntroduction().length() > 0) {
                criteria.andIntroductionLike("%" + seckillGoods.getIntroduction() + "%");
            }
        }

        Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 查询所有秒杀商品
     *
     * @return
     */
    @Override
    public List<TbSeckillGoods> findSecKillGoodsList() {
        //从缓存中查询数据
        List<TbSeckillGoods> seckillList = redisTemplate.boundHashOps("seckillGoods").values();
        if (CollectionUtils.isEmpty(seckillList)) {
            TbSeckillGoodsExample tbSeckillGoodsExample = new TbSeckillGoodsExample();
            TbSeckillGoodsExample.Criteria criteria = tbSeckillGoodsExample.createCriteria();
            criteria.andStatusEqualTo("1");        //审核状态为审核通过的
            criteria.andStartTimeLessThan(new Date());    //开始时间
            criteria.andEndTimeGreaterThan(new Date());    //结束时间
            criteria.andNumGreaterThan(0);        //库存
            seckillList = seckillGoodsMapper.selectByExample(tbSeckillGoodsExample);
            //将查询出的数据同步到缓存中
            Map<Long, Object> map = new HashMap<>();
            for (TbSeckillGoods seckillGoods : seckillList) {
                map.put(seckillGoods.getId(), seckillGoods);
            }
            redisTemplate.boundHashOps("seckillGoods").putAll(map);
        } else {
            System.out.println("从缓存中查询数据");
        }
        return seckillList;
    }

    /**
     * 从缓存中查询秒杀商品详情
     *
     * @param id 注意取值时ID的类型要与缓存中的对应上
     * @return
     */
    @Override
    public TbSeckillGoods findItemFromRedis(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
    }
    /**
     * 审核商品
     *
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        if (ids!=null){
            for (Long id : ids) {
                //修改goods表数据
                TbSeckillGoods seckillGoods = seckillGoodsMapper.selectByPrimaryKey(id);
                seckillGoods.setStatus(status);
                seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
            }
            redisTemplate.delete("seckillGoods");
        }
    }

}
