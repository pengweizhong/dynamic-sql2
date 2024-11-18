package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.dml.select.build.WhereCondition;

import java.util.Collection;
import java.util.function.Consumer;

public interface DataUpdater {

    <T> int update(T entity, Consumer<WhereCondition> condition);

    <T> int updateSelective(T entity, Consumer<WhereCondition> condition);

    <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields);

    <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields, Consumer<WhereCondition> condition);

    <T> int updateByPrimaryKey(T entity);

    <T> int updateSelectiveByPrimaryKey(T entity);

}
