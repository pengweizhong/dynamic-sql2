package com.pengwz.dynamic.sql2.mapper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import static com.pengwz.dynamic.sql2.mapper.MapperProxyFactory.*;

public class EntityMapperProxy implements InvocationHandler {
    private static final Method privateLookupInMethod;
    private static final Constructor<MethodHandles.Lookup> lookupConstructor;
    private static final int ALLOWED_MODES = MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED
            | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC;
    private static final List<String> SPECIAL_SIGNATURE_METHOD = Arrays.asList(
            "selectByPrimaryKey",
            "deleteByPrimaryKey",
            "delete"
    );

    static {
        Method privateLookupIn;
        try {
            privateLookupIn = MethodHandles.class.getMethod("privateLookupIn", Class.class, MethodHandles.Lookup.class);
        } catch (NoSuchMethodException e) {
            privateLookupIn = null;
        }
        privateLookupInMethod = privateLookupIn;

        Constructor<MethodHandles.Lookup> lookup = null;
        if (privateLookupInMethod == null) {
            // JDK 1.8
            try {
                lookup = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
                lookup.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new IllegalStateException(
                        "There is neither 'privateLookupIn(Class, Lookup)' nor 'Lookup(Class, int)' " +
                                "method in java.lang.invoke.MethodHandles.",
                        e);
            } catch (Exception e) {
                lookup = null;
            }
        }
        lookupConstructor = lookup;
    }

    private MethodHandle getMethodHandleJava9(Method method)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return ((MethodHandles.Lookup) privateLookupInMethod.invoke(null, declaringClass, MethodHandles.lookup())).findSpecial(
                declaringClass, method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()),
                declaringClass);
    }

    private MethodHandle getMethodHandleJava8(Method method)
            throws IllegalAccessException, InstantiationException, InvocationTargetException {
        final Class<?> declaringClass = method.getDeclaringClass();
        return lookupConstructor.newInstance(declaringClass, ALLOWED_MODES).unreflectSpecial(method, declaringClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
        }
        LinkedHashSet<String> entityMapperMethodNames = getEntityMapperMethodNames();
        //非代理方法走原始接口
        if (!entityMapperMethodNames.contains(method.getName())) {
            MethodHandle methodHandle;
            if (privateLookupInMethod == null) {
                methodHandle = getMethodHandleJava8(method);
            } else {
                methodHandle = getMethodHandleJava9(method);
            }
            return methodHandle.bindTo(proxy).invokeWithArguments(args);
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
