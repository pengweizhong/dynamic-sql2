package com.pengwz.dynamic.sql2.core.database.parser;

import com.pengwz.dynamic.sql2.core.Fn;

public interface UpdateParser {
    void updateByPrimaryKey();

    void updateSelectiveByPrimaryKey();

    void updateSelectiveByPrimaryKey(Fn<?, ?>[] forcedFields);
}
