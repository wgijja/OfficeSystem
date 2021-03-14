package com.offcn.bean;

import com.offcn.common.BasePage;

import java.util.List;

public class TRole extends BasePage {
    private Integer rid;

    private String rcode;

    private String rname;

    private List<TFunction> functionList;

    private String remark1;

    private String remark2;

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getRcode() {
        return rcode;
    }

    public void setRcode(String rcode) {
        this.rcode = rcode == null ? null : rcode.trim();
    }

    public String getRname() {
        return rname;
    }

    public void setRname(String rname) {
        this.rname = rname == null ? null : rname.trim();
    }

    public String getRemark1() {
        return remark1;
    }

    public void setRemark1(String remark1) {
        this.remark1 = remark1 == null ? null : remark1.trim();
    }

    public String getRemark2() {
        return remark2;
    }

    public void setRemark2(String remark2) {
        this.remark2 = remark2 == null ? null : remark2.trim();
    }

    public List<TFunction> getFunctionList() {
        return functionList;
    }

    public void setFunctionList(List<TFunction> functionList) {
        this.functionList = functionList;
    }

    @Override
    public String toString() {
        return "TRole{" +
                "rid=" + rid +
                ", rcode='" + rcode + '\'' +
                ", rname='" + rname + '\'' +
                ", functionList=" + functionList +
                ", remark1='" + remark1 + '\'' +
                ", remark2='" + remark2 + '\'' +
                '}';
    }
}