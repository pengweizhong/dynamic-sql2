package com.pengwz.dynamic.sql2.mapper;

import com.pengwz.dynamic.sql2.context.SqlContextHelper;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.plugins.pagination.PageInterceptorPlugin;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class EntityMapperProxy<T> implements InvocationHandler {
    private Class<T> entityClass;
    private SqlContext sqlContext;

    @SuppressWarnings("unchecked")
    public static <T, M extends EntityMapper<T>> EntityMapper<T> createMapper(Class<M> mapperClass) {
        // 获取实体泛型 T 的类型
        Class<T> entityClass = (Class<T>) ReflectUtils.getGenericTypes(mapperClass, EntityMapper.class).get(0);
        return (M) Proxy.newProxyInstance(
                mapperClass.getClassLoader(),
                new Class[]{mapperClass},
                new EntityMapperProxy<>(entityClass)
        );
    }

    protected static SqlContextProperties getSqlContextProperties() {
        SqlContextProperties sqlContextProperties = SqlContextProperties.defaultSqlContextProperties();
        sqlContextProperties.setScanTablePackage("com.pengwz.dynamic.sql2");
        sqlContextProperties.setScanDatabasePackage("com.pengwz.dynamic.sql2");
        SchemaProperties schemaProperties = new SchemaProperties();
        schemaProperties.setDataSourceName("dataSource");
        schemaProperties.setUseSchemaInQuery(false);
//        schemaProperties.setSqlDialect(SqlDialect.ORACLE);
//        schemaProperties.setDatabaseProductVersion("11.0.0.1");
//        schemaProperties.setDatabaseProductVersion("5.6.0");
        schemaProperties.setUseAsInQuery(true);
        schemaProperties.setPrintSql(true);
        sqlContextProperties.addSchemaProperties(schemaProperties);
        sqlContextProperties.addInterceptor(new PageInterceptorPlugin());
        return sqlContextProperties;
    }

    protected static SqlContext getSqlContext(SqlContextProperties sqlContextProperties) {
        return SqlContextHelper.createSqlContext(sqlContextProperties);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (sqlContext == null) {
            sqlContext = getSqlContext(getSqlContextProperties());
        }
        String methodName = method.getName();
        System.out.println(methodName);
        return sqlContext.selectByPrimaryKey(entityClass, args[0]);
    }

    public EntityMapperProxy(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

}
