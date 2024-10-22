package com.pengwz.dynamic.sql2.core.database;

import java.util.List;
import java.util.Map;

public interface SqlExecutor {

    List<Map<String, Object>> executeQuery();

//    int executeUpdate(String sql);
//
//    void execute(String sql);
}