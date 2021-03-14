package com.offcn.controller;

import com.alibaba.fastjson.JSONArray;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.bean.TFunction;
import com.offcn.bean.TRole;
import com.offcn.common.BasePage;
import com.offcn.common.BaseResult;
import com.offcn.common.RoleQuery;
import com.offcn.service.FunctionService;
import com.offcn.service.RoleService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/role")
@Controller
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private FunctionService functionService;

    /**
     * 获取所有的角色
     */
    @RequestMapping("/getAllRoles")
    @ResponseBody
    public Object getAllRoles(RoleQuery roleQuery) {
        Page<Object> pageInfo = PageHelper.startPage(roleQuery.getPage(), roleQuery.getRows());
        List<TRole> roleList = roleService.getAllRoles(roleQuery);
        long total = pageInfo.getTotal();
        Map<String, Object> map = new HashMap<>();
        map.put("rows", roleList);
        map.put("total", total);
        return map;
    }

    /**
     * 获取所有的角色2
     */
    @RequestMapping("/getAllRoles2")
    @ResponseBody
    public Object getAllRoles2(RoleQuery roleQuery) {
        List<TRole> roleList = roleService.getAllRoles(roleQuery);
        return roleList;
    }

    /**
     * 获根据rid获取角色功能信息，并存入session中
     */
    @RequestMapping("/getRoleFunctionsByRid")
    @ResponseBody
    public Object getRoleFunctionsByRid(long rid) {
        TRole role = roleService.getRoleFunctionsByRid(rid);
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("upDateRoleFunction",role);
        return role;
    }

    /**
     * 获取角色功能信息
     */
    @RequestMapping("/getRoleFunctions")
    @ResponseBody
    public JSONArray getRoleFunctions(){
        //获取角色功能
        Session session = SecurityUtils.getSubject().getSession();
        TRole role = (TRole) session.getAttribute("upDateRoleFunction");
        List<TFunction> roleFunctions = role.getFunctionList();
        //获取系统所有功能
        List<TFunction> allFunctions = functionService.getAllFunctions();
        JSONArray jsonArray = functionService.convert2(allFunctions,roleFunctions,0L);
        return jsonArray;
    }

    /**
     * 根据rids批量删除角色
     */
    @RequestMapping("/deleteByRids")
    @ResponseBody
    public BaseResult deleteByRids(String rids) {
        BaseResult result = roleService.deleteByRids(rids);
        return result;
    }

    /**
     * 新增角色
     */
    @RequestMapping("/addRole")
    @ResponseBody
    public BaseResult addRole(TRole role) {
        BaseResult result = roleService.addEmployee(role);
        return result;
    }

    /**
     * 修改角色功能信息
     */
    @RequestMapping("/updateRole")
    @ResponseBody
    public BaseResult updateRole(TRole role){
        BaseResult result = roleService.updateRole(role);
        return result;
    }

    /**
     * 根据rid删除角色信息
     */
    @RequestMapping("/deleteRoleByRid")
    @ResponseBody
    public BaseResult deleteRoleByRid(long rid){
        BaseResult result = roleService.deleteRoleByRid(rid);
        return result;
    }
}
