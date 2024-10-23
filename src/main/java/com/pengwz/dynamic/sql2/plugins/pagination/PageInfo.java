package com.pengwz.dynamic.sql2.plugins.pagination;

import java.util.Collection;
import java.util.function.Supplier;

public class PageInfo<C extends Collection<T>, T> extends AbstractPage {
    //分页结果
    private C records;
    //总分页数量
    private int totalPage;

    public PageInfo(int pageIndex, int pageSize) {
        super(pageIndex, pageSize);
    }

    protected void setRecords(C records) {
        this.records = records;
    }

    public C getRecords() {
        return records;
    }

    public int getRealSize() {
        return records.size();
    }

    /**
     * 是否有上一页
     */
    public boolean hasPreviousPage() {
        return getPageIndex() > 1;
    }

    /**
     * 是否有下一页
     */
    public boolean hasNextPage() {
        return getPageIndex() < totalPage;
    }


    public PageInfo<C, T> selectNextPage(Supplier<C> selectSupplier) {
        return PageHelper.of(getPageIndex() + 1, getPageSize()).selectPageInfo(selectSupplier);
    }
}
