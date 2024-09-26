package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.ColumnInfo;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.TableRelation;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.function.Consumer;

public class ColumnReference extends AbstractColumnReference {

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn) {
        return this.column(fn, null);
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn, String alias) {
        columFunctions.add(ColumnInfo.builder().columFunction(new Column(fn)).alias(alias).build());
        return this;
    }

    @Override
    public ColumnReference column(ColumFunction iColumFunction) {
        return this.column(iColumFunction, null);
    }

    @Override
    public ColumnReference column(ColumFunction iColumFunction, String alias) {
        columFunctions.add(ColumnInfo.builder().columFunction(iColumFunction).alias(alias).build());
        return this;
    }

    @Override
    public AbstractColumnReference column(WindowsFunction windowsFunction, Over over, String alias) {
        columFunctions.add(ColumnInfo.builder().columFunction(windowsFunction).alias(alias).over(over).build());
        return this;
    }

    @Override
    public AbstractColumnReference column(Consumer<NestedSelect> nestedSelect, String alias) {
        NestedSelect nested = new NestedSelect();
        nestedSelect.accept(nested);
        return this;
    }

    @Override
    public <T> TableRelation<T> from(Class<T> tableClass) {
        TableRelation tableRelation;
        tableRelation = new TableRelation<>(tableClass);
        parseColumnFunction(tableClass);
        return tableRelation;
    }

    @Override
    public TableRelation<?> from(CteTable cteTable) {
        return null;
    }

    private void parseColumnFunction(Class<?> tableClass) {
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
        for (ColumnInfo columnInfo : columFunctions) {
            String functionToString = columnInfo.getColumFunction().getFunctionToString(schemaProperties.getSqlDialect(), version);
            System.out.println("测试函数输出结果 ---> " + functionToString);
        }

    }
}
