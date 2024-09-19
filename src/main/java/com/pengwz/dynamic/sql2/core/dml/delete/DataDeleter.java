package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.core.condition.WhereCondition;

import java.util.function.Consumer;

public interface DataDeleter {

    <T> int delete(Class<T> entityClass, Consumer<WhereCondition> condition);

    <T> int deleteByPrimaryKey(Class<T> entityClass, Object key);

}
