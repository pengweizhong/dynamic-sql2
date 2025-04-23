package com.dynamic.sql.table;

import com.dynamic.sql.anno.Column;
import com.dynamic.sql.anno.GeneratedValue;
import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.View;
import com.dynamic.sql.context.SchemaContextHolder;
import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.enums.GenerationType;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.plugins.conversion.AttributeConverter;
import com.dynamic.sql.plugins.conversion.DefaultAttributeConverter;
import com.dynamic.sql.table.cte.CTEColumnMeta;
import com.dynamic.sql.table.cte.CTEEntityMapping;
import com.dynamic.sql.table.cte.CTEMeta;
import com.dynamic.sql.table.view.ViewColumnMeta;
import com.dynamic.sql.table.view.ViewMeta;
import com.dynamic.sql.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
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
//        if (packagePath == null || packagePath.length == 0) {
//            throw new IllegalArgumentException("The package path to search must be provided");
//        }
//        for (String path : packagePath) {
//            Map<Class<?>, ViewMeta> viewEntities = SchemaStructureScanner.findViewEntities(path);
//            viewEntities.forEach(TableProvider::saveViewMeta);
//        }
    }

    public static TableMeta parseTableClass(Class<?> tableClazz) {
        TableEntityMapping tableEntityMapping = SchemaStructureScanner.parseTableEntityMapping(tableClazz);
        if (tableEntityMapping == null) {
            return null;
        }
        TableMeta tableMeta = parseTableClass(tableEntityMapping).get(tableClazz);
        if (tableEntityMapping.isCache()) {
            TableProvider.saveTableMeta(tableClazz, tableMeta);
        }
        return tableMeta;
    }

    public static ViewMeta parseViewClass(Class<?> clazz) {
        List<Field> fields = ReflectUtils.getAllFields(clazz, filterFieldTypeRules());
        View view = clazz.getDeclaredAnnotation(View.class);
        boolean cache = false;
        String dataSourceName = null;
        if (null != view) {
            cache = view.isCache();
            dataSourceName = view.dataSourceName();
        }
//        final String finalDataSourceName = matchBestDataSourceName(clazz, dataSourceName);
        final String finalDataSourceName = dataSourceName;
        List<ColumnMetaSymbol> columnMetaSymbols = fields.stream().map(f -> parseTableColumn(finalDataSourceName, clazz, f))
                .filter(Objects::nonNull).collect(Collectors.toList());
        //检查列声明标识是否合规
        List<ColumnMeta> columnMetas = assertAndFilterColumn(columnMetaSymbols, clazz.getSimpleName());
        ViewMeta viewMeta = new ViewMeta();
        viewMeta.setBindDataSourceName(finalDataSourceName);
        List<ViewColumnMeta> viewColumnMetas = columnMetas.stream().map(cm -> {
            ViewColumnMeta viewColumnMeta = new ViewColumnMeta();
            viewColumnMeta.setColumnName(cm.getColumnName());
            viewColumnMeta.setField(cm.getField());
            viewColumnMeta.setConverter(cm.getConverter());
            return viewColumnMeta;
        }).collect(Collectors.toList());
        viewMeta.setViewColumnMetas(viewColumnMetas);
        if (cache) {
            TableProvider.saveViewMeta(clazz, viewMeta);
        }
        return viewMeta;
    }

    private static Map<Class<?>, TableMeta> parseTableClass(TableEntityMapping tableEntity) {
        log.trace("Parsing table class: {}", tableEntity);
        Class<?> entityClass = tableEntity.getEntityClass();
        List<Field> fields = ReflectUtils.getAllFields(entityClass, filterFieldTypeRules());
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
        List<ColumnMetaSymbol> columnMetaSymbols = fields.stream()
                .map(f -> parseTableColumn(tableEntity.getBindDataSourceName(), entityClass, f))
                .filter(Objects::nonNull).collect(Collectors.toList());
        //检查列声明标识是否合规
        List<ColumnMeta> columnMetas = assertAndFilterColumn(columnMetaSymbols, tableMeta.getTableName());
        tableMeta.setColumnMetas(columnMetas);
        return MapUtils.of(entityClass, tableMeta);
    }

    public static CTEMeta parseCTEClass(CTEEntityMapping cteEntityMapping) {
        CTEMeta cteMeta = new CTEMeta();
        cteMeta.setCteName(cteEntityMapping.getCteName());
        List<Field> fields = ReflectUtils.getAllFields(cteEntityMapping.getCteClass(), filterFieldTypeRules());
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

    private static ColumnMetaSymbol parseTableColumn(String dataSourceName, Class<?> entityClass, Field field) {
        ColumnMetaSymbol columnMetaSymbol = new ColumnMetaSymbol();
        ColumnMeta columnMeta = new ColumnMeta();
        columnMetaSymbol.setColumnMeta(columnMeta);
        columnMeta.setField(field);
        Column column = field.getDeclaredAnnotation(Column.class);
        String value = null;
        boolean primary = false;
        Class<? extends AttributeConverter> converter = null;
        if (column != null) {
            if (column.ignore()) {
                log.trace("Ignore mapping field '{}'", field.getName());
                return null;
            }
            value = column.value().trim();
            primary = column.primary();
            //格式化
            if (StringUtils.isNotBlank(column.format())) {
                columnMeta.setFormat(column.format());
            }
            if (!DefaultAttributeConverter.class.equals(column.converter())) {
                converter = column.converter();
            }
            columnMeta.setSrid(column.srid());
        }
        if (converter == null) {
            if (AttributeConverter.class.isAssignableFrom(field.getType())) {
                if (null == ConverterUtils.getCustomAttributeConverter(field.getType())) {
                    AttributeConverter instance;
                    if (field.getType().isEnum()) {
                        Object[] enumConstants = field.getType().getEnumConstants();
                        if (enumConstants == null || enumConstants.length == 0) {
                            throw new IllegalArgumentException("No enum constants found for class: " + field.getType().getCanonicalName());
                        }
                        instance = (AttributeConverter) enumConstants[0]; // 返回第一个枚举实例
                    } else {
                        instance = (AttributeConverter) ReflectUtils.instance(field.getType());
                    }
                    converter = instance.getClass();
                    ConverterUtils.putCustomAttributeConverter(converter, instance);
                }
            }
        }
        columnMetaSymbol.setPrimary(primary);
        String columnName;
        if (StringUtils.isBlank(value)) {
            columnName = NamingUtils.camelToSnakeCase(field.getName());
            //如果是Oracle 就转为大写。先写死逻辑，后面有需求在配置化 TODO
            if (StringUtils.isNotEmpty(dataSourceName)) {
                SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties(dataSourceName);
                if (schemaProperties.getSqlDialect() == SqlDialect.ORACLE) {
                    columnName = columnName.toUpperCase();
                }
            }
        } else {
            //如果是用户指定的，就保留原样 不做任何处置
            columnName = value;
        }
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
        columnMeta.setConverter(converter);
        //检测字段是否为基本类型
        if (log.isDebugEnabled() && field.getType().isPrimitive()) {
            log.warn("It is not recommended that the field type be a basic type," +
                            " because the basic type is not equal to null at any time. Field position: {}#{}",
                    entityClass.getName(), field.getName());
        }
        return columnMetaSymbol;
    }

    public static int[] excludeFieldTypes() {
        return new int[]{Modifier.STATIC, Modifier.FINAL, Modifier.NATIVE, Modifier.TRANSIENT};
    }

    public static Function<Field, Boolean> filterFieldTypeRules() {
        return field -> {
            return !Modifier.isFinal(field.getModifiers())
                    && !Modifier.isStatic(field.getModifiers())
                    && !Modifier.isNative(field.getModifiers())
                    && !Modifier.isTransient(field.getModifiers());
        };
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
