package com.pengwz.dynamic.sql2.core.dml.update;

public interface UpdateHandler {

    int updateByPrimaryKey();

    int updateSelectiveByPrimaryKey();

    //    int update(Consumer<WhereCondition> condition);
//
//    <T> int updateSelective(T entity, Consumer<WhereCondition> condition);
//
//    <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields);
//
//    <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields, Consumer<WhereCondition> condition);
//

//
//    <T> int updateSelectiveByPrimaryKey(T entity);
    //    int upsert();
    //
    //    int batchUpsert();
}
