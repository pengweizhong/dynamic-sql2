package com.pengwz.dynamic.sql2.core.database;

import com.pengwz.dynamic.sql2.enums.DMLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SqlDebugger {
    private static final Logger log = LoggerFactory.getLogger(SqlDebugger.class);

    private SqlDebugger() {
    }

    public static void debug(PreparedSql preparedSql, String dataSourceName, boolean isIntercepted) {
        if (!log.isDebugEnabled()) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        List<Object> params = preparedSql.getParams();
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            stringBuilder.append(param).append("(").append(param.getClass().getSimpleName()).append(")");
            if (i != params.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        log.debug("{} -->     Preparing: {}", dataSourceName, preparedSql.getSql());
        log.debug("{} -->    Parameters: {}", dataSourceName, stringBuilder);
        if (!isIntercepted) {
            log.debug("{} -->       !!!!!! : SQL is intercepted.", dataSourceName);
        }
    }

    /*
     *
     *
     *
     * 这么注释其实就是想让LOG输出在我想要的行号上。。。
     * 所以故意跨域了很多行。。。
     * 包括方法名也是特意做的对其。。。
     *
     */

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void debug(DMLType dmlType, String dataSourceName, Object applyResult) {//NOSONAR
        if (!log.isDebugEnabled()) {
            return;
        }
        if (dmlType == DMLType.SELECT) {
            if (applyResult instanceof Collection) {
                Collection collection = (Collection) applyResult;
                if (collection.size() == 1) {
                    Object next = collection.iterator().next();
                    if (next instanceof Map) {
                        Map<String, Object> map = (Map) next;
                        Map.Entry<String, Object> entry = map.entrySet().iterator().next();
                        if (entry.getKey().equalsIgnoreCase("count(1)")) {
                            log.debug("{} <--         Count: {}", dataSourceName, entry.getValue());
                            return;
                        }
                    }
                }
                log.debug("{} <--         Total: {}", dataSourceName, collection.size());
            } else {
                log.debug("{} <--     Returned: {}", dataSourceName, applyResult);
            }
        }
        if (dmlType == DMLType.INSERT || dmlType == DMLType.UPDATE || dmlType == DMLType.DELETE) {
            log.debug("{} <-- Affected Rows: {}", dataSourceName, applyResult);
        }
    }
}
