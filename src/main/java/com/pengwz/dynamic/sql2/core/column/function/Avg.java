package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.Fn;

public class Avg extends AbstractFuncColumn {
    public Avg(String columnName) {
        super(columnName);
    }

    public <T, F> Avg(Fn<T, F> fn) {
        super(fn);
    }

    public Avg(AbstractFuncColumn abstractFuncColumn) {
        super(abstractFuncColumn);
    }

    @Override
    public String getFuncToString() {
        return "";
    }
//    public Avg(String columnName) {
//        super(columnName);
//    }
//
//    @Override
//    String getFuncToString() {
//        return "avg(" + getColumnName() + ")";
//    }
//
//    public <T, F> Avg(Fn<T, F> fn) {
//        super(fn.toString());
//    }
//
//    public Avg(AbstractFuncColumn funcColumn) {
//        super(funcColumn.getColumnName());
//    }


}
