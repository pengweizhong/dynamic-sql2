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


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.ColumnArithmetic;
import com.dynamic.sql.core.dml.select.SelectDsl;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.model.Arithmetic;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

public abstract class AbstractColumFunction implements ColumFunction, ColumnArithmetic {
    protected AbstractColumFunction delegateFunction;
    private Map<String, TableAliasMapping> aliasTableMap;
    private String dataSourceName;
    private Arithmetic arithmetic = new Arithmetic(new StringBuilder(), new ParameterBinder());

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction subtract(Number value) {
        arithmetic.add("-", new Value(value)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction subtract(SelectDsl nestedSelect) {
        arithmetic.add("-", new ScalarSubquery(nestedSelect)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public <T, F> AbstractColumFunction subtract(Fn<T, F> column) {
        arithmetic.add("-", new Column(column)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction subtract(ColumFunction function) {
        if(function==null || function==this) throw new IllegalArgumentException("Invalid arithmetic operand");
        arithmetic.add("-", function); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction multiply(Number value) {
        arithmetic.add("*", new Value(value)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction multiply(SelectDsl nestedSelect) {
        arithmetic.add("*", new ScalarSubquery(nestedSelect)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public <T, F> AbstractColumFunction multiply(Fn<T, F> column) {
        arithmetic.add("*", new Column(column)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction multiply(ColumFunction function) {
        if(function==null || function==this) throw new IllegalArgumentException("Invalid arithmetic operand");
        arithmetic.add("*", function); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction divide(Number value) {
        arithmetic.add("/", new Value(value)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction divide(SelectDsl nestedSelect) {
        arithmetic.add("/", new ScalarSubquery(nestedSelect)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public <T, F> AbstractColumFunction divide(Fn<T, F> column) {
        arithmetic.add("/", new Column(column)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction divide(ColumFunction function) {
        if(function==null || function==this) throw new IllegalArgumentException("Invalid arithmetic operand");
        arithmetic.add("/", function); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction add(Number value) {
        arithmetic.add("+", new Value(value)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction add(SelectDsl nestedSelect) {
        arithmetic.add("+", new ScalarSubquery(nestedSelect)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public <T, F> AbstractColumFunction add(Fn<T, F> column) {
        arithmetic.add("+", new Column(column)); return this;
    }

    /** 记录操作数并推迟列与参数渲染，链式运算遵循 SQL 优先级。 */
    @Override public AbstractColumFunction add(ColumFunction function) {
        if(function==null || function==this) throw new IllegalArgumentException("Invalid arithmetic operand");
        arithmetic.add("+", function); return this;
    }
    public AbstractColumFunction getDelegateFunction() {
        return delegateFunction;
    }


    public Map<String, TableAliasMapping> getAliasTableMap() {
        return aliasTableMap;
    }

    public void setAliasTableMap(Map<String, TableAliasMapping> aliasTableMap) {
        if (aliasTableMap == null) {
            throw new DynamicSqlException("Parameters must be set before calling: aliasTableMap");
        }
        this.aliasTableMap = aliasTableMap;
    }

    public String getDataSourceName() {
        if (dataSourceName == null) {
            throw new DynamicSqlException("Parameters must be set before calling: dataSourceName");
        }
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public Arithmetic getArithmetic() {
        return arithmetic;
    }

    public void setArithmetic(Arithmetic arithmetic) {
        this.arithmetic = arithmetic;
    }
}
