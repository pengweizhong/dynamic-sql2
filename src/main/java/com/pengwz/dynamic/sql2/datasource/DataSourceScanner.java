package com.pengwz.dynamic.sql2.datasource;

import com.pengwz.dynamic.sql2.anno.DBSource;
import com.pengwz.dynamic.sql2.utils.NamingUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
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
                .setScanners(Scanners.MethodsAnnotated));
        // 查找标注了 @DBSource 的方法
        //TODO 测试在类上标注
        Set<Method> methods = reflections.getMethodsAnnotatedWith(DBSource.class);
        List<DataSourceMapping> dataSources = new ArrayList<>();
        for (Method method : methods) {
            try {
                // 创建包含 @DBSource 方法的类的实例
                Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
                DBSource dbSource = method.getAnnotation(DBSource.class);
                String beanName = NamingUtils.getBeanName(dbSource.value(), method.getName());
                // 调用该方法并获取 DataSource 对象
                DataSource dataSource = (DataSource) method.invoke(instance);
                dataSources.add(new DataSourceMapping(beanName, dataSource, dbSource.defaultDB(), dbSource.basePackages()));
                log.debug("Found the data source, named: {}.", beanName);
            } catch (Exception e) {
                throw new IllegalStateException("An exception occurred while retrieving the data source.", e);
            }
        }
        return dataSources;
    }

}
