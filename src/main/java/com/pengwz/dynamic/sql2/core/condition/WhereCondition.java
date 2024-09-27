package com.pengwz.dynamic.sql2.core.condition;

import java.util.List;

public interface WhereCondition extends NestedCondition, FunctionCondition {

    String getWhereConditionSyntax();

    Object[] getWhereConditionParams();
}
