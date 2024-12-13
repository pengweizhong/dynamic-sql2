package com.dynamic.sql.core.database.parser;


import com.dynamic.sql.core.Fn;

public interface UpdateParser {
    void updateByPrimaryKey();

    void updateSelectiveByPrimaryKey(Fn<?, ?>[] forcedFields);

    void update();

    void updateSelective(Fn<?, ?>[] forcedFields);

    void upsertSelective(Fn<?, ?>[] forcedFields);
}
