/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.datasource;


import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DataSourceProvider {//NOSONAR
    private static final Logger log = LoggerFactory.getLogger(DataSourceProvider.class);
    //key 是数据源名称
    private static final Map<String, DataSourceMeta> DATA_SOURCE_META_MAP = new ConcurrentHashMap<>();

    private DataSourceProvider() {
    }


    public static DataSourceMeta getDataSourceMeta(String dataSourceName) {
        if (DATA_SOURCE_META_MAP.isEmpty() || dataSourceName == null) {
            return null;
        }
        return DATA_SOURCE_META_MAP.get(dataSourceName);
    }

    public static List<String> getDataSourceNameList() {
        if (DATA_SOURCE_META_MAP.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(DATA_SOURCE_META_MAP.keySet());
    }

    public static List<DataSourceMeta> getDataSourceMetaList() {
        if (DATA_SOURCE_META_MAP.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(DATA_SOURCE_META_MAP.values());
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
        saveDataSourceMeta(false, dataSourceName, dataSourceMeta);
    }

    protected static synchronized void saveDataSourceMeta(boolean allowDataSourceDefinitionOverriding, String dataSourceName, DataSourceMeta dataSourceMeta) {
        if (dataSourceMeta == null) {
            throw new NullPointerException("dataSourceMeta is null");
        }
        DataSourceMeta exists = getDataSourceMeta(dataSourceName);
        if (exists != null) {
            if (allowDataSourceDefinitionOverriding) {
                log.info("Override data source: {}", dataSourceName);
            } else {
                throw new IllegalArgumentException("Duplicate datasource name: " + dataSourceName);
            }
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

