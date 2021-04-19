package com.offcn.page.service;

/**
 * 商品详情页接口
 */
public interface ItemPageService {

    /**
     * 生成商品详情页
     * @param goodsId
     * @return
     */
    boolean createItemPage(Long goodsId);

    /**
     * 删除商品详情页面
     */
    void deleteItemPage(Long[] ids);
}
