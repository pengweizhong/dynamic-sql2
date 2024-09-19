package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.core.IWhereCondition;

import java.util.function.Consumer;

public class Delete implements DataDeleter {
    @Override
    public <T> int delete(Class<T> entityClass, Consumer<IWhereCondition> condition) {
        return 0;
    }

    @Override
    public <T> int deleteByPrimaryKey(Class<T> entityClass, Object key) {
        return 0;
    }
}
