package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.dml.select.build.WhereSelectCondition;

import java.util.Collection;
import java.util.function.Consumer;

public interface DataUpdater {

    <T> int update(T entity, Consumer<WhereSelectCondition> condition);

    <T> int updateSelective(T entity, Consumer<WhereSelectCondition> condition);

    <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields);

    <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields, Consumer<WhereSelectCondition> condition);

    <T> int updateByPrimaryKey(T entity);

    <T> int updateSelectiveByPrimaryKey(T entity);

}
