package com.pengwz.dynamic.sql2.core.database.parser;

import com.pengwz.dynamic.sql2.core.Fn;

public interface InsertParser {

    void insertSelective(Fn<?, ?>[] forcedFields);

    void insert();

    void insertBatch();

    void insertMultiple();
}
