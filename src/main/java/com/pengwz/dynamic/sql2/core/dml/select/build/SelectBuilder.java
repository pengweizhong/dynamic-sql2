package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.HavingCondition;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.ColumnQuery;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.FunctionColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.NestedColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SelectBuilder {
    private StringBuilder sqlBuilder = new StringBuilder();
    private List<ColumnQuery> columFunctions = new ArrayList<>();
    private List<JoinTable> joinTables = new ArrayList<>();
    private Consumer<WhereCondition> whereCondition;
    private List<Fn> groupByFields;
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

    public List<Fn> getGroupByFields() {
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

    public StringBuilder getSqlBuilder() {
        return sqlBuilder;
    }

    public void build(SelectBuilder selectBuilder) {
        selectBuilder.getSqlBuilder().append("SELECT ");
        parseColumnFunction(selectBuilder);
        System.out.println("构建后的SQL：" + selectBuilder.getSqlBuilder().toString());
    }

    private void parseColumnFunction(SelectBuilder selectBuilder) {
        JoinTable joinTable = selectBuilder.getJoinTables().get(0);
        FromJoin fromJoin = (FromJoin) joinTable;
        Class<?> tableClass = fromJoin.getTableClass();
        String classCanonicalName = tableClass.getCanonicalName();
//        TableMeta tableMeta = TableProvider.getInstance().getTableMeta(tableClass);
//        DataSourceMeta dataSourceMeta = DataSourceProvider.getInstance().getDataSourceMeta(tableMeta.getBindDataSourceName());
//
//        Version version;
//        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
//        if (StringUtils.isBlank(schemaProperties.getDatabaseProductVersion())) {
//            version = new Version(dataSourceMeta.getMajorVersionNumber(),
//                    dataSourceMeta.getMinorVersionNumber(), dataSourceMeta.getPatchVersionNumber());
//        } else {
//            version = new Version(schemaProperties.getMajorVersionNumber(),
//                    schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
//        }
        List<ColumnQuery> columFunctions1 = selectBuilder.getColumFunctions();
        StringBuilder sqlBuilder = selectBuilder.getSqlBuilder();
        ArrayList<String> columns = new ArrayList<>();
        for (ColumnQuery columnQuery : columFunctions1) {
            if (columnQuery instanceof FunctionColumn) {
                FunctionColumn functionColumn = (FunctionColumn) columnQuery;
                ColumFunction columFunction = functionColumn.getColumFunction();
                String functionToString = columFunction.getFunctionToString(getSqlDialect(classCanonicalName), getVersion(classCanonicalName));
                System.out.println("测试函数输出结果 ---> " + functionToString);
                columns.add(functionToString);
            }
            if (columnQuery instanceof NestedColumn) {
                NestedColumn nestedColumn = (NestedColumn) columnQuery;
                Consumer<NestedSelect> nestedSelect = nestedColumn.getNestedSelect();

            }
        }
        sqlBuilder.append(String.join(", ", columns));

    }

    private Version getVersion(String classCanonicalName) {
        TableMeta tableMeta = TableProvider.getTableMeta(classCanonicalName);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        if (StringUtils.isBlank(schemaProperties.getDatabaseProductVersion())) {
            DataSourceMeta dataSourceMeta = DataSourceProvider.getDataSourceMeta(tableMeta.getBindDataSourceName());
            return new Version(dataSourceMeta.getMajorVersionNumber(),
                    dataSourceMeta.getMinorVersionNumber(), dataSourceMeta.getPatchVersionNumber());
        } else {
            return new Version(schemaProperties.getMajorVersionNumber(),
                    schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
        }
    }

    private SqlDialect getSqlDialect(String classCanonicalName) {
        TableMeta tableMeta = TableProvider.getTableMeta(classCanonicalName);
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        return schemaProperties.getSqlDialect();
    }

}
