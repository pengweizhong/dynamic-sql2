package com.pengwz.dynamic.sql2.mapper;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static com.pengwz.dynamic.sql2.mapper.MapperProxyFactory.*;

public class EntityMapperProxy implements InvocationHandler {
    private static final List<String> SPECIAL_SIGNATURE_METHOD = Arrays.asList(
            "selectByPrimaryKey",
            "deleteByPrimaryKey",
            "delete"
    );

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LinkedHashSet<String> entityMapperMethodNames = getEntityMapperMethodNames();
        //非代理方法走原始接口
        if (!entityMapperMethodNames.contains(method.getName())) {
            final Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                    .getDeclaredConstructor(Class.class, int.class);
            final Class<?> declaringClass = method.getDeclaringClass();
            return constructor
                    .newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
                    .unreflectSpecial(method, declaringClass)
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        }
        if (!SPECIAL_SIGNATURE_METHOD.contains(method.getName())) {
            String methodSignature = getMethodSignature(method, null);
            Method sqlContextMethod = getSqlContextMethod(methodSignature);
            try {
                return sqlContextMethod.invoke(getSqlContext(), args);
            } catch (Exception e) {
                throw e.getCause();
            }
        }
        String methodSignature = getMethodSignature(method, "java.lang.Class");
        Method sqlContextMethod = getSqlContextMethod(methodSignature);
        Object[] params = new Object[args.length + 1];
        Class<EntityMapper<?>> entityMapperClass = (Class<EntityMapper<?>>) proxy.getClass().getInterfaces()[0];
        MapperRegistry<Object> mapperRegistry = getMapperRegistry(entityMapperClass);
        params[0] = mapperRegistry.getEntityClass();
        System.arraycopy(args, 0, params, 1, args.length);
        try {
            return sqlContextMethod.invoke(getSqlContext(), params);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

}
