package com.dynamic.sql.plugins.logger.impl;

import com.dynamic.sql.context.properties.SqlLogProperties;
import com.dynamic.sql.plugins.logger.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.dynamic.sql.plugins.debugs.SqlDebugger.printSql;

public class DefaultSqlLogger extends AbstractSqlLog implements SqlLogger {

    @Override
    public void beforeSql(SqlLogProperties props, SqlLogContext ctx) {
        if (!props.isEnabled()) {
            return;
        }
        if (!ctx.isIntercepted()) {
            printSql(props.getLevel(), "{} -->       !!!!!! : SQL is intercepted.", getPrintDataSourceName(props, ctx));
        }
        printSql(props.getLevel(), "{} -->     Preparing: {}", getPrintDataSourceName(props, ctx), ctx.getPreparedSql().getSql());
        if (props.isPrintParameters()) {
            List<List<Object>> batchParams = ctx.getPreparedSql().getBatchParams();
            for (List<Object> batchParam : batchParams) {
                printSql(props.getLevel(), "{} -->    Parameters: {}", getPrintDataSourceName(props, ctx), assemblyParameters(batchParam));
            }
        }
    }

    @Override
    public void afterSql(SqlLogProperties props, SqlLogContext ctx) {
        if (!props.isEnabled()) {
            return;
        }
        SqlLogResult resolve = SqlLogResultResolver.resolve(props.isEnabled(), ctx.getSqlExecuteType(), ctx.getRawResult());
        //没有解析出来结果就不打印了
        if (resolve == null) {
            return;
        }
        resolve.afterLog(props, ctx);
    }

    //    @Override
//    public void logSql(SchemaProperties.SqlLogProperties printSqlProperties, PreparedSql preparedSql, String dataSourceName, boolean isIntercepted) {
//        if (!log.isDebugEnabled() || !printSqlProperties.isPrintSql()) {
//            return;
//        }
//        if (!printSqlProperties.isPrintDataSourceName()) {
//            dataSourceName = "";
//        }
//        log.debug("{} -->     Preparing: {}", dataSourceName, preparedSql.getSql());
//        List<List<Object>> batchParams = preparedSql.getBatchParams();
//        //
//        for (List<Object> batchParam : batchParams) {
//            log.debug("{} -->    Parameters: {}", dataSourceName, assemblyParameters(batchParam));
//        }
//        if (!isIntercepted) {
//            log.debug("{} -->       !!!!!! : SQL is intercepted.", dataSourceName);
//        }
//    }
//
//    @Override
//    @SuppressWarnings("unchecked")
//    public void logResult(SchemaProperties.SqlLogProperties printSqlProperties, SqlExecuteType sqlExecuteType, String dataSourceName, Object applyResult) {
//        if (!log.isDebugEnabled() || !printSqlProperties.isPrintSql()) {
//            return;
//        }
//        if (!printSqlProperties.isPrintDataSourceName()) {
//            dataSourceName = "";
//        }
//        if (sqlExecuteType == DMLType.SELECT || sqlExecuteType instanceof DDLType) {
//            if (applyResult instanceof Collection) {
//                Collection collection = (Collection) applyResult;
//                if (collection.size() == 1) {
//                    Object next = collection.iterator().next();
//                    if (next instanceof Map) {
//                        Map<String, Object> map = (Map) next;
//                        Map.Entry<String, Object> entry = map.entrySet().iterator().next();
//                        if (entry.getKey().equalsIgnoreCase("count(1)")) {
//                            log.debug("{} <--         Count: {}", dataSourceName, entry.getValue());
//                            return;
//                        }
//                    }
//                }
//                log.debug("{} <--         Total: {}", dataSourceName, collection.size());
//            } else {
//                log.debug("{} <--     Returned: {}", dataSourceName, applyResult);
//            }
//        }
//        if (sqlExecuteType == DMLType.INSERT || sqlExecuteType == DMLType.UPDATE || sqlExecuteType == DMLType.DELETE) {
//            log.debug("{} <-- Affected Rows: {}", dataSourceName, applyResult);
//        }
//    }
//
    private static StringBuilder assemblyParameters(List<Object> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            stringBuilder.append(param);
            if (param != null) {
                stringBuilder.append("(").append(param.getClass().getSimpleName()).append(")");
            }
            if (i != params.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder;
    }
}
