package com.offcn.service.impl;

import com.offcn.bean.*;
import com.offcn.common.BaseResult;
import com.offcn.common.RoleQuery;
import com.offcn.dao.TEmployeeRoleMapper;
import com.offcn.dao.TRoleFunctionMapper;
import com.offcn.dao.TRoleMapper;
import com.offcn.service.RoleService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    private static Logger logger = Logger.getLogger(RoleServiceImpl.class);

    @Autowired
    private TRoleMapper roleMapper;
    @Autowired
    private TRoleFunctionMapper roleFunctionMapper;
    @Autowired
    private TEmployeeRoleMapper employeeRoleMapper;

    @Override
    public List<TRole> getAllRoles(RoleQuery roleQuery) {
        logger.info("数据获取开始，入参" + roleQuery);
        TRoleExample example = new TRoleExample();
        TRoleExample.Criteria criteria = example.createCriteria();
        if (!"".equals(roleQuery.getRname()) && roleQuery.getRname() != null) {
            criteria.andRnameLike("%" + roleQuery.getRname() + "%");
        }
        List<TRole> roleList = roleMapper.selectByExample(example);
        logger.info("数据获取结束，出参:" + roleList);
        return roleList;
    }

    @Override
    @Transactional
    public BaseResult deleteByRids(String rids) {
        logger.info("批量删除开始，入参" + rids);
        BaseResult result = new BaseResult();
        try{
            String[] ridArray = rids.split(",");
            for (String rid :ridArray) {
                //循环调用删除方法
                BaseResult result1 = deleteRoleByRid(Long.parseLong(rid));
                if (!result1.getSuccess()) {
                    throw new Exception();
                }
            }
            result.setMessage("删除成功");
            result.setSuccess(true);
        }catch (Exception e){
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("删除失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        logger.info("批量删除结束，出参" + result);
        return result;
    }

    @Override
    @Transactional
    public BaseResult addEmployee(TRole role) {
        logger.info("角色新增开始，入参" + role);
        BaseResult result = new BaseResult();
        //校验编码唯一
        TRoleExample roleExample = new TRoleExample();
        TRoleExample.Criteria criteria = roleExample.createCriteria();
        criteria.andRcodeEqualTo(role.getRcode());
        List<TRole> roleList = roleMapper.selectByExample(roleExample);
        if (roleList != null && roleList.size() > 0) {
            result.setSuccess(false);
            result.setMessage("编码重复，操作失败");
            return result;
        }
        try {
            //新增角色表
            roleMapper.insert(role);
            //新增角色功能关系表
            String[] fidArray = role.getFids().split(",");
            for (String fid : fidArray) {
                TRoleFunction roleFunction = new TRoleFunction();
                roleFunction.setFid(Integer.valueOf(fid));
                roleFunction.setRid(role.getRid());
                roleFunctionMapper.insert(roleFunction);
            }
            result.setSuccess(true);
            result.setMessage("新增成功");
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            result.setMessage("新增失败");
            result.setSuccess(false);
        }
        logger.info("角色新增结束，出参" + result);
        return result;
    }

    @Override
    public TRole getRoleFunctionsByRid(long rid) {
        TRole role = roleFunctionMapper.getRoleFunctionsByRid(rid);
        return role;
    }

    @Override
    @Transactional
    public BaseResult updateRole(TRole role) {
        logger.info("修改角色功能开始，入参" + role);
        BaseResult result = new BaseResult();
        //角色编码唯一校验
        TRoleExample example = new TRoleExample();
        TRoleExample.Criteria criteria = example.createCriteria();
        criteria.andRcodeEqualTo(role.getRcode());
        criteria.andRidNotEqualTo(role.getRid());
        List<TRole> roleList = roleMapper.selectByExample(example);
        if (roleList != null && roleList.size() > 0) {
            result.setSuccess(false);
            result.setMessage("角色编码重复，操作失败");
            return result;
        }
        try {
            //修改角色表基本信息
            roleMapper.updateByPrimaryKey(role);
            //删除以前的角色功能关系表数据
            TRoleFunctionExample roleFunctionExample = new TRoleFunctionExample();
            TRoleFunctionExample.Criteria criteria2 = roleFunctionExample.createCriteria();
            criteria2.andRidEqualTo(role.getRid());
            roleFunctionMapper.deleteByExample(roleFunctionExample);
            //新增角色关系表
            String[] fids = role.getFids().split(",");
            for (String fid : fids) {
                TRoleFunction roleFunction = new TRoleFunction();
                roleFunction.setRid(role.getRid());
                roleFunction.setFid(Integer.valueOf(fid));
                roleFunctionMapper.insert(roleFunction);
            }
            result.setSuccess(true);
            result.setMessage("修改成功！");
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("修改失败，请稍候再试");
            result.setSuccess(false);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        logger.info("修改角色功能结束，出参" + result);
        return result;
    }

    @Override
    @Transactional
    public BaseResult deleteRoleByRid(long rid) {
        logger.info("删除角色信息开始，入参" + rid);
        BaseResult result = new BaseResult();
        try {
            //删除角色功能关系表数据
            TRoleFunctionExample roleFunctionExample = new TRoleFunctionExample();
            TRoleFunctionExample.Criteria criteria = roleFunctionExample.createCriteria();
            criteria.andRidEqualTo((int) rid);
            roleFunctionMapper.deleteByExample(roleFunctionExample);
            //删除角色员工关系表数据
            TEmployeeRoleExample employeeRoleExample = new TEmployeeRoleExample();
            TEmployeeRoleExample.Criteria criteria1 = employeeRoleExample.createCriteria();
            criteria1.andRidEqualTo((int) rid);
            employeeRoleMapper.deleteByExample(employeeRoleExample);
            //删除角色表数据
            roleMapper.deleteByPrimaryKey((int) rid);
            result.setMessage("删除成功");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("删除失败");
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        logger.info("删除角色信息结束，出参" + result);
        return result;
    }
}
