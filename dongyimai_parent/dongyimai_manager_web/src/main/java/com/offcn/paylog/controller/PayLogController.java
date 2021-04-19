package com.offcn.paylog.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.offcn.entity.PageResult;
import com.offcn.pay.service.PayLogService;
import com.offcn.pojo.TbPayLog;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payLog")
public class PayLogController {

    @Reference
    private PayLogService payLogService;

    @RequestMapping("/search")
    public PageResult search(@RequestBody TbPayLog tbPayLog,  int page, int rows ){
        return payLogService.search(tbPayLog, page, rows);
    }
}
