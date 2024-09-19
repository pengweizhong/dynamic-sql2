package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.IWhereCondition;

import java.util.Collection;
import java.util.function.Consumer;

public interface DataUpdater {

    <T> int update(T data, Consumer<IWhereCondition> condition);

    <T> int updateSelective(T entity, Consumer<IWhereCondition> condition);

    <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields);

    <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields, Consumer<IWhereCondition> condition);

    <T> int updateByPrimaryKey(T entity);

    <T> int updateSelectiveByPrimaryKey(T entity);

}
