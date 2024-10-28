package com.pengwz.dynamic.sql2.core.database.parser;

import com.pengwz.dynamic.sql2.core.Fn;

import java.util.Collection;

public interface InsertParser {

    void insertSelective();

    <T, F> void insertSelective(Collection<Fn<T, F>> forcedFields);

    void insert();

    void batchInsert();

}
