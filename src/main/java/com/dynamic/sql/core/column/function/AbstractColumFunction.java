package com.dynamic.sql.core.column.function;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.ColumnArithmetic;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.SqlUtils;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractColumFunction implements ColumFunction, ColumnArithmetic {
    protected final StringBuilder arithmeticSql = new StringBuilder();
    protected final ParameterBinder arithmeticParameterBinder = new ParameterBinder();
    protected Map<String, String> aliasTableMap;
    protected String dataSourceName;
    protected SqlDialect sqlDialect;
    protected Version version;

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
    public AbstractColumFunction subtract(ColumFunction columFunction) {
        String functionToString = columFunction.getFunctionToString(sqlDialect, version);
        arithmeticSql.append(" - ").append(functionToString);
        arithmeticParameterBinder.addParameterBinder(columFunction.getParameterBinder());
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
    public AbstractColumFunction multiply(ColumFunction columFunction) {
        String functionToString = columFunction.getFunctionToString(sqlDialect, version);
        arithmeticSql.append(" * ").append(functionToString);
        arithmeticParameterBinder.addParameterBinder(columFunction.getParameterBinder());
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
    public AbstractColumFunction divide(ColumFunction columFunction) {
        String functionToString = columFunction.getFunctionToString(sqlDialect, version);
        arithmeticSql.append(" / ").append(functionToString);
        arithmeticParameterBinder.addParameterBinder(columFunction.getParameterBinder());
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

    @Override
    public AbstractColumFunction add(ColumFunction columFunction) {
        String functionToString = columFunction.getFunctionToString(sqlDialect, version);
        arithmeticSql.append(" + ").append(functionToString);
        arithmeticParameterBinder.addParameterBinder(columFunction.getParameterBinder());
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

    public void setSqlDialect(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    public void setVersion(Version version) {
        this.version = version;
    }
}
