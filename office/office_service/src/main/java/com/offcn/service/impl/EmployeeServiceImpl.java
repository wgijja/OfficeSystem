package com.offcn.service.impl;

import com.offcn.bean.TEmployee;
import com.offcn.bean.TEmployeeExample;
import com.offcn.common.EmployeeQuery;
import com.offcn.common.EmployeeResult;
import com.offcn.dao.TEmployeeMapper;
import com.offcn.service.EmployeeService;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static Logger logger = Logger.getLogger(EmployeeServiceImpl.class);
    @Autowired
    private TEmployeeMapper employeeMapper;

    @Override
    public EmployeeResult loginCheck(String jobNumber, String password) {
        logger.info("登陆校验开始，入参:" + jobNumber);
        EmployeeResult employeeResult = new EmployeeResult();
        try {
            TEmployeeExample employeeExample = new TEmployeeExample();
            TEmployeeExample.Criteria criteria = employeeExample.createCriteria();
            criteria.andJobNumberEqualTo(jobNumber);
            criteria.andPasswordEqualTo(password);
            List<TEmployee> employeeList = employeeMapper.selectByExample(employeeExample);
            if (employeeList != null && employeeList.size() > 0) {
                employeeResult.setLoginSuccess(true);
                employeeResult.setMessage("登陆成功");
                //employeeResult.setEmployee(employeeList.get(0));
            } else {
                employeeResult.setLoginSuccess(false);
                employeeResult.setMessage("用户或密码错误");
            }
            employeeResult.setSuccess(true);
        } catch (Exception e) {
            logger.error(e.getMessage());
            employeeResult.setSuccess(false);
            employeeResult.setMessage("系统繁忙，请稍候再试");
        }
        logger.info("登陆校验结束，出参:" + employeeResult);
        return employeeResult;
    }

    @Override
    public EmployeeResult shiroLogin(String jobNumber, String password) {
        logger.info("登陆校验开始，入参:" + jobNumber);
        EmployeeResult result = new EmployeeResult();//定义结果类来封装结果信息
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(jobNumber, password);
        try {
            subject.login(usernamePasswordToken);//登陆成功与否根据是否抛异常来判断
            result.setSuccess(true);
            result.setLoginSuccess(true);
            result.setMessage("登陆成功");
            //获取shiro的session
            Session session = subject.getSession();
            //将登陆成功的用户存放到session中
            TEmployee employee = (TEmployee) subject.getPrincipal();
            session.setAttribute("employee", employee);
        } catch (Exception e) {
            e.printStackTrace();
            result.setMessage("登陆失败");
            result.setLoginSuccess(false);
            result.setSuccess(false);
        }
        logger.info("登陆校验结束，出参:" + result);
        return result;
    }

    @Override
    public List<TEmployee> selectAllEmployees(EmployeeQuery employeeQuery) {
        logger.info("数据获取开始，入参" + employeeQuery);
        TEmployeeExample employeeExample = new TEmployeeExample();
        TEmployeeExample.Criteria criteria = employeeExample.createCriteria();
        if (!"".equals(employeeQuery.getEname()) && employeeQuery.getEname() != null) {
            criteria.andEnameLike("%" + employeeQuery.getEname() + "%");
        }
        if (!"".equals(employeeQuery.getJobNumber()) && employeeQuery.getJobNumber() != null) {
            criteria.andJobNumberLike("%" + employeeQuery.getJobNumber() + "%");
        }
        if (employeeQuery.getSort() != null) {
            if ("hireDate".equals(employeeQuery.getSort())) {
                employeeQuery.setSort("hire_date");
            }
            employeeExample.setOrderByClause(employeeQuery.getSort() + " " + employeeQuery.getOrder());
        }
        List<TEmployee> employeeList = employeeMapper.selectByExample(employeeExample);
        logger.info("数据获取结束，出参:" + employeeList);
        return employeeList;
    }

    @Override
    public boolean deleteByEids(String[] eids) {
        TEmployeeExample employeeExample = new TEmployeeExample();
        TEmployeeExample.Criteria criteria = employeeExample.createCriteria();
        List<Integer> list = new ArrayList<>();
        for (String s : eids) {
            Integer integer = Integer.valueOf(s);
            list.add(integer);
        }
        criteria.andEidIn(list);
        return employeeMapper.deleteByExample(employeeExample) != 0;
    }

    @Override
    public boolean addEmployee(TEmployee employee) {
        //生成加密盐
        String salt = new SecureRandomNumberGenerator().nextBytes().toHex();
        //密码加密
        String newPassword = new Md5Hash(employee.getPassword(),salt,3).toString();
        employee.setPassword(newPassword);
        //将盐存放在数据库
        employee.setRemark1(salt);
        //新增员工
        int res = employeeMapper.insert(employee);
        return res != 0;
    }

    @Override
    public TEmployee getEmployeeByEid(Integer eid) {
        return employeeMapper.selectByPrimaryKey(eid);
    }

    @Override
    public boolean updateEmployeeByEid(TEmployee employee) {
        logger.info("修改人员开始，入参"+employee);
        int res = employeeMapper.updateByPrimaryKeySelective(employee);
        logger.info("修改人员结束，出参"+res);
        return res != 0;
    }

    @Override
    public boolean deleteEmployeeByEid(String eid) {
        return employeeMapper.deleteByPrimaryKey(Integer.valueOf(eid)) != 0;
    }
}
