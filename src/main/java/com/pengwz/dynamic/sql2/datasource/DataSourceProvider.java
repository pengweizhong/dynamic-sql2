package com.pengwz.dynamic.sql2.datasource;

import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceProvider {//NOSONAR

    private static final DataSourceProvider INSTANCE = new DataSourceProvider();

    //key 是数据源名称
    private static final Map<String, DataSourceMeta> DATA_SOURCE_META_MAP = new ConcurrentHashMap<>();

    private DataSourceProvider() {
    }


    public static DataSourceMeta getDataSourceMeta(String dataSourceName) {
        if (DATA_SOURCE_META_MAP.isEmpty()) {
            return null;
        }
        return DATA_SOURCE_META_MAP.get(dataSourceName);
    }

    public static String getDataSourceName(DataSourceMeta dataSourceMeta) {
        if (DATA_SOURCE_META_MAP.isEmpty()) {
            return null;
        }
        for (Map.Entry<String, DataSourceMeta> entry : DATA_SOURCE_META_MAP.entrySet()) {
            if (entry.getValue().equals(dataSourceMeta)) {
                return entry.getKey();
            }
        }
        return null;
    }

    protected static synchronized void saveDataSourceMeta(String dataSourceName, DataSourceMeta dataSourceMeta) {
        if (dataSourceMeta == null) {
            throw new NullPointerException("dataSourceMeta is null");
        }
        DataSourceMeta exists = getDataSourceMeta(dataSourceName);
        if (exists != null) {
            throw new IllegalArgumentException("Duplicate datasource name: " + dataSourceName);
        }
        if (dataSourceMeta.isGlobalDefault()) {
            DataSourceMeta defaultDataSourceMeta = getDefaultDataSourceMeta();
            if (defaultDataSourceMeta != null) {
                throw new IllegalArgumentException("Duplicate default data sources are specified, " +
                        "namely '" + dataSourceName + "' and '" + getDefaultDataSourceName() + "'");
            }
        }
        DATA_SOURCE_META_MAP.put(dataSourceName, dataSourceMeta);
    }


    public static DataSourceMeta getDefaultDataSourceMeta() {
        if (DATA_SOURCE_META_MAP.isEmpty()) {
            return null;
        }
        for (DataSourceMeta dataSourceMeta : DATA_SOURCE_META_MAP.values()) {
            if (dataSourceMeta.isGlobalDefault()) {
                return dataSourceMeta;
            }
        }
        return null;
    }

    public static DataSourceMeta getDataSourceMeta(Class<?> tableClass) {
        TableMeta tableMeta = TableProvider.getTableMeta(tableClass);
        String bindDataSourceName = tableMeta.getBindDataSourceName();
        return DATA_SOURCE_META_MAP.get(bindDataSourceName);
    }

    public static boolean existDataSource(String dataSourceName) {
        return DATA_SOURCE_META_MAP.containsKey(dataSourceName);
    }

    public static Map<String, String[]> getDataSourceBoundPath() {
        HashMap<String, String[]> hashMap = new HashMap<>();
        DATA_SOURCE_META_MAP.forEach((key, value) -> {
            hashMap.put(key, value.getBindBasePackages());
        });
        return hashMap;
    }

    public static String getDefaultDataSourceName() {
        DataSourceMeta defaultDataSourceMeta = getDefaultDataSourceMeta();
        if (defaultDataSourceMeta == null) {
            return null;
        }
        return getDataSourceName(defaultDataSourceMeta);
    }
}

