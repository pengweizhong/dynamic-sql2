package com.pengwz.dynamic.sql2.core.database.parser;

public interface UpdateParser {
    void updateByPrimaryKey();

    void updateSelectiveByPrimaryKey();
}
