package com.offcn.dao;

import com.offcn.bean.TEmployee;
import com.offcn.bean.TEmployeeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TEmployeeMapper {
    long countByExample(TEmployeeExample example);

    int deleteByExample(TEmployeeExample example);

    int deleteByPrimaryKey(Integer eid);

    int insert(TEmployee record);

    int insertSelective(TEmployee record);

    List<TEmployee> selectByExample(TEmployeeExample example);

    TEmployee selectByPrimaryKey(Integer eid);

    int updateByExampleSelective(@Param("record") TEmployee record, @Param("example") TEmployeeExample example);

    int updateByExample(@Param("record") TEmployee record, @Param("example") TEmployeeExample example);

    int updateByPrimaryKeySelective(TEmployee record);

    int updateByPrimaryKey(TEmployee record);
}