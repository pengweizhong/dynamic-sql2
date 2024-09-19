package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.core.IWhereCondition;

import java.util.function.Consumer;

public interface DataDeleter {

    <T> int delete(Class<T> entityClass, Consumer<IWhereCondition> condition);

    <T> int deleteByPrimaryKey(Class<T> entityClass, Object key);

}
