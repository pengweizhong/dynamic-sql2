package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.core.dml.select.build.WhereSelectCondition;

import java.util.function.Consumer;

public interface DataDeleter {

    <T> int delete(Class<T> entityClass, Consumer<WhereSelectCondition> condition);

    <T> int deleteByPrimaryKey(Class<T> entityClass, Object key);

}
