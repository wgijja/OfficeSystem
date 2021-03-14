package com.offcn.dao;

import com.offcn.bean.TFunction;
import com.offcn.bean.TFunctionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TFunctionMapper {
    long countByExample(TFunctionExample example);

    int deleteByExample(TFunctionExample example);

    int deleteByPrimaryKey(Integer fid);

    int insert(TFunction record);

    int insertSelective(TFunction record);

    List<TFunction> selectByExample(TFunctionExample example);

    TFunction selectByPrimaryKey(Integer fid);

    int updateByExampleSelective(@Param("record") TFunction record, @Param("example") TFunctionExample example);

    int updateByExample(@Param("record") TFunction record, @Param("example") TFunctionExample example);

    int updateByPrimaryKeySelective(TFunction record);

    int updateByPrimaryKey(TFunction record);

    /**
     * 根据员工ID获取员工的功能权限
     */
    List<TFunction> getFunctionsByEid(long eid);

    /**
     * 根据条件分页获取权限列表数据
     */
    public List<TFunction> findFunctionsByCondition(TFunction function);

    /**
     * 根据条件获取权限列表数据总条数
     */
    public Integer countFunctionsByCondition(TFunction function);

    /**
     * 根据父级功能ID修改子功能父级功能名称
     */
    public void updateRemark2ByParentId(TFunction function);

}