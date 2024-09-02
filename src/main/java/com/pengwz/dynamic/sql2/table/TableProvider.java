package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class TableProvider {//NOSONAR

    private TableProvider() {
    }

    private static final Logger log = LoggerFactory.getLogger(TableProvider.class);

    private static final List<TableMeta> TABLE_META_LIST = new ArrayList<>();

    private static final TableProvider INSTANCE = new TableProvider();

    public static TableProvider getInstance() {
        return INSTANCE;
    }

    protected void saveTableMeta(TableMeta tableMeta) {
        String bindDataSourceName = tableMeta.getBindDataSourceName();
        String tableName = tableMeta.getTableName();
        String uniqueKey = bindDataSourceName + "." + tableName;
        boolean present = TABLE_META_LIST.stream().anyMatch(t -> {
            String uniKey = t.getBindDataSourceName() + "." + t.getTableName();
            return StringUtils.isEquals(uniqueKey, uniKey);
        });
        if (present) {
            log.warn("Data source '{}' has already added table '{}', skip adding this time",
                    bindDataSourceName, tableName);
            return;
        }
        TABLE_META_LIST.add(tableMeta);
    }

    protected boolean existTable(String tableName) {
        return TABLE_META_LIST.stream().anyMatch(t -> t.getTableName().equals(tableName));
    }
}
