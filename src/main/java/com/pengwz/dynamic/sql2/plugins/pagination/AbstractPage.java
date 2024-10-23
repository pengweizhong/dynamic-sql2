package com.pengwz.dynamic.sql2.plugins.pagination;

public abstract class AbstractPage {
    //当前页码
    private final int pageIndex;
    //当前页数量
    private final int pageSize;
    //总数量
    private long total;

    public AbstractPage(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotal() {
        return total;
    }

    protected void setTotal(long total) {
        this.total = total;
    }
}
