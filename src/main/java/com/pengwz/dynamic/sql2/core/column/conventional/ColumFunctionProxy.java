package com.pengwz.dynamic.sql2.core.column.conventional;

import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ColumFunctionProxy implements InvocationHandler {
    private final ColumFunction columFunction;
    private final String alias;

    private ColumFunctionProxy(ColumFunction columFunction, String alias) {
        this.alias = alias;
        this.columFunction = columFunction;
    }

    public static ColumFunction newInstance(ColumFunction columFunction, String alias) {
        return (ColumFunction) Proxy.newProxyInstance(
                ColumFunction.class.getClassLoader(),
                new Class<?>[]{ColumFunction.class},
                new ColumFunctionProxy(columFunction, alias)
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object invoke;
        try {
            invoke = method.invoke(columFunction, args);
        } catch (InvocationTargetException e) {
            // 提取被调用方法抛出的实际异常
            throw e.getTargetException();
        }
        return alias + invoke.toString();
    }
}
