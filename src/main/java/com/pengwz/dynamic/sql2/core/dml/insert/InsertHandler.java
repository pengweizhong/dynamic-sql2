package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.core.Fn;

import java.util.Collection;

public interface InsertHandler {

    int insertSelective();

    int insert();

    int batchInsert();

}
