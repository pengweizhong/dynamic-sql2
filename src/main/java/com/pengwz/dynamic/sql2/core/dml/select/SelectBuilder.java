package com.pengwz.dynamic.sql2.core.dml.select;

import java.util.ArrayList;
import java.util.List;

public class SelectBuilder {
    protected final List<ColumnInfo> columFunctions = new ArrayList<>();
    private String table;
    private StringBuilder whereClause = new StringBuilder();
    private List<String> groupByColumns = new ArrayList<>();
    private StringBuilder havingClause = new StringBuilder();
    //嵌套查询
    private SelectBuilder selectBuilder;
}
