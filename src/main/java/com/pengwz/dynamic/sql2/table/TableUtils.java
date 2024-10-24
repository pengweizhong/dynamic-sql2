package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.anno.Column;
import com.pengwz.dynamic.sql2.anno.GeneratedValue;
import com.pengwz.dynamic.sql2.anno.Id;
import com.pengwz.dynamic.sql2.enums.GenerationType;
import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.plugins.conversion.DefaultAttributeConverter;
import com.pengwz.dynamic.sql2.table.cte.CTEColumnMeta;
import com.pengwz.dynamic.sql2.table.cte.CTEEntityMapping;
import com.pengwz.dynamic.sql2.table.cte.CTEMeta;
import com.pengwz.dynamic.sql2.table.view.ViewMeta;
import com.pengwz.dynamic.sql2.utils.*;
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
    private static final Logger log = LoggerFactory.getLogger(TableUtils.class);

    private TableUtils() {
    }

    public static void scanAndInitTable(String... packagePath) {
        if (packagePath == null || packagePath.length == 0) {
            throw new IllegalArgumentException("The package path to search must be provided");
        }
        List<TableEntityMapping> tableEntities = new ArrayList<>();
        for (String path : packagePath) {
            tableEntities.addAll(SchemaStructureScanner.findTableEntities(path));
        }
        if (tableEntities.isEmpty()) {
            log.info("No table entities were detected");
            return;
        }
        tableEntities.stream().filter(te -> {
            if (log.isTraceEnabled() && !te.isCache()) {
                log.trace("Table name '{}' does not need to be cached", te.getTableName());
            }
            return te.isCache();
        }).map(TableUtils::parseTableClass).forEach(metaMap -> metaMap.forEach(TableProvider::saveTableMeta));
    }

    public static void scanAndInitCTETableInfo(String... packagePath) {
        if (packagePath == null || packagePath.length == 0) {
            throw new IllegalArgumentException("The package path to search must be provided");
        }
        for (String path : packagePath) {
            List<CTEEntityMapping> cteEntities = SchemaStructureScanner.findCTEEntities(path);
            if (CollectionUtils.isNotEmpty(cteEntities)) {
                cteEntities.forEach(cte -> {
                    if (!cte.isCache()) {
                        return;
                    }
                    TableProvider.saveCTEMeta(cte.getCteClass(), parseCTEClass(cte));
                });
            }
        }
    }

    public static void scanAndInitViewInfo(String... packagePath) {
        if (packagePath == null || packagePath.length == 0) {
            throw new IllegalArgumentException("The package path to search must be provided");
        }
        for (String path : packagePath) {
            Map<Class<?>, ViewMeta> viewEntities = SchemaStructureScanner.findViewEntities(path);
            viewEntities.forEach(TableProvider::saveViewMeta);
        }
    }

    public static TableMeta parseTableClass(Class<?> tableClazz) {
        TableEntityMapping tableEntityMapping = SchemaStructureScanner.parseTableEntityMapping(tableClazz);
        return parseTableClass(tableEntityMapping).get(tableClazz);
    }

    public static List<ColumnMeta> parseViewClass(Class<?> clazz) {

        List<Field> fields = ReflectUtils.getAllFields(clazz, excludeFieldTypes());
        List<ColumnMetaSymbol> columnMetaSymbols = fields.stream().map(f -> parseTableColumn(clazz, f))
                .filter(Objects::nonNull).collect(Collectors.toList());
        //检查列声明标识是否合规
        return assertAndFilterColumn(columnMetaSymbols, clazz.getSimpleName());
    }

    private static Map<Class<?>, TableMeta> parseTableClass(TableEntityMapping tableEntity) {
        log.trace("Parsing table class: {}", tableEntity);
        Class<?> entityClass = tableEntity.getEntityClass();
        List<Field> fields = ReflectUtils.getAllFields(entityClass, excludeFieldTypes());
        TableMeta tableMeta = new TableMeta();
        tableMeta.setTableName(tableEntity.getTableName());
        if (StringUtils.isBlank(tableMeta.getTableName())) {
            throw new IllegalArgumentException("The table name is empty");
        }
        tableMeta.setTableAlias(tableEntity.getTableAlias());
        if (StringUtils.isBlank(tableMeta.getTableAlias())) {
            throw new IllegalArgumentException("The table alias is empty");
        }
        tableMeta.setBindDataSourceName(tableEntity.getBindDataSourceName());
        List<ColumnMetaSymbol> columnMetaSymbols = fields.stream().map(f -> parseTableColumn(entityClass, f))
                .filter(Objects::nonNull).collect(Collectors.toList());
        //检查列声明标识是否合规
        List<ColumnMeta> columnMetas = assertAndFilterColumn(columnMetaSymbols, tableMeta.getTableName());
        tableMeta.setColumnMetas(columnMetas);
        return MapUtils.of(entityClass, tableMeta);
    }

    public static CTEMeta parseCTEClass(CTEEntityMapping cteEntityMapping) {
        CTEMeta cteMeta = new CTEMeta();
        cteMeta.setCteName(cteEntityMapping.getCteName());
        List<Field> fields = ReflectUtils.getAllFields(cteEntityMapping.getCteClass(), excludeFieldTypes());
        List<CTEColumnMeta> cteColumnMetas = fields.stream().map(field -> {
            Column column = field.getDeclaredAnnotation(Column.class);
            String columnValue = column != null ? column.value() : null;
            String columnName = NamingUtils.camelToSnakeCase(StringUtils.isBlank(columnValue) ? field.getName() : columnValue);
            CTEColumnMeta meta = new CTEColumnMeta();
            meta.setColumnName(columnName);
            meta.setField(field);
            return meta;
        }).collect(Collectors.toList());
        cteMeta.setCteColumnMetas(cteColumnMetas);
        return cteMeta;
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

    private static ColumnMetaSymbol parseTableColumn(Class<?> entityClass, Field field) {
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
        //是否使用了自定义转换
        Class<? extends AttributeConverter> converter = column == null ? null : column.converter();
        if (converter != null && !DefaultAttributeConverter.class.equals(converter)) {
            columnMeta.setConverter(converter);
        }
        //检测字段是否为基本类型
        if (log.isDebugEnabled() && field.getType().isPrimitive()) {
            log.debug("It is not recommended that the field type be a basic type," +
                            " because the basic type is not equal to null at any time. Field position: {}#{}",
                    entityClass.getName(), field.getName());
        }
        return columnMetaSymbol;
    }

    public static int[] excludeFieldTypes() {
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
