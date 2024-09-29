package com.pengwz.dynamic.sql2.core.condition;

import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

public interface WhereCondition extends NestedCondition, FunctionCondition {

    String getWhereConditionSyntax();

    ParameterBinder getParameterBinder();
}
