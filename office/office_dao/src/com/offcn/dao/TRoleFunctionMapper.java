package com.offcn.dao;

import com.offcn.bean.TRoleFunction;
import com.offcn.bean.TRoleFunctionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TRoleFunctionMapper {
    long countByExample(TRoleFunctionExample example);

    int deleteByExample(TRoleFunctionExample example);

    int deleteByPrimaryKey(Integer rfid);

    int insert(TRoleFunction record);

    int insertSelective(TRoleFunction record);

    List<TRoleFunction> selectByExample(TRoleFunctionExample example);

    TRoleFunction selectByPrimaryKey(Integer rfid);

    int updateByExampleSelective(@Param("record") TRoleFunction record, @Param("example") TRoleFunctionExample example);

    int updateByExample(@Param("record") TRoleFunction record, @Param("example") TRoleFunctionExample example);

    int updateByPrimaryKeySelective(TRoleFunction record);

    int updateByPrimaryKey(TRoleFunction record);
}