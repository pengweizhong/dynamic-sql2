package com.dynamic.sql.core.ddl;

import com.dynamic.sql.datasource.DataSourceProvider;

public class MetaDataHelper {
    private final String dataSourceName;
    private final String catalog;

    public MetaDataHelper(String dataSourceName, String catalog) {
        this.dataSourceName = dataSourceName == null ? DataSourceProvider.getDefaultDataSourceName() : dataSourceName;
        this.catalog = catalog;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public String getCatalog() {
        return catalog;
    }

}
