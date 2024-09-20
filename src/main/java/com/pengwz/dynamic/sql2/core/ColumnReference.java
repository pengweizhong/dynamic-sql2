package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.column.function.windows.Over;
import com.pengwz.dynamic.sql2.core.column.function.windows.WindowsFunction;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;
import com.pengwz.dynamic.sql2.core.dml.select.TableRelation;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.datasource.DataSourceMeta;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;

import java.util.function.Consumer;

public class ColumnReference extends AbstractColumnReference {
//    private ColumFunction columnFunction;

    public ColumnReference() {
    }

    public ColumnReference(ColumFunction columnFunction) {
//        this.columnFunction = columnFunction;
        columFunctions.add(columnFunction);
    }

    public <T, F> ColumnReference(Fn<T, F> fn) {
        columFunctions.add(new Column(fn));
    }


    @Override
    public AbstractColumnReference one() {
        return this;
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn) {
        return this.column(fn, null);
    }

    @Override
    public <T, F> ColumnReference column(Fn<T, F> fn, String alias) {
        columFunctions.add(new Column(fn));
        return this;
    }

    @Override
    public ColumnReference column(ColumFunction iColumFunction) {
        return this.column(iColumFunction, null);
    }

    @Override
    public ColumnReference column(ColumFunction iColumFunction, String alias) {
        columFunctions.add(iColumFunction);
        return this;
    }

    @Override
    public AbstractColumnReference column(WindowsFunction windowsFunction, Over over, String alias) {
        // Build the SQL part for the window function
        String orderByClause = String.join(", ", over.getOrderByColumns());
        String partitionByClause = over.getPartitionByClause();
        String frameSpecification = over.getFrameSpecification();

        String sql = windowsFunction.apply(over) + " OVER (" +
                partitionByClause + " ORDER BY " + orderByClause +
                " " + frameSpecification + ") AS " + alias;

        // Add this SQL part to the select query
        // Assuming some internal mechanism to store/select SQL parts
        return this;
    }

    @Override
    public AbstractColumnReference column(Consumer<NestedSelect> nestedSelect, String alias) {
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
        DataSourceMeta dataSourceMeta = DataSourceProvider.getInstance().getDataSourceMeta(tableClass);
        for (ColumFunction columFunction : columFunctions) {
            String sqlFunction;
            switch (dataSourceMeta.getSqlDialect()) {
                case ORACLE:
                    sqlFunction = columFunction.getOracleFunction();
                    break;
                case MYSQL:
                    sqlFunction = columFunction.getMySqlFunction();
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported SQL dialect: " + dataSourceMeta.getSqlDialect());
            }
            System.out.println("测试函数输出结果 ---> " + sqlFunction);
        }
    }
}
