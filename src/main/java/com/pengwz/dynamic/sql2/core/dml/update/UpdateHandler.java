package com.pengwz.dynamic.sql2.core.dml.update;

public interface UpdateHandler {

    int updateByPrimaryKey();

    int updateSelectiveByPrimaryKey();

    int update();

    int updateSelective();
}
