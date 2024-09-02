package com.pengwz.dynamic.sql2.datasource;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DataSourceFactory {//NOSONAR

    private static final DataSourceFactory INSTANCE = new DataSourceFactory();

    private static final List<DataSourceMeta> DATA_SOURCE_META_LIST = Collections.synchronizedList(new ArrayList<>());

    private DataSourceFactory() {
    }

    protected static DataSourceFactory getInstance() {
        return INSTANCE;
    }

    protected DataSourceMeta getDataSourceMeta(String dataSourceName) {
        if (DATA_SOURCE_META_LIST.isEmpty()) {
            return null;
        }
        for (DataSourceMeta dataSourceMeta : DATA_SOURCE_META_LIST) {
            if (dataSourceMeta.getDataSourceName().equals(dataSourceName)) {
                return dataSourceMeta;
            }
        }
        return null;
    }

    protected void setDataSourceMeta(DataSourceMeta dataSourceMeta) {
        if (dataSourceMeta == null) {
            throw new NullPointerException("dataSourceMeta is null");
        }
        String dataSourceName = dataSourceMeta.getDataSourceName();
        DataSourceMeta exists = getDataSourceMeta(dataSourceName);
        if (exists != null) {
            throw new IllegalArgumentException("Duplicate datasource name: " + dataSourceName);
        }
        if (dataSourceMeta.isGlobalDefault()) {
            DataSourceMeta defaultDataSourceMeta = getDefaultDataSourceMeta();
            if (defaultDataSourceMeta != null) {
                throw new IllegalArgumentException("Duplicate default data sources are specified, " +
                        "namely '" + dataSourceName + "' and '" + defaultDataSourceMeta.getDataSourceName() + "'");
            }
        }
        DATA_SOURCE_META_LIST.add(dataSourceMeta);
    }

    protected String matchDataSourceName(DataSource dataSource) {
        if (DATA_SOURCE_META_LIST.isEmpty()) {
            return null;
        }
        for (int i = 0; i < DATA_SOURCE_META_LIST.size(); i++) {
            DataSourceMeta dataSourceMeta = DATA_SOURCE_META_LIST.get(i);
            if (dataSourceMeta.getDataSource().equals(dataSource)) {
                return dataSourceMeta.getDataSourceName();
            }
        }
        return null;
    }

    protected DataSourceMeta getDefaultDataSourceMeta() {
        if (DATA_SOURCE_META_LIST.isEmpty()) {
            return null;
        }
        for (DataSourceMeta dataSourceMeta : DATA_SOURCE_META_LIST) {
            if (dataSourceMeta.isGlobalDefault()) {
                return dataSourceMeta;
            }
        }
        return null;
    }
}

