package com.pengwz.dynamic.sql2.core.column.function.scalar.string;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumnFunctionDecorator;
import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

public class Like extends ColumnFunctionDecorator {
    private String leftSymbol;
    private String rightSymbol;

    public Like(IColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public Like(String leftSymbol, IColumFunction delegateFunction, String rightSymbol) {
        super(delegateFunction);
        this.leftSymbol = leftSymbol;
        this.rightSymbol = rightSymbol;
    }

    public Like(String leftSymbol, IColumFunction delegateFunction) {
        super(delegateFunction);
        this.leftSymbol = leftSymbol;
    }

    public Like(IColumFunction delegateFunction, String rightSymbol) {
        super(delegateFunction);
        this.rightSymbol = rightSymbol;
    }

    public <T, F> Like(Fn<T, F> fn) {
        super(fn);
    }

    public <T, F> Like(String leftSymbol, Fn<T, F> fn, String rightSymbol) {
        super(fn);
        this.leftSymbol = leftSymbol;
        this.rightSymbol = rightSymbol;
    }

    public <T, F> Like(String leftSymbol, Fn<T, F> fn) {
        super(fn);
        this.leftSymbol = leftSymbol;
    }

    public <T, F> Like(Fn<T, F> fn, String rightSymbol) {
        super(fn);
        this.rightSymbol = rightSymbol;
    }

    @Override
    public String getFunctionToString() {
        if (leftSymbol != null && rightSymbol != null) {
            return "CONCAT('" + leftSymbol + "', " + delegateFunction.getFunctionToString() + ", '" + rightSymbol + "')";
        }
        if (leftSymbol != null) {
            return "CONCAT('" + leftSymbol + "', " + delegateFunction.getFunctionToString() + ")";
        }
        if (rightSymbol != null) {
            return "CONCAT(" + delegateFunction.getFunctionToString() + ", '" + rightSymbol + "')";
        }
        return delegateFunction.getFunctionToString();
    }
}