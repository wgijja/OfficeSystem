package com.offcn.service;

import com.alibaba.fastjson.JSONArray;
import com.offcn.bean.TEmployee;
import com.offcn.bean.TFunction;
import com.offcn.bean.TRole;
import com.offcn.common.BaseResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 功能权限表服务
 */
public interface FunctionService {
    /**
     * 根据员工ID获取员工的功能权限
     */
    public List<TFunction> getFunctionsByEid(long eid);

    /**
     * 将功能列表转换为easyUI识别的菜单格式json字符串(只支持两级)
     */
    @Deprecated
    public JSONArray convert(List<TFunction> functions);

    /**
     * 将功能列表转换为easyUI识别的菜单格式json字符串（支持多级）
     */
    public JSONArray convert2(List<TFunction> functions,long parentId);

    /**
     * 将功能列表转换为easyUI识别的菜单格式json字符串
     */
    public JSONArray convert2(List<TFunction> allFunctions, List<TFunction> roleFunctions, long parentId);

    /**
     * 根据条件分页获取权限列表数据
     */
    public List<TFunction> findFunctionsByCondition(TFunction function);

    /**
     * 根据条件统计权限列表数据总条数
     */
    public Integer countFunctionsByCondition(TFunction function);

    /**
     * 获取所有一级功能
     */
    public List<TFunction> getFirstFunctions();

    /**
     * 新增功能权限
     */
    public BaseResult addFunction(TFunction function);

    /**
     * 根据fid获取功能信息
     */
    public TFunction findFunctionByFid(long fid);

    /**
     * 修改功能权限
     */
    public BaseResult updateFunction(TFunction function);

    /**
     * 根据fid,或parentID等于fid来删除功能
     */
    public BaseResult deleteFunctionAndRoleFunctionByFid(Integer fid);

    /**
     * 批量删除
     */
    public BaseResult batchDelete(String fids);

    /**
     * 根据父功能id获取子功能列表
     */
    public List<TFunction> findFunctionsByParentId(long parentid);

    /**
     * 获取系统所有功能
     */
    public List<TFunction> getAllFunctions();
}
