package com.pengwz.dynamic2.sql.core.crud.select;

import com.pengwz.dynamic2.sql.core.column.IColumn;
import com.pengwz.dynamic2.sql.core.column.conventional.AllColumn;
import com.pengwz.dynamic2.sql.core.column.conventional.Column;

import java.util.ArrayList;
import java.util.List;

public class Selector {
    private final List<IColumn> columns = new ArrayList<>();

    private Selector() {
    }

//    public static Selector allColumn() {
//        Selector selector = new Selector();
//        selector.columns.add(new AllColumn());
//        return selector;
//    }
//
//    public static Selector column(String columnName) {
//        Selector selector = new Selector();
//        selector.columns.add(new Column(columnName));
//        return selector;
//    }
//
////    public Selector column(String columnName) {
////        columns.add(new Column(columnName));
////        return this;
////    }
//
//    public Selector from(String table) {
//        return this;
//    }
}
