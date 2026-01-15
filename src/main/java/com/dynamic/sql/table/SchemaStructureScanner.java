package com.dynamic.sql.table;

import com.dynamic.sql.anno.CTETable;
import com.dynamic.sql.anno.Column;
import com.dynamic.sql.anno.Table;
import com.dynamic.sql.anno.View;
import com.dynamic.sql.datasource.DataSourceProvider;
import com.dynamic.sql.plugins.resolve.ValueParserManager;
import com.dynamic.sql.table.cte.CTEEntityMapping;
import com.dynamic.sql.table.view.ViewColumnMeta;
import com.dynamic.sql.table.view.ViewMeta;
import com.dynamic.sql.utils.NamingUtils;
import com.dynamic.sql.utils.ReflectUtils;
import com.dynamic.sql.utils.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static com.dynamic.sql.table.TableUtils.filterFieldTypeRules;


public class SchemaStructureScanner {
    private static final Logger log = LoggerFactory.getLogger(SchemaStructureScanner.class);

    private SchemaStructureScanner() {
    }

    /**
     * 检索表实体类
     *
     * @param forPackage 检索基本路径
     */
    public static List<TableEntityMapping> findTableEntities(String forPackage) {
        log.debug("Find the table entities based on the provided '{}' path. ", forPackage);
        Reflections reflections = getReflections(forPackage);
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(Table.class);
        List<TableEntityMapping> tableEntityMappings = new ArrayList<>();
        try {
            for (Class<?> annotatedClass : annotatedClasses) {
                tableEntityMappings.add(parseTableEntityMapping(annotatedClass));
            }
        } catch (Exception e) {
            throw new IllegalStateException("An exception occurred while retrieving entities.", e);
        }
        return tableEntityMappings;
    }

    public static List<CTEEntityMapping> findCTEEntities(String forPackage) {
        Reflections reflections = getReflections(forPackage);
        return reflections.getTypesAnnotatedWith(CTETable.class).stream().map(cte -> {
            CTETable cteTable = cte.getDeclaredAnnotation(CTETable.class);
            CTEEntityMapping cteEntityMapping = new CTEEntityMapping();
            cteEntityMapping.setCteName(cteTable.value());
            cteEntityMapping.setCache(cteTable.isCache());
            cteEntityMapping.setCteClass(cte);
            return cteEntityMapping;
        }).collect(Collectors.toList());
    }

    public static Map<Class<?>, ViewMeta> findViewEntities(String forPackage) {
        Reflections reflections = getReflections(forPackage);
        Map<Class<?>, ViewMeta> viewMap = new HashMap<>();
        reflections.getTypesAnnotatedWith(View.class).forEach(cls -> {
            ViewMeta viewMeta = new ViewMeta();
            List<Field> allFields = ReflectUtils.getAllFields(cls, filterFieldTypeRules());
            List<ViewColumnMeta> viewColumnMetas = allFields.stream().map(field -> {
                Column column = field.getDeclaredAnnotation(Column.class);
                String columnValue = column != null ? column.value() : null;
                String columnName = NamingUtils.camelToSnakeCase(StringUtils.isBlank(columnValue) ? field.getName() : columnValue);
                ViewColumnMeta viewColumnMeta = new ViewColumnMeta();
                viewColumnMeta.setColumnName(columnName);
                viewColumnMeta.setField(field);
                return viewColumnMeta;
            }).collect(Collectors.toList());
            viewMeta.setViewColumnMetas(viewColumnMetas);
            viewMap.put(cls, viewMeta);
        });
        return viewMap;
    }

    private static Reflections getReflections(String forPackage) {
        return new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(forPackage))
                //只关心指定包
                .filterInputsBy(new FilterBuilder().includePackage(forPackage))
                .setScanners(Scanners.TypesAnnotated));
    }

    /**
     * 按照优先级匹配绑定的数据源
     *
     * @param annotatedClass       表实体
     * @param inLineDataSourceName 内联数据源
     * @return 匹配到绑定的数据源
     */
    public static String matchBestDataSourceName(Class<?> annotatedClass, String inLineDataSourceName) {
        if (StringUtils.isNotBlank(inLineDataSourceName)) {
            if (!DataSourceProvider.existDataSource(inLineDataSourceName)) {
                throw new IllegalArgumentException("DataSource '" + inLineDataSourceName + "' does not exist. " +
                        "Please check that the data source has been initialized correctly");
            }
            return inLineDataSourceName;
        }
        Map<String, String[]> bindPath = DataSourceProvider.getDataSourceBoundPath();
        if (bindPath.isEmpty()) {
            throw new NoSuchElementException("No data source has been initialized");
        }
        //检查表前缀是否匹配
        String thisTablePath = annotatedClass.getCanonicalName();
        Map<String, String> bestMatchMap = new HashMap<>();
        bindPath.forEach((datasourceName, pathPrefix) -> {
            if (pathPrefix == null || pathPrefix.length == 0) {
                return;
            }
            String bestMatch = getBestMatch(thisTablePath, pathPrefix);
            if (bestMatch != null) {
                bestMatchMap.put(bestMatch, datasourceName);
            }
        });
        //获取到最终的位置
        if (!bestMatchMap.isEmpty()) {
            String bestMatch = getBestMatch(thisTablePath, bestMatchMap.keySet().toArray(new String[0]));
            return bestMatchMap.get(bestMatch);
        }
        //最后匹配全局默认数据源
        String defaultDataSourceName = DataSourceProvider.getDefaultDataSourceName();
        if (StringUtils.isNotBlank(defaultDataSourceName)) {
            return defaultDataSourceName;
        }
        throw new NoSuchElementException("Table class '" + annotatedClass.getCanonicalName()
                + "' cannot be matched to any data source");
    }

    public static String getBestMatch(String classPath, String[] paths) {
        String bestMatch = null;
        int maxDepth = -1;
        int maxLength = -1;

        for (String path : paths) {
            if (classPath.startsWith(path)) {
                int depth = path.split("\\.").length;
                int length = path.length();
                // 判断路径深度和包名长度
                if (depth > maxDepth || (depth == maxDepth && length > maxLength)) {
                    maxDepth = depth;
                    maxLength = length;
                    bestMatch = path;
                }
            }
        }
        return bestMatch;
    }

    public static TableEntityMapping parseTableEntityMapping(Class<?> annotatedClass) {
        Table table = annotatedClass.getAnnotation(Table.class);
        if (table == null) {
            return null;
        }
        //判断应当归属哪个数据源
        TableEntityMapping tableEntityMapping = new TableEntityMapping();
        tableEntityMapping.setSchema(ValueParserManager.resolve(table.schema().trim()));
        String tableName = StringUtils.isBlank(table.name()) ? table.value().trim() : table.name().trim();
        tableEntityMapping.setTableName(ValueParserManager.resolve(tableName));
        String tableAlias = table.alias().trim();
        if (StringUtils.isBlank(tableAlias)) {
            tableAlias = tableEntityMapping.getTableName();
        }
        tableEntityMapping.setTableAlias(tableAlias);
        tableEntityMapping.setEntityClass(annotatedClass);
        tableEntityMapping.setCache(table.isCache());
        tableEntityMapping.setBindDataSourceName(matchBestDataSourceName(annotatedClass, table.dataSourceName()));
        return tableEntityMapping;
    }

}
