package com.offcn.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.entity.Result;
import com.offcn.group.Goods;
import com.offcn.mapper.*;
import com.offcn.pojo.*;
import com.offcn.pojo.TbGoodsExample.Criteria;
import com.offcn.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbSellerMapper sellerMapper;
    @Autowired
    private TbItemMapper itemMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(Goods goods) {
        //1、设置商品审核状态
        goods.getGoods().setAuditStatus("0");
        //2、添加SPU信息
        goodsMapper.insert(goods.getGoods());
        //3、添加SPU的主键ID
        Long id = goods.getGoods().getId();
        //4、添加商品扩展信息
        goods.getGoodsDesc().setGoodsId(id);
        goodsDescMapper.insert(goods.getGoodsDesc());
        //5、添加SKU信息
        this.setItem(goods);
    }

    private void setItemValue(Goods goods, TbItem item) {
        item.setCreateTime(new Date());                                //创建时间
        item.setUpdateTime(new Date());                                //更新时间
        item.setCategoryid(goods.getGoods().getCategory3Id());        //分类ID
        item.setGoodsId(goods.getGoods().getId());                    //SPU的ID
        item.setSellerId(goods.getGoods().getSellerId());            //商家ID
        //根据分类ID查询分类信息，设置分类名称
        TbItemCat itemCat = itemCatMapper.selectByPrimaryKey(item.getCategoryid());
        item.setCategory(itemCat.getName());
        //根据商品ID查询商品信息，设置品牌名称
        TbBrand brand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
        item.setBrand(brand.getName());
        //根据商家ID查询商家信息，设置商家名称
        TbSeller seller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
        item.setSeller(seller.getNickName());
        //设置图片路径
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (!CollectionUtils.isEmpty(imageList)) {
            String url = (String) imageList.get(0).get("url");
            item.setImage(url);
        }
    }


    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        //1、重置商品的审核新动态
        goods.getGoods().setAuditStatus("0");
        //2、修改SPU的对象信息
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        //3、修改商品的扩展信息
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        //4、根据商品ID删除SKU表数据
        TbItemExample itemExample = new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(itemExample);
        //5、重新添加SKU表数据
        this.setItem(goods);

    }

    private void setItem(Goods goods) {
        //判断是否启用规格
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {
            if (!CollectionUtils.isEmpty(goods.getItemList())) {
                for (TbItem item : goods.getItemList()) {
                    //拼接SKU名称 SPU名称+规格选项
                    String title = goods.getGoods().getGoodsName();
                    Map<String, String> specMap = JSON.parseObject(item.getSpec(), Map.class);
                    for (String key : specMap.keySet()) {
                        title += " " + specMap.get(key);
                    }
                    item.setTitle(title);                                        //SKUq名称
                    this.setItemValue(goods, item);
                    itemMapper.insert(item);
                }
            }
        } else {
            //设置SKU数据为默认数据
            TbItem item = new TbItem();
            item.setTitle(goods.getGoods().getGoodsName());
            item.setPrice(goods.getGoods().getPrice());
            item.setNum(9999);
            item.setStatus("1");
            item.setIsDefault("1");
            item.setSpec("{}");
            this.setItemValue(goods, item);
            itemMapper.insert(item);
        }
    }

    /**
     * 根据ID获取复合实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        //根据ID查询SPU信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        //根据ID查询商品扩展信息
        TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        //根据ID作为查询条件查询SKU列表
        TbItemExample itemExample = new TbItemExample();
        TbItemExample.Criteria criteria = itemExample.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> itemList = itemMapper.selectByExample(itemExample);
        //设置复合实体
        Goods goods = new Goods();
        goods.setGoods(tbGoods);
        goods.setGoodsDesc(goodsDesc);
        goods.setItemList(itemList);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            //逻辑删除
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            //执行修改
            goodsMapper.updateByPrimaryKey(goods);
            //根据SPU的ID查询出SKU集合
            List<TbItem> itemList = this.findItemListByGoodsIdAndStatus(ids, "1");
            //设置SKU状态为0
            if (!CollectionUtils.isEmpty(itemList)) {
                for (TbItem item:itemList){
                    item.setStatus("0");
                    //执行修改操作
                    itemMapper.updateByPrimaryKey(item);
                }
            }
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            criteria.andIsDeleteIsNull();
        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 审核商品
     *
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id : ids) {
            //修改goods表数据
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
            //根据ID查询SKU表数据
            TbItemExample itemExample = new TbItemExample();
            TbItemExample.Criteria criteria = itemExample.createCriteria();
            criteria.andGoodsIdEqualTo(id);
            List<TbItem> itemList = itemMapper.selectByExample(itemExample);
            for (TbItem item : itemList) {
                item.setStatus(status);
                itemMapper.updateByPrimaryKey(item);
            }
        }
    }

    /**
     * 设置商品上下架
     *
     * @param ids
     * @param isMarketable
     */
    @Override
    public Result updateIsMarketable(Long[] ids, String isMarketable) {
        boolean res = true;
        for (Long id : ids) {
            TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
            if (!"1".equals(tbGoods.getAuditStatus())) {
                res = false;
            }
        }
        if (res) {
            for (Long id : ids) {
                TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
                tbGoods.setIsMarketable(isMarketable);
                goodsMapper.updateByPrimaryKey(tbGoods);
            }
            return new Result(true, "修改成功");
        } else {
            return new Result(false, "只能对审核通过商品进行操作！");
        }
    }

    /**
     * 根据SPU的ID和审核状态查询SKU列表
     *
     * @param ids
     * @param status
     */
    @Override
    public List<TbItem> findItemListByGoodsIdAndStatus(Long[] ids, String status) {
        TbItemExample tbItemExample = new TbItemExample();
        TbItemExample.Criteria criteria = tbItemExample.createCriteria();
        criteria.andGoodsIdIn(Arrays.asList(ids));
        criteria.andStatusEqualTo(status);
        return itemMapper.selectByExample(tbItemExample);

    }
}
