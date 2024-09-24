package com.pengwz.dynamic.sql2.context;

import com.pengwz.dynamic.sql2.context.config.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.CrudOperations;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class SqlContextProxy implements InvocationHandler {

    private final SqlContextConfigurer sqlContextConfigurer;

    private SqlContextProxy(SqlContextProperties sqlContextProperties) {
        sqlContextConfigurer = new SqlContextConfigurer(sqlContextProperties, new SqlContext());
        sqlContextConfigurer.initializeContext();
    }

    public static CrudOperations newInstance(SqlContextProperties sqlContextProperties) {
        return (CrudOperations) Proxy.newProxyInstance(
                CrudOperations.class.getClassLoader(),
                new Class<?>[]{CrudOperations.class},
                new SqlContextProxy(sqlContextProperties) // 将参数传递给代理类
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        SqlContextHolder.setSqlContextConfigurer(sqlContextConfigurer);
        Object invoke;
        try {
            invoke = method.invoke(sqlContextConfigurer.getSqlContext(), args);
        } catch (InvocationTargetException e) {
            // 提取被调用方法抛出的实际异常
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("Error invoking SqlContext method", e);
        } finally {
            SqlContextHolder.clear();
        }
        return invoke;
    }

}