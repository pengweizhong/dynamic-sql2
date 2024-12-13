package com.dynamic.sql.core.dml.select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SelectHandler {

    List<Map<String, Object>> executeQuery();

    <T> T selectByPrimaryKey(Class<T> entityClass, Object pkValue);

    <T> List<T> selectByPrimaryKey(Class<T> entityClass, Collection<?> pkValues);
}
