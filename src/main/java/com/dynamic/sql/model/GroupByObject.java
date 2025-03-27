package com.dynamic.sql.model;

import java.util.ArrayList;
import java.util.List;

public class GroupByObject {

    //    private List<Fn<?, ?>> groupByFields;
    // 可能是函数 ColumFunction、或者实际字段 Fn<?, ?>
    private List<Object> groupByList;

//    public List<Fn<?, ?>> getGroupByFields() {
//        if (groupByFields == null) {
//            groupByFields = new ArrayList<>();
//        }
//        return groupByFields;
//    }

    public List<Object> getGroupByList() {
        if (groupByList == null) {
            groupByList = new ArrayList<>();
        }
        return groupByList;
    }

}
