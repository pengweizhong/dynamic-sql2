package com.pengwz.dynamic.sql2.core.database;

import java.util.List;

public class PreparedSql {
    private String sql;
    private List<Object> params;

    public PreparedSql(String sql, List<Object> params) {
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
