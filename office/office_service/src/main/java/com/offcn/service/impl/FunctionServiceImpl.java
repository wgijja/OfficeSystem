package com.offcn.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.offcn.bean.TFunction;
import com.offcn.bean.TFunctionExample;
import com.offcn.bean.TRoleFunctionExample;
import com.offcn.common.BaseResult;
import com.offcn.dao.TFunctionMapper;
import com.offcn.dao.TRoleFunctionMapper;
import com.offcn.service.FunctionService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class FunctionServiceImpl implements FunctionService {

    private static Logger logger = Logger.getLogger(FunctionServiceImpl.class);

    @Autowired
    private TFunctionMapper functionMapper;
    @Autowired
    private TRoleFunctionMapper roleFunctionMapper;

    @Override
    public List<TFunction> getFunctionsByEid(long eid) {
        List<TFunction> functions = functionMapper.getFunctionsByEid(eid);
        return functions;
    }

    @Override
    public JSONArray convert(List<TFunction> functions) {

        //每次循环将对应的一组数据放入到对应的字符串中
        JSONArray jsonArray = new JSONArray();
        //获取当前用户的所有一级功能
        for (TFunction function : functions) {
            if (function.getParentid() == 0) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", function.getFid());
                jsonObject.put("text", function.getFname());
                jsonObject.put("state", "closed");
                //获取一级功能下面的子功能
                JSONArray jsonArrayChildren = new JSONArray();
                for (TFunction function1 : functions) {
                    if (function1.getParentid() == function.getFid()) {
                        JSONObject jsonObject1 = new JSONObject();
                        jsonObject1.put("id", function1.getFid());
                        jsonObject1.put("text", function1.getFname());
                        jsonObject1.put("state", "open");
                        jsonObject1.put("url", function1.getFurl());
                        jsonArrayChildren.add(jsonObject1);
                    }
                }
                jsonObject.put("children", jsonArrayChildren);
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray convert2(List<TFunction> functions, long parentID) {
        JSONArray jsonArray = new JSONArray();
        //遍历获取当前功能下的所有子功能
        for (TFunction f : functions) {
            if (f.getParentid() == parentID) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", f.getFid());
                jsonObject.put("text", f.getFname());
                //remark1表示是否为叶子节点
                if ("YES".equals(f.getRemark1())) {
                    jsonObject.put("state", "open");
                    jsonObject.put("url", f.getFurl());
                } else {
                    JSONArray children = convert2(functions, f.getFid());
                    jsonObject.put("children", children);
                    jsonObject.put("state", "closed");
                }
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray convert2(List<TFunction> allFunctions, List<TFunction> roleFunctions, long parentId) {
        JSONArray jsonArray = new JSONArray();
        //遍历获取当前功能下的所有子功能
        for (TFunction f : allFunctions) {
            if (f.getParentid() == parentId) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", f.getFid());
                jsonObject.put("text", f.getFname());
                //remark1表示是否为叶子节点
                if ("YES".equals(f.getRemark1())) {
                    jsonObject.put("state", "open");
                    jsonObject.put("url", f.getFurl());
                    for (TFunction function :roleFunctions) {
                        if (function.getFid() == f.getFid()){
                            jsonObject.put("checked",true);
                        }
                    }
                } else {
                    JSONArray children = convert2(allFunctions,roleFunctions,f.getFid());
                    jsonObject.put("children", children);
                    jsonObject.put("state", "open");
                }
                jsonArray.add(jsonObject);
            }
        }
        return jsonArray;
    }

    @Override
    public List<TFunction> findFunctionsByCondition(TFunction function) {
        function.setLimitStart((function.getPage() - 1) * function.getRows());
        List<TFunction> functions = functionMapper.findFunctionsByCondition(function);
        return functions;
    }

    @Override
    public Integer countFunctionsByCondition(TFunction function) {
        return functionMapper.countFunctionsByCondition(function);
    }

    @Override
    public List<TFunction> getFirstFunctions() {
        TFunctionExample example = new TFunctionExample();
        TFunctionExample.Criteria criteria = example.createCriteria();
        criteria.andParentidEqualTo(0);
        List<TFunction> list = functionMapper.selectByExample(example);
        return list;
    }

    @Override
    public BaseResult addFunction(TFunction function) {
        logger.info("新增功能权限方法开始，入参：" + function);
        BaseResult result = new BaseResult();
        try {
            //校验功能编码唯一
            TFunctionExample example = new TFunctionExample();
            TFunctionExample.Criteria criteria = example.createCriteria();
            criteria.andFcodeEqualTo(function.getFcode());
            List<TFunction> functions = functionMapper.selectByExample(example);
            if (functions != null && functions.size() > 0) {
                result.setSuccess(false);
                result.setMessage("功能编码重复，请重新输入");
                return result;
            }
            if (function.getFlevel() == 1) {
                //新增一级功能
                function.setParentid(0);
                function.setRemark1("NO");
                function.setRemark2("一级功能");
            } else {
                //新增二级功能
                function.setRemark1("YES");
                //获取二级功能的一级功能名称
                TFunction function1 = functionMapper.selectByPrimaryKey(function.getParentid());
                function.setRemark2(function1.getFname());
            }
            //新增操作
            functionMapper.insert(function);
            result.setMessage("新增成功");
            result.setSuccess(true);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            result.setSuccess(false);
            result.setMessage("新增失败");
        }
        logger.info("新增功能权限结束，出参：" + result);
        return result;
    }

    @Override
    public TFunction findFunctionByFid(long fid) {
        return functionMapper.selectByPrimaryKey((int) fid);
    }

    @Override
    @Transactional
    public BaseResult updateFunction(TFunction function) {
        logger.info("修改功能权限开始，入参:" + function);
        BaseResult result = new BaseResult();
        try {
            //功能编码统一校验
            TFunctionExample example = new TFunctionExample();
            TFunctionExample.Criteria criteria = example.createCriteria();
            criteria.andFcodeEqualTo(function.getFcode());
            criteria.andFidNotEqualTo(function.getFid());
            List<TFunction> functions = functionMapper.selectByExample(example);
            if (functions != null && functions.size() > 0) {
                result.setSuccess(false);
                result.setMessage("功能编码重复！");
                return result;
            }
            if (function.getFlevel() == 1) {
                //修改之后的功能为一级功能
                function.setParentid(0);
                function.setRemark1("NO");
                function.setRemark2("一级功能");
            } else {
                //修改之后的功能为二级功能
                //删除该功能下的子功能
                TFunctionExample example1 = new TFunctionExample();
                TFunctionExample.Criteria criteria1 = example1.createCriteria();
                criteria1.andParentidEqualTo(function.getFid());
                functionMapper.deleteByExample(example1);
                function.setRemark1("YES");
                //获取二级功能的父级功能信息
                TFunction function1 = functionMapper.selectByPrimaryKey(function.getParentid());
                function.setRemark2(function1.getFname());
            }
            //修改功能信息
            functionMapper.updateByPrimaryKey(function);
            //修改该功能的子功能的父级功能名称
            TFunction function1 = new TFunction();
            function1.setRemark2(function.getFname());
            function1.setParentid(function.getFid());
            functionMapper.updateRemark2ByParentId(function1);
            result.setMessage("修改成功");
            result.setSuccess(true);
        } catch (Exception e) {
            //手动调用spring回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("操作失败");
        }
        logger.info("修改功能权限结束，出参:" + result);
        return result;
    }

    @Override
    @Transactional
    public BaseResult deleteFunctionAndRoleFunctionByFid(Integer fid) {
        logger.info("删除功能权限开始，入参："+fid);
        BaseResult result = new BaseResult();
        try{
            //获取该功能的子功能
            TFunctionExample functionExample = new TFunctionExample();
            TFunctionExample.Criteria criteria = functionExample.createCriteria();
            criteria.andParentidEqualTo(fid);
            List<TFunction> children = functionMapper.selectByExample(functionExample);
            if (children != null && children.size() > 0){
                for (TFunction f :children) {
                    //删除角色功能表中的子功能
                    TRoleFunctionExample roleFunctionExample = new TRoleFunctionExample();
                    TRoleFunctionExample.Criteria criteria1 = roleFunctionExample.createCriteria();
                    criteria1.andFidEqualTo(f.getFid());
                    roleFunctionMapper.deleteByExample(roleFunctionExample);
                    //删除功能表子功能
                    functionMapper.deleteByPrimaryKey(f.getFid());
                }
            }
            //删除功能角色表中的fid
            TRoleFunctionExample roleFunctionExample = new TRoleFunctionExample();
            TRoleFunctionExample.Criteria criteria1 = roleFunctionExample.createCriteria();
            criteria1.andFidEqualTo(fid);
            roleFunctionMapper.deleteByExample(roleFunctionExample);
            //删除功能表中的功能
            functionMapper.deleteByPrimaryKey(fid);
            result.setMessage("删除成功");
            result.setSuccess(true);
        }catch (Exception e){
            //手动调用spring回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            e.printStackTrace();
            result.setSuccess(false);
            result.setMessage("删除失败");
        }
        logger.info("删除功能权限结束，出参:"+result);
        return result;
    }

    @Override
    @Transactional
    public BaseResult batchDelete(String fids) {
        logger.info("批量删除开始，入参"+fids);
        BaseResult result = new BaseResult();
        try{
            String[] fidArray = fids.split(",");
            for (String fid :fidArray) {
                //循环调用删除方法
                BaseResult result1 = deleteFunctionAndRoleFunctionByFid(Integer.valueOf(fid));
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
        }
        logger.info("批量删除结束，出参"+result);
        return result;
    }

    @Override
    public List<TFunction> findFunctionsByParentId(long parentid) {
        TFunctionExample example = new TFunctionExample();
        TFunctionExample.Criteria criteria = example.createCriteria();
        criteria.andParentidEqualTo((int) parentid);
        List<TFunction> functions = functionMapper.selectByExample(example);
        return functions;
    }

    @Override
    public List<TFunction> getAllFunctions() {
        TFunctionExample example = new TFunctionExample();
        List<TFunction> functions = functionMapper.selectByExample(example);
        return functions;
    }
}
