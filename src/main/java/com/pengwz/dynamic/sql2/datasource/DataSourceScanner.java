package com.pengwz.dynamic.sql2.datasource;

import com.pengwz.dynamic.sql2.anno.DBSource;
import com.pengwz.dynamic.sql2.utils.NamingUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DataSourceScanner {
    private DataSourceScanner() {
    }

    private static final Logger log = LoggerFactory.getLogger(DataSourceScanner.class);

    /**
     * 适用于单体项目中检索数据源
     *
     * @param forPackage 检索基本路径
     */
    public static List<DataSourceMapping> findDataSource(String forPackage) {
        log.debug("Find the data source based on the provided '{}' path. ", forPackage);
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(forPackage))
                //只关心指定包
                .filterInputsBy(new FilterBuilder().includePackage(forPackage))
                .setScanners(Scanners.MethodsAnnotated, Scanners.TypesAnnotated));
        // 查找标注了 @DBSource 的方法
        Set<Method> methods = reflections.getMethodsAnnotatedWith(DBSource.class);
        // 查找标注了 @DBSource 的类
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(DBSource.class);

        List<DataSourceMapping> dataSources = new ArrayList<>();
        try {
            for (Method method : methods) {
                // 创建包含 @DBSource 方法的类的实例
                Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                Object invoke = method.invoke(instance);
                DBSource dbSource = method.getAnnotation(DBSource.class);
                dataSources.add(dataSourceMapping(invoke, dbSource, method.getName()));
            }
            for (Class<?> annotatedClass : annotatedClasses) {
                Constructor<?> constructor = annotatedClass.getDeclaredConstructor();
                Object instance = constructor.newInstance();
                DBSource dbSource = annotatedClass.getAnnotation(DBSource.class);
                dataSources.add(dataSourceMapping(instance, dbSource, annotatedClass.getSimpleName()));
            }
            if (log.isDebugEnabled()) {
                for (DataSourceMapping dataSourceMapping : dataSources) {
                    log.debug("Found the data source, named: {}.", dataSourceMapping.getDataSourceName());
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("An exception occurred while retrieving the data source.", e);
        }
        return dataSources;
    }

    private static DataSourceMapping dataSourceMapping(Object instance, DBSource dbSource, String methodOrClassName) {
        if (!(instance instanceof DataSource)) {
            throw new IllegalStateException("The data source class must return an implementation of DataSource");
        }
        String beanName = NamingUtils.getBeanName(dbSource.value(), methodOrClassName);
        DataSource dataSource = (DataSource) instance;
        return new DataSourceMapping(beanName, dataSource, dbSource.defaultDB(), dbSource.basePackages());
    }

}
