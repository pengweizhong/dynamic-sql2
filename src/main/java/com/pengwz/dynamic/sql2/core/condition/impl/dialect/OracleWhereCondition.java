package com.pengwz.dynamic.sql2.core.condition.impl.dialect;

import com.pengwz.dynamic.sql2.core.Version;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

import java.util.Map;

public class OracleWhereCondition extends GenericWhereCondition {

    public OracleWhereCondition(Version version, Map<String, String> aliasTableMap, String dataSourceName) {
        super(version, aliasTableMap, dataSourceName);
    }

    @Override
    protected SqlDialect sqlDialect() {
        return SqlDialect.ORACLE;
    }
}
