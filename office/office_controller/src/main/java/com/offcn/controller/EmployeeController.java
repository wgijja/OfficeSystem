package com.offcn.controller;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.offcn.bean.TEmployee;
import com.offcn.common.EmployeeQuery;
import com.offcn.common.EmployeeResult;
import com.offcn.service.EmployeeRoleService;
import com.offcn.service.EmployeeService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRoleService employeeRoleService;

    /**
     * 登陆校验
     */
    @RequestMapping("/loginCheck")
    @ResponseBody
    public EmployeeResult loginCheck(String jobNumber, String password, HttpSession session) {
        EmployeeResult result = employeeService.shiroLogin(jobNumber, password);
        return result;
    }

    /**
     * 获取用户信息
     */
    @RequestMapping("/userInfo")
    @ResponseBody
    public TEmployee userInfo(HttpSession session) {
        TEmployee employee = (TEmployee) session.getAttribute("employee");
        return employee;
    }

    /**
     * 退出登陆
     */
    @RequestMapping("/logout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        subject.logout();
        return "redirect:/index.html";
    }

    /**
     * 查询所有员工数据
     *
     * @return 所有员工的json字符串
     */
    @RequestMapping("/selectAllEmployees")
    @ResponseBody
    public Object selectAllEmployees(EmployeeQuery employeeQuery) {
        Page<Object> pageInfo = PageHelper.startPage(employeeQuery.getPage(), employeeQuery.getRows());
        List<TEmployee> list = employeeService.selectAllEmployees(employeeQuery);
        long total = pageInfo.getTotal();
        Map<String, Object> map = new HashMap<>();
        map.put("rows", list);
        map.put("total", total);
        return map;
    }

    /**
     * 根据eid批量删除员工,员工角色
     */
    @RequestMapping("/deleteByEids")
    @ResponseBody
    public Object deleteByEids(String[] eid) {
        boolean res1 = employeeService.deleteByEids(eid);
        boolean res2 = employeeRoleService.deleteRolesByEids(eid);
        Map<String, String> map = new HashMap<>();
        if (res1 && res2) {
            map.put("code", "200");
            map.put("msg", "删除成功");
        } else {
            map.put("code", "777");
            map.put("msg", "删除失败，请稍候再试");
        }
        return map;
    }

    /**
     * 新增员工
     */
    @RequestMapping("/addEmployee")
    @ResponseBody
    public Object addEmployee(TEmployee employee, String[] rid) {
        boolean res1 = employeeService.addEmployee(employee);
        boolean res2 = employeeRoleService.addEmployeeRoles(employee.getEid(), rid);
        Map<String, String> map = new HashMap<>();
        if (res1 && res2) {
            map.put("code", "200");
            map.put("msg", "新增成功");
        } else {
            map.put("code", "777");
            map.put("msg", "新增失败，请稍候再试");
        }
        return map;
    }

    /**
     * 根据eid查询员工信息
     */
    @RequestMapping("/getEmployeeByEid")
    @ResponseBody
    public TEmployee getEmployeeByEid(Integer eid) {
        TEmployee employee = employeeService.getEmployeeByEid(eid);
        return employee;
    }

    /**
     * 根据eid修改员工信息
     */
    @RequestMapping("/updateEmployeeByEid")
    @ResponseBody
    public Object updateEmployeeByEid(TEmployee employee, String[] rid) {
        boolean res1 = employeeService.updateEmployeeByEid(employee);
        boolean res3 = true;
        if (rid != null && !"".equals(rid)){
            employeeRoleService.deleteRolesByEid(employee.getEid());
            res3 = employeeRoleService.addEmployeeRoles(employee.getEid(), rid);
        }else {
            employeeRoleService.deleteRolesByEid(employee.getEid());
        }
        Map<String, String> map = new HashMap<>();
        if (res1 && res3) {
            map.put("code", "200");
            map.put("msg", "修改成功");
        } else {
            map.put("code", "777");
            map.put("msg", "修改失败，请稍候再试");
        }
        return map;
    }

    /**
     * 根据eid删除员工
     */
    @RequestMapping("/deleteEmployeeByEid")
    @ResponseBody
    public Object deleteEmployeeByEid(String eid) {
        boolean res1 = employeeService.deleteEmployeeByEid(eid);
        boolean res2 = employeeRoleService.deleteRolesByEid(Integer.valueOf(eid));
        Map<String, String> map = new HashMap<>();
        if (res1 && res2) {
            map.put("code", "200");
            map.put("msg", "删除成功");
        } else {
            map.put("code", "777");
            map.put("msg", "删除失败，请稍候再试");
        }
        return map;
    }

    /**
     * 导出数据
     * @return
     */
    @RequestMapping("/export")
    public ResponseEntity<byte[]> export(EmployeeQuery employeeQuery) throws IOException {
        String name = "employees.xls";
        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row title = sheet.createRow(0);
        title.createCell(0).setCellValue("员工id");
        title.createCell(1).setCellValue("员工姓名");
        title.createCell(2).setCellValue("员工性别");
        title.createCell(3).setCellValue("员工年龄");
        title.createCell(4).setCellValue("联系电话");
        title.createCell(5).setCellValue("入职日期");
        employeeQuery.setPage(1);
        employeeQuery.setRows(999999);
        List<TEmployee> employeeList = employeeService.selectAllEmployees(employeeQuery);
        for (TEmployee employee : employeeList){
            Row nextRow = sheet.createRow(sheet.getLastRowNum()+1);
            nextRow.createCell(0).setCellValue(employee.getEid());
            nextRow.createCell(1).setCellValue(employee.getEname());
            nextRow.createCell(2).setCellValue(employee.getEsex()==0?"女":"男");
            nextRow.createCell(3).setCellValue(employee.getEage());
            nextRow.createCell(4).setCellValue(employee.getEtelephone());
            nextRow.createCell(5).setCellValue(new SimpleDateFormat("yyyy-MM-dd").format(employee.getHireDate()));
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        byte[] bytes = outputStream.toByteArray();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentDispositionFormData("attachment",name);
        return new ResponseEntity<>(bytes,httpHeaders, HttpStatus.OK);
    }
}