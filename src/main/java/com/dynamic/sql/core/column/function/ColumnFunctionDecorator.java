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


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.model.Arithmetic;

import java.io.Serializable;

public abstract class ColumnFunctionDecorator extends AbstractColumFunction
        implements Serializable {
    //count 1
    protected Integer value;
    protected ParameterBinder parameterBinder = new ParameterBinder();

    public ColumnFunctionDecorator() {
        delegateFunction = new AnonymousFunction();
    }

    public ColumnFunctionDecorator(AbstractColumFunction delegateFunction) {
        this.delegateFunction = delegateFunction;
    }

    public <T, F> ColumnFunctionDecorator(FieldFn<T, F> fn) {
        this.delegateFunction = new Column(null, fn);
    }

    public <T, F> ColumnFunctionDecorator(String tableAlias, FieldFn<T, F> fn) {
        this.delegateFunction = new Column(tableAlias, fn);
    }

    public ColumnFunctionDecorator(String tableAlias, String columnName) {
        this.delegateFunction = new Column(tableAlias, columnName);
    }

    public ColumnFunctionDecorator(int value) {
        this.value = value;
    }

    //窗口函数
//    public ColumnFunctionDecorator(WindowsFunction windowsFunction, Over over) {
//        this.delegateFunction = windowsFunction;
//    }

    @Override
    public Fn<?, ?> getOriginColumnFn() {
        return delegateFunction.getOriginColumnFn();
    }

    @Override
    public ParameterBinder getParameterBinder() {
        return parameterBinder.addParameterBinder(delegateFunction.getParameterBinder());
    }

    @Override
    public String getTableAlias() {
        return delegateFunction.getTableAlias();
    }

    /**
     * 如果当前列列函数支持数学运算，则应追加此方法。
     *
     * @param sqlDialect 数据库类型
     * @param version    版本号
     * @return 常规加减乘除运算后的结果
     */
    protected String appendArithmeticSql(SqlDialect sqlDialect, Version version) {
        Arithmetic arithmetic = getArithmetic();
        if (arithmetic == null) {
            return "";
        }
        ColumFunction columFunctionArithmetic = arithmetic.getColumFunctionArithmetic();
        if (columFunctionArithmetic == null) {
            parameterBinder.addParameterBinder(arithmetic.getArithmeticParameterBinder());
            return arithmetic.getArithmeticSql().toString();
        }
        String functionToString = columFunctionArithmetic.getFunctionToString(sqlDialect, version, aliasTableMap);
        parameterBinder.addParameterBinder(columFunctionArithmetic.getParameterBinder());
        return arithmetic.getArithmeticSql().append(functionToString).toString();
    }
}
