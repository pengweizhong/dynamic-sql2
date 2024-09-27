package com.pengwz.dynamic.sql2.core.condition;

public interface WhereCondition extends NestedCondition, FunctionCondition {

    String getWhereConditionSyntax();
}
