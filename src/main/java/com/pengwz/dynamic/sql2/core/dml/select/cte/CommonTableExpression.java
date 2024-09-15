package com.pengwz.dynamic.sql2.core.dml.select.cte;

import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;

import java.util.function.Consumer;

public class CommonTableExpression implements ICommonTableExpression {
    private String cteName;

    private CommonTableExpression() {
    }

    public static CommonTableExpression cte(String cteName) {
        CommonTableExpression commonTableExpression = new CommonTableExpression();
        commonTableExpression.cteName = cteName;
        return commonTableExpression;
    }

    @Override
    public CommonTableExpression with(Consumer<NestedSelect> nestedSelect) {
        this.cteName = cteName;
        return null;
    }

    @Override
    public CommonTableExpression withRecursive(Consumer<NestedSelect> nestedSelect) {
        this.cteName = cteName;
        return null;
    }
}
