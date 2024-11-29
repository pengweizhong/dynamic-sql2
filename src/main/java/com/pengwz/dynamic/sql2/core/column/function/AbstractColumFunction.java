package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.core.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.ColumnArithmetic;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlStatementSelectWrapper;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractColumFunction implements ColumFunction, ColumnArithmetic {
    protected final StringBuilder arithmeticSql = new StringBuilder();
    protected final ParameterBinder arithmeticParameterBinder = new ParameterBinder();
    protected Map<String, String> aliasTableMap;
    protected String dataSourceName;

    @Override
    public AbstractColumFunction subtract(Number value) {
        arithmeticSql.append(" - ").append(SqlUtils.registerValueWithKey(arithmeticParameterBinder, value));
        return this;
    }

    @Override
    public AbstractColumFunction subtract(Consumer<AbstractColumnReference> nestedSelect) {
        SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedSelect);
        arithmeticSql.append(" - (").append(sqlStatementWrapper.getRawSql()).append(")");
        arithmeticParameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> AbstractColumFunction subtract(Fn<T, F> column) {
        String name = SqlUtils.extractQualifiedAlias(column, aliasTableMap, dataSourceName);
        arithmeticSql.append(" - ").append(name);
        return this;
    }

    @Override
    public AbstractColumFunction multiply(Number value) {
        arithmeticSql.append(" * ").append(SqlUtils.registerValueWithKey(arithmeticParameterBinder, value));
        return this;
    }

    @Override
    public AbstractColumFunction multiply(Consumer<AbstractColumnReference> nestedSelect) {
        SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedSelect);
        arithmeticSql.append(" * (").append(sqlStatementWrapper.getRawSql()).append(")");
        arithmeticParameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> AbstractColumFunction multiply(Fn<T, F> column) {
        String name = SqlUtils.extractQualifiedAlias(column, aliasTableMap, dataSourceName);
        arithmeticSql.append(" * ").append(name);
        return this;
    }

    @Override
    public AbstractColumFunction divide(Number value) {
        arithmeticSql.append(" / ").append(SqlUtils.registerValueWithKey(arithmeticParameterBinder, value));
        return this;
    }

    @Override
    public AbstractColumFunction divide(Consumer<AbstractColumnReference> nestedSelect) {
        SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedSelect);
        arithmeticSql.append(" / (").append(sqlStatementWrapper.getRawSql()).append(")");
        arithmeticParameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> AbstractColumFunction divide(Fn<T, F> column) {
        String name = SqlUtils.extractQualifiedAlias(column, aliasTableMap, dataSourceName);
        arithmeticSql.append(" / ").append(name);
        return this;
    }

    @Override
    public AbstractColumFunction add(Number value) {
        arithmeticSql.append(" + ").append(SqlUtils.registerValueWithKey(arithmeticParameterBinder, value));
        return this;
    }

    @Override
    public AbstractColumFunction add(Consumer<AbstractColumnReference> nestedSelect) {
        SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedSelect);
        arithmeticSql.append(" + (").append(sqlStatementWrapper.getRawSql()).append(")");
        arithmeticParameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> AbstractColumFunction add(Fn<T, F> column) {
        String name = SqlUtils.extractQualifiedAlias(column, aliasTableMap, dataSourceName);
        arithmeticSql.append(" + ").append(name);
        return this;
    }

    public StringBuilder getArithmeticSql() {
        return arithmeticSql;
    }

    public ParameterBinder getArithmeticParameterBinder() {
        return arithmeticParameterBinder;
    }

    public Map<String, String> getAliasTableMap() {
        return aliasTableMap;
    }

    public void setAliasTableMap(Map<String, String> aliasTableMap) {
        if (aliasTableMap == null) {
            throw new IllegalStateException("Parameters must be set before calling: aliasTableMap");
        }
        this.aliasTableMap = aliasTableMap;
    }

    public String getDataSourceName() {
        if (dataSourceName == null) {
            throw new IllegalStateException("Parameters must be set before calling: dataSourceName");
        }
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }
}
