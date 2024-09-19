package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.core.Fn;

import java.util.Collection;

public interface DataInserter {

    <T> int insertSelective(T entity);

    <T, F> int insertSelective(T entity, Collection<Fn<T, F>> forcedFields);

    <T> int insert(T entity);

    <T> int batchInsert(Collection<T> entities);

    <T> int upsert(T entity);

    <T> int batchUpsert(Collection<T> entities);

}
