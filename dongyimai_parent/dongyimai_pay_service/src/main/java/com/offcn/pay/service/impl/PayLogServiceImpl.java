package com.offcn.pay.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.entity.PageResult;
import com.offcn.mapper.TbPayLogMapper;
import com.offcn.pay.service.PayLogService;
import com.offcn.pojo.TbPayLog;
import com.offcn.pojo.TbPayLogExample;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class PayLogServiceImpl implements PayLogService {

    @Autowired
    private TbPayLogMapper payLogMapper;
    /**
     * 分页
     *
     * @param tbPayLog
     * @param page  当前页码
     * @param rows 每页记录数
     * @return
     */
    @Override
    public PageResult search(TbPayLog tbPayLog,  int page, int rows) {
        PageHelper.startPage(page, rows);

        TbPayLogExample tbPayLogExample = new TbPayLogExample();
        TbPayLogExample.Criteria criteria = tbPayLogExample.createCriteria();

        if (tbPayLog != null) {
            //暂用createTime为开始时间
            if (tbPayLog.getCreateTime() != null) {
                criteria.andCreateTimeGreaterThanOrEqualTo(tbPayLog.getCreateTime());
            }
            //暂用payTime为结束时间
            if (tbPayLog.getPayTime() != null) {
                criteria.andPayTimeLessThan(tbPayLog.getPayTime());
            }
            if (tbPayLog.getTradeState() != null) {
                criteria.andTradeStateEqualTo(tbPayLog.getTradeState());
            }
            if (tbPayLog.getUserId() != null) {
                criteria.andUserIdLike("%" + tbPayLog.getUserId() + "%");
            }
        }
        tbPayLogExample.setOrderByClause("create_time desc");

        Page<TbPayLog> pageResult = (Page<TbPayLog>) payLogMapper.selectByExample(tbPayLogExample);
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }
}
