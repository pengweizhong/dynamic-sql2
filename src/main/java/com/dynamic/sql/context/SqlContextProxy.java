package com.dynamic.sql.context;//package com.pengwz.dynamic.sql2.context;
//
//import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
//import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
//import com.pengwz.dynamic.sql2.core.SqlContext;
//import com.pengwz.dynamic.sql2.interceptor.SqlInterceptorChain;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.lang.reflect.Proxy;
//
//public class SqlContextProxy implements InvocationHandler {
//
//    private final SqlContextConfigurer sqlContextConfigurer;
//
//    private SqlContextProxy(SqlContextProperties sqlContextProperties) {
//        sqlContextConfigurer = new SqlContextConfigurer(sqlContextProperties, new DefaultSqlContext());
//        sqlContextConfigurer.initializeContext();
//    }
//
//    public static SqlContext newInstance(SqlContextProperties sqlContextProperties) {
//        return (SqlContext) Proxy.newProxyInstance(
//                SqlContext.class.getClassLoader(),
//                new Class<?>[]{SqlContext.class},
//                new SqlContextProxy(sqlContextProperties)
//        );
//    }
//
//    @Override
//    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//        for (SchemaProperties schemaProperty : sqlContextConfigurer.getSqlContextProperties().getSchemaProperties()) {
//            SchemaContextHolder.addSchemaProperties(schemaProperty);
//        }
//        SqlContextProperties sqlContextProperties = sqlContextConfigurer.getSqlContextProperties();
//        sqlContextProperties.getInterceptors().forEach(SqlInterceptorChain.getInstance()::addInterceptor);
//        Object invoke;
//        try {
//            invoke = method.invoke(sqlContextConfigurer.getSqlContext(), args);
//        } catch (InvocationTargetException e) {
//            // 提取被调用方法抛出的实际异常
//            throw e.getTargetException();
//        }
//        return invoke;
//    }
//
//}