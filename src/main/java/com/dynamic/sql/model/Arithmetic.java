/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.model;

import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.column.function.RenderContext;
import java.util.ArrayList;
import java.util.List;
import com.dynamic.sql.core.placeholder.ParameterBinder;

/**
 * 针对与算术运算列动态生成
 */
public class Arithmetic {
    private final List<String> operators=new ArrayList<>();
    private final List<ColumFunction> operands=new ArrayList<>();

    /** 按调用顺序记录算术项，字段别名到渲染阶段才解析。 */
    public void add(String operator,ColumFunction operand) {
        operators.add(operator); operands.add(operand);
    }
    /** 渲染不修改表达式结构，重复执行不会追加 SQL。 */
    public String render(RenderContext context) {
        StringBuilder sql=new StringBuilder(arithmeticSql);
        for(int i=0;i<operands.size();i++) {
            sql.append(" ").append(operators.get(i)).append(" (").append(operands.get(i).render(context)).append(")");
            arithmeticParameterBinder.addParameterBinder(operands.get(i).parameterBinder());
        }
        if(columFunctionArithmetic!=null) {
            sql.append(columFunctionArithmetic.render(context));
            arithmeticParameterBinder.addParameterBinder(columFunctionArithmetic.parameterBinder());
        }
        return sql.toString();
    }

    protected final StringBuilder arithmeticSql;
    protected final ParameterBinder arithmeticParameterBinder;
    //该列需要动态运算
    private ColumFunction columFunctionArithmetic;

    public Arithmetic(final StringBuilder arithmeticSql, final ParameterBinder arithmeticParameterBinder) {
        this.arithmeticSql = arithmeticSql;
        this.arithmeticParameterBinder = arithmeticParameterBinder;
    }

    public StringBuilder getArithmeticSql() {
        return arithmeticSql;
    }

    public ParameterBinder getArithmeticParameterBinder() {
        return arithmeticParameterBinder;
    }

    public ColumFunction getColumFunctionArithmetic() {
        return columFunctionArithmetic;
    }

    public void setColumFunctionArithmetic(ColumFunction columFunctionArithmetic) {
        this.columFunctionArithmetic = columFunctionArithmetic;
    }
}
