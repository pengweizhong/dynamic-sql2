package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.anno.Column;
import com.pengwz.dynamic.sql2.anno.GeneratedValue;
import com.pengwz.dynamic.sql2.anno.Id;
import com.pengwz.dynamic.sql2.enums.GenerationType;
import com.pengwz.dynamic.sql2.utils.NamingUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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
            log.info("No table entities were detected");
            return;
        }
        List<TableEntityMapping> collected = tableEntities.stream().filter(te -> {
            if (log.isTraceEnabled() && !te.isCache()) {
                log.trace("Table name '{}' does not need to be cached", te.getTableName());
            }
            return te.isCache();
        }).collect(Collectors.toList());
        //反射获取字段成员信息
        List<TableMeta> tableMetas = collected.stream().map(TableUtils::parseTableClass)
                .collect(Collectors.toList());
        tableMetas.forEach(TableUtils::initTable);
    }

    protected static synchronized void initTable(TableMeta tableMeta) {
        log.trace("Initialize '{}' into the cache", tableMeta.getTableName());
        TableProvider.getInstance().saveTableMeta(tableMeta);
    }

    private static TableMeta parseTableClass(TableEntityMapping tableEntity) {
        log.trace("Parsing table class: {}", tableEntity);
        Class<?> entityClass = tableEntity.getEntityClass();
        List<Field> fields = ReflectUtils.getAllFields(entityClass, excludeFieldTypes());
        TableMeta tableMeta = new TableMeta();
        tableMeta.setTableName(tableEntity.getTableName());
        tableMeta.setBindDataSourceName(tableEntity.getBindDataSourceName());
        List<ColumnMetaSymbol> columnMetaSymbols = fields.stream().map(TableUtils::parseTableColumn)
                .filter(Objects::nonNull).collect(Collectors.toList());
        //检查列声明标识是否合规
        List<ColumnMeta> columnMetas = assertAndFilterColumn(columnMetaSymbols, tableMeta.getTableName());
        tableMeta.setColumnMetas(columnMetas);
        return tableMeta;
    }

    private static List<ColumnMeta> assertAndFilterColumn(List<ColumnMetaSymbol> columnMetaSymbols, String tableName) {
        List<ColumnMeta> columnMetas = new ArrayList<>();
        //校验列名的重复
        Map<String, List<ColumnMeta>> groupByColumnNameMap = columnMetaSymbols.stream().map(ColumnMetaSymbol::getColumnMeta)
                .collect(Collectors.groupingBy(ColumnMeta::getColumnName));
        //获取唯一项
        List<ColumnMeta> primarySymbol = columnMetaSymbols.stream().filter(ColumnMetaSymbol::isPrimary)
                .map(ColumnMetaSymbol::getColumnMeta).collect(Collectors.toList());
        groupByColumnNameMap.forEach((columnName, conflictColumnMetas) -> {
            if (conflictColumnMetas.size() == 1) {
                columnMetas.add(conflictColumnMetas.get(0));
                return;
            }
            //如果列名冲突，则判断是否声明了唯一
            if (primarySymbol.isEmpty()) {
                throw new IllegalArgumentException("Column name '" + columnName + "' is created repeatedly " +
                        "in table '" + tableName + "'! You need to specify the primary identifier or delete the conflicting field");
            }
            List<ColumnMeta> primaryMetas = conflictColumnMetas.stream()
                    .filter(primarySymbol::contains).collect(Collectors.toList());
            if (primaryMetas.size() > 1) {
                throw new IllegalArgumentException("The column name '" + columnName + "'" +
                        " is repeated in the table '" + tableName + "' " +
                        "and is declared unique! Only one primary identifier can be retained " +
                        "or the conflicting field can be deleted.");
            }
            columnMetas.add(primaryMetas.get(0));
        });
        //判断是否又多个主键声明
        List<ColumnMeta> ids = columnMetas.stream().filter(ColumnMeta::isPrimary).collect(Collectors.toList());
        if (ids.size() > 1) {
            throw new IllegalArgumentException("Duplicate primary key ID in the '" + tableName + "' table! Only one ID can be reserved");
        }
        return columnMetas;
    }

    private static ColumnMetaSymbol parseTableColumn(Field field) {
        ColumnMetaSymbol columnMetaSymbol = new ColumnMetaSymbol();
        ColumnMeta columnMeta = new ColumnMeta();
        columnMetaSymbol.setColumnMeta(columnMeta);
        columnMeta.setField(field);
        Column column = field.getDeclaredAnnotation(Column.class);
        String value = null;
        boolean primary = false;
        if (column != null) {
            if (column.ignore()) {
                log.trace("Ignore mapping field '{}'", field.getName());
                return null;
            }
            value = column.value().trim();
            primary = column.primary();
        }
        columnMetaSymbol.setPrimary(primary);
        String columnName = NamingUtils.camelToSnakeCase(StringUtils.isBlank(value) ? field.getName() : value);
        columnMeta.setColumnName(columnName);
        columnMeta.setPrimary(field.getDeclaredAnnotation(Id.class) != null);
        if (columnMeta.isPrimary()) {
            GeneratedValue generatedValue = field.getDeclaredAnnotation(GeneratedValue.class);
            if (generatedValue != null) {
                GeneratedStrategy generatedStrategy = new GeneratedStrategy();
                generatedStrategy.setStrategy(generatedValue.strategy());
                if (generatedValue.strategy().equals(GenerationType.SEQUENCE)) {
                    generatedStrategy.setSequenceName(generatedStrategy.sequenceName);
                }
                columnMeta.setGeneratedStrategy(generatedStrategy);
            }
        }
        return columnMetaSymbol;
    }


    private static int[] excludeFieldTypes() {
        return new int[]{Modifier.STATIC, Modifier.FINAL};
    }

    static class ColumnMetaSymbol {
        ColumnMeta columnMeta;
        boolean primary;

        public ColumnMeta getColumnMeta() {
            return columnMeta;
        }

        public void setColumnMeta(ColumnMeta columnMeta) {
            this.columnMeta = columnMeta;
        }

        public boolean isPrimary() {
            return primary;
        }

        public void setPrimary(boolean primary) {
            this.primary = primary;
        }
    }
}
