package com.pengwz.dynamic.sql2.core.database;

import java.util.List;

public class PreparedObject {
    private String sql;
    private List<Object> params;

    public PreparedObject(String sql, List<Object> params) {
        this.sql = sql;
        this.params = params;
    }

    public String getSql() {
        return sql;
    }

    public List<Object> getParams() {
        return params;
    }
}
