package com.pengwz.dynamic.sql2.table;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TableUtils {
    private TableUtils() {
    }

    private static final Logger log = LoggerFactory.getLogger(TableUtils.class);

    public static void scanAndInitTable(String... packagePath) {
        if (packagePath == null || packagePath.length == 0) {
            throw new IllegalArgumentException("The package path to search must be provided");
        }
        List<TableEntityMapping> tableEntities = new ArrayList<>();
        for (String path : packagePath) {
            tableEntities.addAll(TableEntityScanner.findTableEntities(path));
        }
        if (tableEntities.isEmpty()) {
            log.debug("No table entities were detected");
            return;
        }
//        tableEntities.stream().map(tableEntity -> {
//            //反射获取字段成员信息
//          return  parseTableClass(tableEntity);
//        });
//
//        ReflectUtils.getAllFields()

    }

    private static Object parseTableClass(TableEntityMapping tableEntity) {
        return null;
    }

    public static synchronized void initTable(String[] bindBasePackages) {

    }

    private static int[] excludeFieldTypes() {
        return new int[]{Modifier.STATIC, Modifier.FINAL};
    }
}
