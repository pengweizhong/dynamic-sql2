package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.table.cte.CTEMeta;
import com.pengwz.dynamic.sql2.table.view.ViewMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableProvider {//NOSONAR

    private static final Logger log = LoggerFactory.getLogger(TableProvider.class);
    private static final Map<Class<?>, TableMeta> TABLE_META_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, CTEMeta> CTE_META_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, ViewMeta> VIEW_META_MAP = new ConcurrentHashMap<>();
    private static final TableProvider INSTANCE = new TableProvider();

    private TableProvider() {
    }

    public static TableProvider getInstance() {
        return INSTANCE;
    }


    protected void saveTableMeta(Class<?> tableClass, TableMeta tableMeta) {
        if (TABLE_META_MAP.containsKey(tableClass)) {
            log.warn("Data source '{}' has already added table '{}', skip adding this time",
                    tableMeta.getBindDataSourceName(), tableMeta.getTableName());
            return;
        }
        TABLE_META_MAP.put(tableClass, tableMeta);
    }

    public TableMeta getTableMeta(Class<?> tableClass) {
        return TABLE_META_MAP.get(tableClass);
    }

    protected void saveCTEMeta(Class<?> cteClass, CTEMeta cteMeta) {
        if (CTE_META_MAP.containsKey(cteClass)) {
            return;
        }
        CTE_META_MAP.put(cteClass, cteMeta);
    }

    public CTEMeta getCTEMeta(Class<?> cteClass) {
        return CTE_META_MAP.get(cteClass);
    }

    protected void saveViewMeta(Class<?> viewClass, ViewMeta viewMeta) {
        if (VIEW_META_MAP.containsKey(viewClass)) {
            return;
        }
        VIEW_META_MAP.put(viewClass, viewMeta);
    }

    public ViewMeta getViewMeta(Class<?> viewClass) {
        return VIEW_META_MAP.get(viewClass);
    }
}
