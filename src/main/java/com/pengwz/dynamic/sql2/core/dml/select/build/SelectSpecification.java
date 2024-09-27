package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.HavingCondition;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.ColumnQuery;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SelectSpecification {
    private List<ColumnQuery> columFunctions = new ArrayList<>();
    private List<JoinTable> joinTables = new ArrayList<>();
    private Consumer<WhereCondition> whereCondition;
    private List<Fn<?, ?>> groupByFields;
    private Consumer<HavingCondition> havingCondition;
    private LimitInfo limitInfo;

    public List<ColumnQuery> getColumFunctions() {
        return columFunctions;
    }

    public List<JoinTable> getJoinTables() {
        return joinTables;
    }

    public void setWhereCondition(Consumer<WhereCondition> whereCondition) {
        this.whereCondition = whereCondition;
    }

    public List<Fn<?, ?>> getGroupByFields() {
        if (groupByFields == null) {
            groupByFields = new ArrayList<>();
        }
        return groupByFields;
    }

    public void setHavingCondition(Consumer<HavingCondition> havingCondition) {
        this.havingCondition = havingCondition;
    }

    public void setLimitInfo(LimitInfo limitInfo) {
        this.limitInfo = limitInfo;
    }

}
