package com.pengwz.dynamic.sql2.plugins.pagination;

import java.util.function.Supplier;

public abstract class AbstractPage {
    //当前页码
    protected int pageIndex;
    //当前页数量
    protected final int pageSize;
    //总数量
    protected Long total;
    //总分页数量
    protected int totalPage;

    protected AbstractPage(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    abstract void setRecords(Supplier<?> selectSupplier);

    public abstract boolean hasPreviousPage();

    public abstract boolean hasNextPage();

    public long getTotal() {
        if (total == null) {
            return 0;
        }
        return total;
    }

    public int getTotalPage() {
        return totalPage;
    }

    protected void setTotal(long total) {
        this.total = total;
    }

    protected void initTotalPage() {
        long pages = (total + pageSize - 1L) / pageSize;
        totalPage = (int) pages;
    }

    protected Long getCacheTotal() {
        return total;
    }

}
