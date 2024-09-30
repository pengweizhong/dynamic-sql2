package com.pengwz.dynamic.sql2.core.condition.impl.dialect;

import com.pengwz.dynamic.sql2.core.Version;

import java.util.Map;

public class OracleWhereCondition extends GenericWhereCondition {

    public OracleWhereCondition(Version version, Map<String, String> aliasTableMap) {
        super(version, aliasTableMap);
    }
}
