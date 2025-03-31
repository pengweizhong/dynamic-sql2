package com.dynamic.sql.model;

import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;

/**
 * 针对与算术运算列动态生成
 */
public class Arithmetic {
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
