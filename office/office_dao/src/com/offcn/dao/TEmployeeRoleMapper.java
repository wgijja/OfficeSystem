package com.offcn.dao;

import com.offcn.bean.TEmployeeRole;
import com.offcn.bean.TEmployeeRoleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TEmployeeRoleMapper {
    long countByExample(TEmployeeRoleExample example);

    int deleteByExample(TEmployeeRoleExample example);

    int deleteByPrimaryKey(Integer erid);

    int insert(TEmployeeRole record);

    int insertSelective(TEmployeeRole record);

    List<TEmployeeRole> selectByExample(TEmployeeRoleExample example);

    TEmployeeRole selectByPrimaryKey(Integer erid);

    int updateByExampleSelective(@Param("record") TEmployeeRole record, @Param("example") TEmployeeRoleExample example);

    int updateByExample(@Param("record") TEmployeeRole record, @Param("example") TEmployeeRoleExample example);

    int updateByPrimaryKeySelective(TEmployeeRole record);

    int updateByPrimaryKey(TEmployeeRole record);
}