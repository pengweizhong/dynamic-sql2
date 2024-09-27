package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.HavingCondition;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.ColumnQuery;
import com.pengwz.dynamic.sql2.core.dml.select.build.column.FunctionColumn;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.FromJoin;
import com.pengwz.dynamic.sql2.core.dml.select.build.join.JoinTable;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SelectBuilder {
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

    public void build() {
        parseColumnFunction();
    }

    private void parseColumnFunction() {
        JoinTable joinTable = joinTables.get(0);
        FromJoin fromJoin = (FromJoin) joinTable;
        Class<?> tableClass = fromJoin.getTableClass();
        TableMeta tableMeta = TableProvider.getInstance().getTableMeta(tableClass);
        DataSourceMeta dataSourceMeta = DataSourceProvider.getInstance().getDataSourceMeta(tableMeta.getBindDataSourceName());

        Version version;
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(tableMeta.getBindDataSourceName());
        if (StringUtils.isBlank(schemaProperties.getDatabaseProductVersion())) {
            version = new Version(dataSourceMeta.getMajorVersionNumber(),
                    dataSourceMeta.getMinorVersionNumber(), dataSourceMeta.getPatchVersionNumber());
        } else {
            version = new Version(schemaProperties.getMajorVersionNumber(),
                    schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
        }
        List<ColumnQuery> columFunctions1 = columFunctions;
        for (ColumnQuery columnQuery : columFunctions1) {
            if (columnQuery instanceof FunctionColumn) {
                FunctionColumn functionColumn = (FunctionColumn) columnQuery;
                String functionToString = functionColumn.getColumFunction().getFunctionToString(schemaProperties.getSqlDialect(), version);
                System.out.println("测试函数输出结果 ---> " + functionToString);
            }
        }


    }
}
