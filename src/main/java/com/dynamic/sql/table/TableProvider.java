package com.dynamic.sql.table;

import com.dynamic.sql.table.cte.CTEMeta;
import com.dynamic.sql.table.view.ViewMeta;
import com.dynamic.sql.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableProvider {//NOSONAR

    private static final Logger log = LoggerFactory.getLogger(TableProvider.class);
    private static final Map<Class<?>, TableMeta> TABLE_META_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, CTEMeta> CTE_META_MAP = new ConcurrentHashMap<>();
    private static final Map<Class<?>, ViewMeta> VIEW_META_MAP = new ConcurrentHashMap<>();

    private TableProvider() {
    }

    protected static void saveTableMeta(Class<?> tableClass, TableMeta tableMeta) {
        if (TABLE_META_MAP.containsKey(tableClass)) {
            log.info("Data source '{}' has already added table '{}', skip adding this time",
                    tableMeta.getBindDataSourceName(), tableMeta.getTableName());
            return;
        }
        TABLE_META_MAP.put(tableClass, tableMeta);
    }

    public static TableMeta getTableMeta(Class<?> tableClass) {
        TableMeta tableMeta = TABLE_META_MAP.get(tableClass);
        if (tableMeta == null) {
            tableMeta = TableUtils.parseTableClass(tableClass);
            if (tableMeta == null) {
                return null;
            }
            saveTableMeta(tableClass, tableMeta);
        }
        return tableMeta;
    }

    public static TableMeta getTableMeta(String classCanonicalName) {
        for (Map.Entry<Class<?>, TableMeta> entry : TABLE_META_MAP.entrySet()) {
            if (entry.getKey().getCanonicalName().equals(classCanonicalName)) {
                return entry.getValue();
            }
        }
        Class<?> loadClass = ReflectUtils.loadClass(classCanonicalName);
        return getTableMeta(loadClass);
    }

    protected static void saveCTEMeta(Class<?> cteClass, CTEMeta cteMeta) {
        if (CTE_META_MAP.containsKey(cteClass)) {
            return;
        }
        CTE_META_MAP.put(cteClass, cteMeta);
    }

    public static CTEMeta getCTEMeta(Class<?> cteClass) {
        return CTE_META_MAP.get(cteClass);
    }

    protected static void saveViewMeta(Class<?> viewClass, ViewMeta viewMeta) {
        if (VIEW_META_MAP.containsKey(viewClass)) {
            log.info("Already added view '{}', skip adding this time", viewClass.getCanonicalName());
            return;
        }
        VIEW_META_MAP.put(viewClass, viewMeta);
    }

    public static ViewMeta getViewMeta(Class<?> viewClass) {
        ViewMeta viewMeta = VIEW_META_MAP.get(viewClass);
        if (viewMeta == null) {
            viewMeta = TableUtils.parseViewClass(viewClass);
            saveViewMeta(viewClass, viewMeta);
        }
        return viewMeta;
    }
}
