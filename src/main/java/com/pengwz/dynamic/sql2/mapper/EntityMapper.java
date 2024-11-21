package com.pengwz.dynamic.sql2.mapper;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;

import java.util.Collection;
import java.util.function.Consumer;

public interface EntityMapper<T> {

    AbstractColumnReference select();


    T selectByPrimaryKey(Object pkValue);


    int insertSelective(T entity);


    int insertSelective(T entity, Collection<Fn<T, ?>> forcedFields);


    int insert(T entity);


    int insertBatch(Collection<T> entities);


    int insertMultiple(Collection<T> entities);


    int deleteByPrimaryKey(Object pkValue);


    int deleteByPrimaryKey(Collection<Object> pkValues);


    int delete(Consumer<WhereCondition> condition);


    int updateByPrimaryKey(T entity);


    int updateSelectiveByPrimaryKey(T entity);


    int updateSelectiveByPrimaryKey(T entity, Collection<Fn<T, ?>> forcedFields);


    int update(T entity, Consumer<WhereCondition> condition);


    int updateSelective(T entity, Consumer<WhereCondition> condition);


    int updateSelective(T entity, Collection<Fn<T, ?>> forcedFields, Consumer<WhereCondition> condition);


    int upsert(T entity);


    int upsertSelective(T entity);


    int upsertSelective(T entity, Collection<Fn<T, ?>> forcedFields);


    int upsertMultiple(Collection<T> entities);

}
