package com.dynamic.sql.core.dml;

import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.enums.SqlExecuteType;
import com.dynamic.sql.utils.SqlUtils;

import java.util.function.Consumer;

public abstract class ParseWhereHandler implements SqlExecuteType {
    protected final Consumer<GenericWhereCondition> condition;

    protected ParseWhereHandler(Consumer<GenericWhereCondition> condition) {
        this.condition = condition;
    }

    protected GenericWhereCondition applyGenericWhereCondition(SchemaProperties schemaProperties) {
        GenericWhereCondition whereCondition = null;
        if (condition != null) {
            Version version = new Version(schemaProperties.getMajorVersionNumber(),
                    schemaProperties.getMinorVersionNumber(), schemaProperties.getPatchVersionNumber());
            whereCondition = SqlUtils.matchDialectCondition(schemaProperties.getSqlDialect(),
                    version, null, schemaProperties.getDataSourceName());
            whereCondition.setSqlExecuteType(this);
            condition.accept(whereCondition);
        }
        return whereCondition;
    }
}
