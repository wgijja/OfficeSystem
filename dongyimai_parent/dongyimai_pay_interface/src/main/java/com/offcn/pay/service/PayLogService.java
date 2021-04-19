package com.offcn.pay.service;

import com.offcn.entity.PageResult;
import com.offcn.pojo.TbOrder;
import com.offcn.pojo.TbPayLog;

import java.util.List;

public interface PayLogService {

    /**
     * 分页
     * @param page 当前页码
     * @param rows 每页记录数
     * @return
     */
    PageResult search(TbPayLog tbPayLog, int page, int rows);
}
