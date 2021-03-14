package com.offcn.service;

import com.offcn.bean.TEmployee;
import com.offcn.common.EmployeeQuery;
import com.offcn.common.EmployeeResult;

import java.util.List;

public interface EmployeeService {

    /**
     * 自己写的登陆校验
     *
     * @param jobNumber 工号
     * @param password  密码
     */
    public EmployeeResult loginCheck(String jobNumber, String password);

    /**
     * 使用shiro的登陆校验
     */
    public EmployeeResult shiroLogin(String jobNumber, String password);

    /**
     * 获取所有的员工
     *
     * @return
     */
    public List<TEmployee> selectAllEmployees(EmployeeQuery employeeQuery);

    /**
     * 根据eid删除员工
     */
    public boolean deleteByEids(String[] eids);

    /**
     * 新增员工
     */
    public boolean addEmployee(TEmployee employee);

    /**
     * 根据eid查询员工信息
     */
    public TEmployee getEmployeeByEid(Integer eid);

    /**
     * 根据eid修改员工信息
     */
    public boolean updateEmployeeByEid(TEmployee employee);

    /**
     * 根据eid删除员工
     */
    public boolean deleteEmployeeByEid(String eid);
}
