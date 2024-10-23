package com.pengwz.dynamic.sql2.plugins.pagination;

import java.util.Collection;

public class PageInfo<T> extends AbstractPage {
    private Collection<T> records;

    public PageInfo(int pageIndex, int pageSize) {
        super(pageIndex, pageSize);
    }

    public Collection<T> getRecords() {
        return records;
    }

    protected void setRecords(Collection<T> records) {
        this.records = records;
    }
}
