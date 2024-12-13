package com.dynamic.sql.core.dml.select.build;

public class LimitInfo {
    private Integer offset;
    private int limit;

    public LimitInfo(Integer offset, int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public int getLimit() {
        return limit;
    }
}
