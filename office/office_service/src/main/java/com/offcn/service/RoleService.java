package com.offcn.service;

import com.offcn.bean.TRole;
import com.offcn.common.BaseResult;
import com.offcn.common.RoleQuery;

import java.util.List;

public interface RoleService {

    /**
     * 获取所有角色
     */
    public List<TRole> getAllRoles(RoleQuery roleQuery);

    /**
     * 根据rid删除角色
     */
    public BaseResult deleteByRids(String rids);

    /**
     * 新增角色
     */
    BaseResult addEmployee(TRole role);

    /**
     * 获根据rid获取角色功能信息，并存入session中
     */
    TRole getRoleFunctionsByRid(long rid);

    /**
     * 修改角色功能信息
     */
    BaseResult updateRole(TRole role);

    /**
     * 根据rid删除角色信息
     */
    BaseResult deleteRoleByRid(long rid);
}
