package com.dynamic.sql.core.dml.select.cte;


import com.dynamic.sql.core.dml.select.NestedSelect;

import java.util.function.Consumer;

public interface ICommonTableExpression {

    ICommonTableExpression with(Class<?> cteClass, Consumer<NestedSelect> nestedSelect);

    ICommonTableExpression withRecursive(Class<?> cteClass, Consumer<NestedSelect> nestedSelect);

}
