package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.core.dml.select.Select;

public class SqlContext {

    private SqlContext() {
    }

    //TODO 回头把扫描包配置作为参数传进来
    public static SqlContext createSqlContext() {
        return new SqlContext();
    }

    public Select select() {
        return new Select();
    }


}
