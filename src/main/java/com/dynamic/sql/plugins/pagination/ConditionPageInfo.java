package com.dynamic.sql.plugins.pagination;

import com.dynamic.sql.core.condition.WhereCondition;

import java.util.function.Consumer;

public class ConditionPageInfo<T> extends PageInfo<T> {
    private Consumer<WhereCondition> appendWhereCondition;

    public ConditionPageInfo(int pageIndex, int pageSize) {
        super(pageIndex, pageSize);
    }

    public ConditionPageInfo(PageInfo<T> pageInfo, Consumer<WhereCondition> condition) {
        super(pageInfo.getPageIndex(), pageInfo.getPageSize());
        this.appendWhereCondition = condition;
    }

    public Consumer<WhereCondition> getAppendWhere() {
        return appendWhereCondition;
    }
}
