package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.HavingCondition;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SelectBuilder {
    private List<ColumnInfo> columFunctions = new ArrayList<>();
    private AllColumnInfo allColumnInfo;
    private List<JoinTable> joinTables = new ArrayList<>();
    private Consumer<WhereCondition> whereCondition;
    private NestedSelect nestedSelect;
    private List<Fn> groupByFields;
    private Consumer<HavingCondition> havingCondition;
    private LimitInfo limitInfo;

    public List<ColumnInfo> getColumFunctions() {
        return columFunctions;
    }


    public void setAllColumFunction(AllColumnInfo allColumnInfo) {
        this.allColumnInfo = allColumnInfo;
    }

    public List<JoinTable> getJoinTables() {
        return joinTables;
    }

    public void setWhereCondition(Consumer<WhereCondition> whereCondition) {
        this.whereCondition = whereCondition;
    }

    public void setNestedSelect(NestedSelect nestedSelect) {
        this.nestedSelect = nestedSelect;
    }

    public List<Fn> getGroupByFields() {
        return groupByFields;
    }

    public void setHavingCondition(Consumer<HavingCondition> havingCondition) {
        this.havingCondition = havingCondition;
    }

    public void setLimitInfo(LimitInfo limitInfo) {
        this.limitInfo = limitInfo;
    }

    public void build() {

    }
}
