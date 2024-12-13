package com.dynamic.sql.core.dml.update;

public interface UpdateHandler {

    int updateByPrimaryKey();

    int updateSelectiveByPrimaryKey();

    int update();

    int updateSelective();
}
