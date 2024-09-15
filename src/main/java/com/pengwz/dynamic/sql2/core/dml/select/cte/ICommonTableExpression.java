package com.pengwz.dynamic.sql2.core.dml.select.cte;

import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;

import java.util.function.Consumer;

public interface ICommonTableExpression {

    ICommonTableExpression with(Consumer<NestedSelect> nestedSelect);

    ICommonTableExpression withRecursive(Consumer<NestedSelect> nestedSelect);

}
