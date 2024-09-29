package com.pengwz.dynamic.sql2.core.condition;

import com.pengwz.dynamic.sql2.core.dml.select.HavingCondition;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

public interface WhereCondition extends NestedCondition, FunctionCondition, HavingCondition {

    String getWhereConditionSyntax();

    ParameterBinder getParameterBinder();
}
