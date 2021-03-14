package com.offcn.common;

public class BasePage {
    /**
     * 表示当前页码
     */
    private Integer page;
    /**
     * 表示当前页条数
     */
    private Integer rows;
    /**
     * 查询数据的起始位置
     */
    private Integer limitStart;

    /**
     * 排序条件
     */
    private String sort;
    /**
     * 排序方式
     */
    private String order;

    /**
     * 功能字符串
     */
    private String fids;

    public String getFids() {
        return fids;
    }

    public void setFids(String fids) {
        this.fids = fids;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Integer getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(Integer limitStart) {
        this.limitStart = limitStart;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
