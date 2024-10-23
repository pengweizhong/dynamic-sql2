package com.pengwz.dynamic.sql2.plugins.pagination;

public abstract class AbstractPage {
    private final int pageIndex;
    private final int pageSize;
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
