package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.core.Fn;

import java.util.Collection;

public interface InsertHandler {

    int insertSelective();

    <T, F> int insertSelective(Collection<Fn<T, F>> forcedFields);

    int insert();

    int batchInsert();

}
