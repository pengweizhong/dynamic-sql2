package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.core.Version;
import com.dynamic.sql.enums.SqlDialect;

public class NestedMeta {
    private Version version;
    private String dataSourceName;
    private SqlDialect sqlDialect;

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    public void setSqlDialect(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
}
