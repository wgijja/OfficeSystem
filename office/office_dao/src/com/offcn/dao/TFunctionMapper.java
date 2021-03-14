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
}