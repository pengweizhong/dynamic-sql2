package com.pengwz.dynamic.sql2.mapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class EntityMapperProxy implements InvocationHandler {
    private static final List<String> SPECIAL_SIGNATURE_METHOD = Arrays.asList(
            "selectByPrimaryKey",
            "deleteByPrimaryKey",
            "delete"
    );

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<EntityMapper<?>> entityMapperClass = (Class<EntityMapper<?>>) proxy.getClass().getInterfaces()[0];
        if (!SPECIAL_SIGNATURE_METHOD.contains(method.getName())) {
            String  methodSignature= MapperProxyFactory.getMethodSignature(method, null);
            Method sqlContextMethod = MapperProxyFactory.getSqlContextMethod(methodSignature);
            try {
                return sqlContextMethod.invoke(MapperProxyFactory.getSqlContext(), args);
            } catch (Exception e) {
                throw e.getCause();
            }
        }
        String methodSignature = MapperProxyFactory.getMethodSignature(method, "java.lang.Class");
        Method sqlContextMethod = MapperProxyFactory.getSqlContextMethod(methodSignature);
        Object[] params = new Object[args.length + 1];
        params[0] = MapperProxyFactory.getCacheEntityClass(entityMapperClass);
        System.arraycopy(args, 0, params, 1, args.length);
        try {
            return sqlContextMethod.invoke(MapperProxyFactory.getSqlContext(), params);
        } catch (Exception e) {
            throw e.getCause();
        }
    }

}
