package com.dynamic.sql.core.database.parser;


import com.dynamic.sql.core.Fn;

public interface InsertParser {

    void insertSelective(Fn<?, ?>[] forcedFields);

    void insert();

    void insertBatch();

    void insertMultiple();

    void upsert(Fn<?, ?>[] forcedFields);

    void upsertMultiple();
}