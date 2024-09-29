package com.pengwz.dynamic.sql2.core.condition.impl.dialect;

import com.pengwz.dynamic.sql2.core.Version;

public class MysqlWhereCondition extends GenericWhereCondition {

    public MysqlWhereCondition(Version version, String dataSourceName) {
        super(version, dataSourceName);
    }
}
