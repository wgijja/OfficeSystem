package com.offcn.serarch.service;

import com.offcn.pojo.TbItem;

import java.util.List;
import java.util.Map;

/**
 * 搜索模块的查询接口
 */
public interface ItemSearchService {

    /**
     * 搜索商品
     * @param searchMap
     * @return
     */
    Map<String,Object> search(Map searchMap);

    /**
     * 导入SKU数据
     * @param tbItemList
     */
    void importItem(List<TbItem> tbItemList);

    /**
     * 删除SKU数据
     * @param goodsIds
     */
    void deleteByGoodsIds(List<Long> goodsIds);
}
