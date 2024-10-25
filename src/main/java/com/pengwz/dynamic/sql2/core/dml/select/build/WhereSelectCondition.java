package com.pengwz.dynamic.sql2.core.dml.select.build;

import com.pengwz.dynamic.sql2.core.condition.FunctionCondition;
import com.pengwz.dynamic.sql2.core.condition.NestedCondition;
import com.pengwz.dynamic.sql2.core.dml.select.HavingCondition;
import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

public abstract class WhereSelectCondition implements NestedCondition, FunctionCondition, HavingCondition {

    protected abstract String getWhereConditionSyntax();

    protected abstract ParameterBinder getParameterBinder();
}
