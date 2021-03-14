package com.offcn.service;

import com.offcn.bean.TRole;

import java.util.List;

public interface EmployeeRoleService {

    /**
     * 根据eid获取角色信息
     */
    public List<TRole> getRolesByEid(Integer eid);

    /**
     * 根据eid删除员工角色信息
     */
    boolean deleteRolesByEid(Integer eid);

    /**
     * 根据eid删除员工角色信息（多个）
     */
    boolean deleteRolesByEids(String[] eids);

    /**
     * 新增员工角色
     */
    boolean addEmployeeRoles(Integer eid, String[] rid);
}
