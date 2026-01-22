package com.dynamic.sql.plugins.logger;

import com.dynamic.sql.enums.DDLType;
import com.dynamic.sql.enums.DMLType;
import com.dynamic.sql.enums.SqlExecuteType;
import com.dynamic.sql.plugins.logger.type.CountSqlLogResult;
import com.dynamic.sql.plugins.logger.type.SelectSqlLogResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;

public class SqlLogResultResolver {
    private static final Logger log = LoggerFactory.getLogger(SqlLogResultResolver.class);

    public static SqlLogResult resolve(boolean enabled, SqlExecuteType sqlExecuteType, Object raw) {
        if (!enabled) {
            return null;
        }
        if (sqlExecuteType == DMLType.SELECT || sqlExecuteType instanceof DDLType) {
            if (raw instanceof Collection) {
                Collection collection = (Collection) raw;
                if (collection.size() == 1) {
                    Object next = collection.iterator().next();
                    if (next instanceof Map) {
                        Map<String, Object> map = (Map) next;
                        Map.Entry<String, Object> entry = map.entrySet().iterator().next();
                        if (entry.getKey().toLowerCase().startsWith("count(")) {
                            return new CountSqlLogResult(Integer.parseInt(entry.getValue().toString()));
                        }
                    }
                }
            }
            return new SelectSqlLogResult();
        }
        throw new UnsupportedOperationException("Unknown SQL type: " + sqlExecuteType);
    }
}
