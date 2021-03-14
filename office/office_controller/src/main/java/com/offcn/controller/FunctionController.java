package com.offcn.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.offcn.bean.TEmployee;
import com.offcn.bean.TFunction;
import com.offcn.common.BaseResult;
import com.offcn.service.FunctionService;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("function")
public class FunctionController {

    @Autowired
    private FunctionService functionService;

    @RequestMapping("getCurrentEmployeeFunctions")
    @ResponseBody//返回任意对象类型
    public JSONArray getCurrentEmployeeFunctions(HttpSession session){
        //获取当前登陆用户
        TEmployee employee = (TEmployee) session.getAttribute("employee");
        if (employee != null){
            //获取当前用户的功能信息
            List<TFunction> functions =  functionService.getFunctionsByEid(employee.getEid());
            JSONArray jsonArray = functionService.convert2(functions,0L);
            return jsonArray;
        }
        return null;
    }

    /**
     * 分页查询
     */
    @RequestMapping("/getFunctionList")
    @ResponseBody
    public JSONObject findFunctionsByCondition(TFunction function){
        //获取分页列表数据
        List<TFunction> functions = functionService.findFunctionsByCondition(function);
        //获取数据总条数
        Integer total = functionService.countFunctionsByCondition(function);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows",functions);
        jsonObject.put("total",total);
        return jsonObject;
    }

    /**
     * 获取系统所有一级功能
     */
    @RequestMapping("/getFirstFunctions")
    @ResponseBody
    public List<TFunction> getFirstFunctions(){
        List<TFunction> firstFunctions = functionService.getFirstFunctions();
        return firstFunctions;
    }

    /**
     * 新增功能
     */
    @RequestMapping("/addFunction")
    @ResponseBody
    public BaseResult addFunction(TFunction function){
        BaseResult result = functionService.addFunction(function);
        return result;
    }

    /**
     * 根据fid获取功能信息
     */
    @RequestMapping("/getFunctionByFid")
    @ResponseBody
    public TFunction getFunctionByFid(long fid){
        TFunction function = functionService.findFunctionByFid(fid);
        return function;
    }

    /**
     * 根据fid获取功能信息并存放到session中
     */
    @RequestMapping("/getFunctionByFidToSession")
    @ResponseBody
    public BaseResult getFunctionByFidToSession(long fid){
        BaseResult result = new BaseResult();
        TFunction function = functionService.findFunctionByFid(fid);
        Session session = SecurityUtils.getSubject().getSession();
        session.setAttribute("function",function);
        result.setSuccess(true);
        return result;
    }

    /**
     * 从session中获取需要修改的功能
     */
    @RequestMapping("/getFunctionFromSession")
    @ResponseBody
    public TFunction getFunctionFromSession(){
        Session session = SecurityUtils.getSubject().getSession();
        TFunction function = (TFunction) session.getAttribute("function");
        return function;
    }

    /**
     * 修改功能权限
     */
    @RequestMapping("/updateFunction")
    @ResponseBody
    public BaseResult updateFunction(TFunction function){
        BaseResult result = functionService.updateFunction(function);
        return result;
    }

    /**
     * 根据fid,或parentID等于fid来删除功能
     */
    @RequestMapping("/deleteFunctionAndRoleFunctionByFid")
    @ResponseBody
    public BaseResult deleteFunctionAndRoleFunctionByFid(Integer fid){
        BaseResult result = functionService.deleteFunctionAndRoleFunctionByFid(fid);
        return result;
    }

    /**
     * 批量删除
     * @return
     */
    @RequestMapping("/batchDelete")
    @ResponseBody
    public BaseResult batchDelete(String fids){
        BaseResult result = functionService.batchDelete(fids);
        return result;
    }

    /**
     * 根据父功能id获取子功能列表
     */
    @RequestMapping("/findFunctionsByParentId")
    @ResponseBody
    public List<TFunction> findFunctionsByParentId(long parentid){
        return functionService.findFunctionsByParentId(parentid);
    }
}
