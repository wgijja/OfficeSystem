package com.offcn.service.impl;

import com.offcn.bean.TEmployeeRole;
import com.offcn.bean.TEmployeeRoleExample;
import com.offcn.bean.TRole;
import com.offcn.dao.TEmployeeRoleMapper;
import com.offcn.service.EmployeeRoleService;
import com.offcn.service.RoleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeRoleServiceImpl implements EmployeeRoleService {
    private static Logger logger = Logger.getLogger(EmployeeRoleServiceImpl.class);

    @Autowired
    private TEmployeeRoleMapper employeeRoleMapper;
    @Autowired
    private RoleService roleService;

    @Override
    public List<TRole> getRolesByEid(Integer eid) {
        logger.info("获取角色信息开始，入参" + eid);
        TEmployeeRoleExample example = new TEmployeeRoleExample();
        TEmployeeRoleExample.Criteria criteria = example.createCriteria();
        criteria.andEidEqualTo(eid);
        List<TEmployeeRole> list = employeeRoleMapper.selectByExample(example);
        List<TRole> roleList = new ArrayList<>();
        for (TEmployeeRole er : list) {
            roleList.add(roleService.getRoleFunctionsByRid(er.getRid()));
        }
        logger.info("获取角色信息结束，出参" + roleList);
        return roleList;
    }

    @Override
    public boolean deleteRolesByEid(Integer eid) {
        logger.info("删除角色信息开始，入参" + eid);
        TEmployeeRoleExample example = new TEmployeeRoleExample();
        TEmployeeRoleExample.Criteria criteria = example.createCriteria();
        criteria.andEidEqualTo(eid);
        int res = employeeRoleMapper.deleteByExample(example);
        logger.info("删除角色信息结束，出参" + res);
        return res != 0;
    }

    @Override
    public boolean deleteRolesByEids(String[] eids) {
        logger.info("批量删除角色信息开始，入参" + eids);
        TEmployeeRoleExample example = new TEmployeeRoleExample();
        TEmployeeRoleExample.Criteria criteria = example.createCriteria();
        List<Integer> list = new ArrayList<>();
        for (String s : eids) {
            Integer integer = Integer.valueOf(s);
            list.add(integer);
        }
        criteria.andEidIn(list);
        int res = employeeRoleMapper.deleteByExample(example);

        logger.info("批量删除角色信息结束，出参");
        return res != 0;
    }

    @Override
    public boolean addEmployeeRoles(Integer eid, String[] rid) {
        logger.info("新增员工角色信息开始，入参eid=" + eid + "  rid=" + rid);
        List<TEmployeeRole> employeeRoles = new ArrayList<>();
        for (int i = 0; i < rid.length; i++) {
            TEmployeeRole employeeRole = new TEmployeeRole();
            employeeRole.setEid(eid);
            employeeRole.setRid(Integer.valueOf(rid[i]));
            employeeRoles.add(employeeRole);
        }
        int res = 0;
        for (TEmployeeRole er : employeeRoles) {
            res += employeeRoleMapper.insert(er);
        }
        logger.info("新增员工角色信息结束，出参" + res);
        return res != 0;
    }
}
