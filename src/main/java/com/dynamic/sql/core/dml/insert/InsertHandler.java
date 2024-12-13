package com.dynamic.sql.core.dml.insert;

public interface InsertHandler {

    int insertSelective();

    int insert();

    int insertBatch();

    int insertMultiple();

    int upsert();

    int upsertSelective();

    int upsertMultiple();
}
