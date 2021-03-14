package com.offcn.controller;

import com.offcn.bean.TRole;
import com.offcn.service.EmployeeRoleService;
import com.offcn.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/employeeRole")
public class EmployeeRoleController {

    @Autowired
    private EmployeeRoleService employeeRoleService;

    @RequestMapping("/getRolesByEid")
    @ResponseBody
    public Object getRolesByEid(String eid){
        List<TRole> roles = employeeRoleService.getRolesByEid(Integer.valueOf(eid));
        return roles;
    }

    @RequestMapping("/deleteRolesByEid")
    @ResponseBody
    public Object deleteRolesByEid(String eid){
        boolean res = employeeRoleService.deleteRolesByEid(Integer.valueOf(eid));
        return res;
    }
}
