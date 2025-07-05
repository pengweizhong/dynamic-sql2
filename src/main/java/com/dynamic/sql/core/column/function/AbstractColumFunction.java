/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.ColumnArithmetic;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.model.Arithmetic;
import com.dynamic.sql.model.TableAliasMapping;
import com.dynamic.sql.utils.SqlUtils;

import java.util.Map;
import java.util.function.Consumer;

public abstract class AbstractColumFunction implements ColumFunction, ColumnArithmetic {
    //函数嵌套引用
    protected AbstractColumFunction delegateFunction;
    //    protected final StringBuilder arithmeticSql = new StringBuilder();
//    protected final ParameterBinder arithmeticParameterBinder = new ParameterBinder();
    protected Map<String, TableAliasMapping> aliasTableMap;
    protected String dataSourceName;
    protected SqlDialect sqlDialect;
    protected Version version;
    private Arithmetic arithmetic = new Arithmetic(new StringBuilder(), new ParameterBinder());

    @Override
    public AbstractColumFunction subtract(Number value) {
//        arithmeticSql.append(" - ").append(SqlUtils.registerValueWithKey(arithmeticParameterBinder, value));
        arithmetic.getArithmeticSql().append(" - ").append(SqlUtils.registerValueWithKey(arithmetic.getArithmeticParameterBinder(), value));
        return this;
    }

    @Override
    public AbstractColumFunction subtract(Consumer<AbstractColumnReference> nestedSelect) {
        SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedSelect);
//        arithmeticSql.append(" - (").append(sqlStatementWrapper.getRawSql()).append(")");
//        arithmeticParameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
        arithmetic.getArithmeticSql().append(" - (").append(sqlStatementWrapper.getRawSql()).append(")");
        arithmetic.getArithmeticParameterBinder().addParameterBinder(sqlStatementWrapper.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> AbstractColumFunction subtract(Fn<T, F> column) {
        String name = SqlUtils.extractQualifiedAlias(column, aliasTableMap, dataSourceName);
//        arithmeticSql.append(" - ").append(name);
        arithmetic.getArithmeticSql().append(" - ").append(name);
        return this;
    }

    @Override
    public AbstractColumFunction subtract(ColumFunction columFunction) {
//        columFunctionArithmetic = columFunction;
//        arithmeticSql.append(" - ");
        return this;
    }

    @Override
    public AbstractColumFunction multiply(Number value) {
//        arithmeticSql.append(" * ").append(SqlUtils.registerValueWithKey(arithmeticParameterBinder, value));
        arithmetic.getArithmeticSql().append(" * ").append(SqlUtils.registerValueWithKey(arithmetic.getArithmeticParameterBinder(), value));
        return this;
    }

    @Override
    public AbstractColumFunction multiply(Consumer<AbstractColumnReference> nestedSelect) {
        SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedSelect);
//        arithmeticSql.append(" * (").append(sqlStatementWrapper.getRawSql()).append(")");
//        arithmeticParameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
        arithmetic.getArithmeticSql().append(" * (").append(sqlStatementWrapper.getRawSql()).append(")");
        arithmetic.getArithmeticParameterBinder().addParameterBinder(sqlStatementWrapper.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> AbstractColumFunction multiply(Fn<T, F> column) {
        String name = SqlUtils.extractQualifiedAlias(column, aliasTableMap, dataSourceName);
//        arithmeticSql.append(" * ").append(name);
        arithmetic.getArithmeticSql().append(" * ").append(name);
        return this;
    }

    @Override
    public AbstractColumFunction multiply(ColumFunction columFunction) {
//        columFunctionArithmetic = columFunction;
//        arithmeticSql.append(" * ");
        return this;
    }

    @Override
    public AbstractColumFunction divide(Number value) {
//        arithmeticSql.append(" / ").append(SqlUtils.registerValueWithKey(arithmeticParameterBinder, value));
        arithmetic.getArithmeticSql().append(" / ").append(SqlUtils.registerValueWithKey(arithmetic.getArithmeticParameterBinder(), value));
        return this;
    }

    @Override
    public AbstractColumFunction divide(Consumer<AbstractColumnReference> nestedSelect) {
        SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedSelect);
        arithmetic.getArithmeticSql().append(" / (").append(sqlStatementWrapper.getRawSql()).append(")");
        arithmetic.getArithmeticParameterBinder().addParameterBinder(sqlStatementWrapper.getParameterBinder());
//        arithmeticSql.append(" / (").append(sqlStatementWrapper.getRawSql()).append(")");
//        arithmeticParameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> AbstractColumFunction divide(Fn<T, F> column) {
        String name = SqlUtils.extractQualifiedAlias(column, aliasTableMap, dataSourceName);
//        arithmeticSql.append(" / ").append(name);
        arithmetic.getArithmeticSql().append(" / ").append(name);
        return this;
    }

    @Override
    public AbstractColumFunction divide(ColumFunction columFunction) {
//        columFunctionArithmetic = columFunction;
//        arithmeticSql.append(" / ");
        arithmetic.setColumFunctionArithmetic(columFunction);
        arithmetic.getArithmeticSql().append(" / ");
        return this;
    }

    @Override
    public AbstractColumFunction add(Number value) {
        arithmetic.getArithmeticSql().append(" + ").append(SqlUtils.registerValueWithKey(arithmetic.getArithmeticParameterBinder(), value));
//        arithmeticSql.append(" + ").append(SqlUtils.registerValueWithKey(arithmeticParameterBinder, value));
        return this;
    }

    @Override
    public AbstractColumFunction add(Consumer<AbstractColumnReference> nestedSelect) {
        SqlStatementSelectWrapper sqlStatementWrapper = SqlUtils.executeNestedSelect(nestedSelect);
//        arithmeticSql.append(" + (").append(sqlStatementWrapper.getRawSql()).append(")");
//        arithmeticParameterBinder.addParameterBinder(sqlStatementWrapper.getParameterBinder());
        arithmetic.getArithmeticSql().append(" + (").append(sqlStatementWrapper.getRawSql()).append(")");
        arithmetic.getArithmeticParameterBinder().addParameterBinder(sqlStatementWrapper.getParameterBinder());
        return this;
    }

    @Override
    public <T, F> AbstractColumFunction add(Fn<T, F> column) {
        String name = SqlUtils.extractQualifiedAlias(column, aliasTableMap, dataSourceName);
//        arithmeticSql.append(" + ").append(name);
        arithmetic.getArithmeticSql().append(" + ").append(name);
        return this;
    }

    @Override
    public AbstractColumFunction add(ColumFunction columFunction) {
//        columFunctionArithmetic = columFunction;
//        arithmeticSql.append(" + ");
        return this;
    }

    public AbstractColumFunction getDelegateFunction() {
        return delegateFunction;
    }

//    public StringBuilder getArithmeticSql() {
//        return arithmeticSql;
//    }
//
//    public ParameterBinder getArithmeticParameterBinder() {
//        return arithmeticParameterBinder;
//    }
//
//    public ColumFunction getColumFunctionArithmetic() {
//        return columFunctionArithmetic;
//    }

    public Map<String, TableAliasMapping> getAliasTableMap() {
        return aliasTableMap;
    }

    public void setAliasTableMap(Map<String, TableAliasMapping> aliasTableMap) {
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

    public Arithmetic getArithmetic() {
        return arithmetic;
    }

    public void setArithmetic(Arithmetic arithmetic) {
        this.arithmetic = arithmetic;
    }
}
