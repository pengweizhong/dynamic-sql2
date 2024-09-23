package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.anno.CTETable;
import com.pengwz.dynamic.sql2.anno.Table;
import com.pengwz.dynamic.sql2.datasource.DataSourceProvider;
import com.pengwz.dynamic.sql2.utils.StringUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TableEntityScanner {
    private static final Logger log = LoggerFactory.getLogger(TableEntityScanner.class);

    private TableEntityScanner() {
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

    public static Set<Class<?>> findCTEEntities(String forPackage) {
        Reflections reflections = getReflections(forPackage);
        return reflections.getTypesAnnotatedWith(CTETable.class);
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
        DataSourceProvider dataSourceProvider = DataSourceProvider.getInstance();
        if (StringUtils.isNotBlank(inLineDataSourceName)) {
            if (!dataSourceProvider.existDataSource(inLineDataSourceName)) {
                throw new IllegalArgumentException("DataSource '" + inLineDataSourceName + "' does not exist. " +
                        "Please check that the data source has been initialized correctly");
            }
            return inLineDataSourceName;
        }
        Map<String, String[]> bindPath = dataSourceProvider.getDataSourceBoundPath();
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
        String defaultDataSourceName = dataSourceProvider.getDefaultDataSourceName();
        if (StringUtils.isNotBlank(defaultDataSourceName)) {
            return defaultDataSourceName;
        }
        throw new NoSuchElementException("Table entity class '" + annotatedClass.getCanonicalName()
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
        //判断应当归属哪个数据源
        TableEntityMapping tableEntityMapping = new TableEntityMapping();
        tableEntityMapping.setTableName(table.value().trim());
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
