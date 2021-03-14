package com.offcn.realm;

import com.offcn.bean.TEmployee;
import com.offcn.bean.TEmployeeExample;
import com.offcn.bean.TFunction;
import com.offcn.bean.TRole;
import com.offcn.dao.TEmployeeMapper;
import com.offcn.dao.TFunctionMapper;
import com.offcn.dao.TRoleMapper;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EmployeeRealm extends AuthorizingRealm {

    @Autowired
    private TEmployeeMapper employeeMapper;
    @Autowired
    private TRoleMapper roleMapper;
    @Autowired
    private TFunctionMapper functionMapper;

    /**
     * 获取数据库授权信息
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        //获取当前登陆用户
        TEmployee employee = (TEmployee) principalCollection.getPrimaryPrincipal();
        //获取当前用户的角色信息
        List<TRole> roleList = roleMapper.findRolesByEid(employee.getEid());
        //将角色信息封装到AuthorizationInfo中
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        for (TRole role : roleList){
            simpleAuthorizationInfo.addRole(role.getRcode());
        }

        //获取当前功能信息
        List<TFunction> functionList = functionMapper.getFunctionsByEid(employee.getEid());
        //将功能信息封装到AuthorizationInfo中
        for (TFunction f : functionList){
            simpleAuthorizationInfo.addStringPermission(f.getFcode());
        }

        return simpleAuthorizationInfo;
    }

    /**
     * 获取数据库认证信息
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        //获取数据库要校验的用户信息
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        String jobNumber = token.getUsername();
        //根据用户名获取用户信息
        TEmployeeExample employeeExample = new TEmployeeExample();
        TEmployeeExample.Criteria criteria = employeeExample.createCriteria();
        criteria.andJobNumberEqualTo(jobNumber);
        List<TEmployee> employeeList = employeeMapper.selectByExample(employeeExample);
        if (employeeList != null && employeeList.size() > 0) {
            TEmployee employee = employeeList.get(0);
            //将查询出来的数据封装到authenticationInfo中返回给调用者
            SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(employee, employee.getPassword(), token.getUsername());
            //设置加密盐
            simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(employee.getRemark1()));
            return simpleAuthenticationInfo;
        }
        return null;
    }
}
