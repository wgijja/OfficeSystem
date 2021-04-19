package com.offcn.page.service.impl;

import com.offcn.mapper.TbGoodsDescMapper;
import com.offcn.mapper.TbGoodsMapper;
import com.offcn.mapper.TbItemCatMapper;
import com.offcn.mapper.TbItemMapper;
import com.offcn.page.service.ItemPageService;
import com.offcn.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    private TbGoodsMapper tbGoodsMapper;

    @Autowired
    private TbGoodsDescMapper tbGoodsDescMapper;

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemCatMapper tbItemCatMapper;

    @Value("${path}")
    private String path;

    /**
     * 生成商品详情页
     *
     * @param goodsId
     * @return
     */
    @Override
    public boolean createItemPage(Long goodsId) {
        try {
            //1、创建freemarker配置对象
            Configuration configuration = freeMarkerConfigurer.getConfiguration();
            //2、创建模板对象
            Template template = configuration.getTemplate("item.ftl");
            //3、查询SPU信息
            TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
            //4、查询商品扩展信息
            TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);

            //查询三级分类信息
            TbItemCat category1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
            TbItemCat category2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
            TbItemCat category3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());

            //5、查询SKU信息列表
            TbItemExample tbItemExample = new TbItemExample();
            TbItemExample.Criteria criteria = tbItemExample.createCriteria();
            criteria.andGoodsIdEqualTo(goodsId);
            criteria.andStatusEqualTo("1");
            tbItemExample.setOrderByClause("is_default desc");//根据默认值倒序排列
            List<TbItem> itemList = itemMapper.selectByExample(tbItemExample);

            //6、构建数据源
            Map<String, Object> dataSource = new HashMap();
            dataSource.put("goods", tbGoods);
            dataSource.put("goodsDesc", tbGoodsDesc);
            dataSource.put("category1", category1);
            dataSource.put("category2", category2);
            dataSource.put("category3", category3);
            dataSource.put("itemList", itemList);
            //7、生成静态页面
            FileWriter out = new FileWriter(new File(path + goodsId + ".html"));
            template.process(dataSource, out);
            return true;
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除商品详情页面
     *
     * @param ids
     */
    @Override
    public void deleteItemPage(Long[] ids) {
        for (Long id : ids) {
            new File(path + id + ".html").delete();
        }
    }
}
