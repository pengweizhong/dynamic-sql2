package com.pengwz.dynamic.sql2.mapper;

import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapperProxyFactory {
    private MapperProxyFactory() {
    }

    //代理SQL_CONTEXT应用到的所有方法
    private static final Map<String, Method> SQL_CONTEXT_METHOD_CACHE = new ConcurrentHashMap<>();
    //Key 是mapper， value是mapper对应的实体类
    private static final Map<Class<EntityMapper<?>>, MapperRegistry<?>> ENTITY_MAPPER_CLASS_CACHE = new ConcurrentHashMap<>();

    private static SqlContext sqlContext;

    @SuppressWarnings("unchecked")
    public static <T> MapperRegistry<T> getCacheEntityClass(Class<EntityMapper<?>> entityMapperClass) {
        return (MapperRegistry<T>) ENTITY_MAPPER_CLASS_CACHE.get(entityMapperClass);
    }

    @SuppressWarnings("all")
    public static <T, M extends EntityMapper<T>> EntityMapper<T> loadMapper(Class<M> mapperClass) {
        MapperRegistry<T> mapperRegistry = (MapperRegistry<T>) ENTITY_MAPPER_CLASS_CACHE.get(mapperClass);
        if (mapperRegistry != null) {
            return mapperRegistry.getProxyMapper();
        }
        List<Class<?>> genericTypes = ReflectUtils.getGenericTypes(mapperClass, EntityMapper.class);
        if (genericTypes.isEmpty()) {
            throw new IllegalArgumentException("Entity type must be specified in Mapper generics.");
        }
        Class<T> entityClass = (Class<T>) genericTypes.get(0);
        EntityMapper<T> entityMapper = createMapper(mapperClass);
        ENTITY_MAPPER_CLASS_CACHE.put((Class<EntityMapper<?>>) mapperClass, new MapperRegistry(entityMapper, entityClass));
        return entityMapper;
    }

    @SuppressWarnings("all")
    public static <T, M extends EntityMapper<T>> EntityMapper<T> createMapper(Class<M> mapperClass) {
        return (M) Proxy.newProxyInstance(
                mapperClass.getClassLoader(),
                new Class[]{mapperClass},
                new EntityMapperProxy()
        );
    }

    public static SqlContext getSqlContext() {
        return sqlContext;
    }

    public static Method getSqlContextMethod(String methodSignature) {
        return SQL_CONTEXT_METHOD_CACHE.get(methodSignature);
    }

    public static void setSqlContext(SqlContext sqlContext) {
        MapperProxyFactory.sqlContext = sqlContext;
        Method[] methods = sqlContext.getClass().getMethods();
        List<String> methodTypes = Arrays.asList("delete", "insert", "upsert", "update", "select");
        for (Method m : methods) {
            for (String methodType : methodTypes) {
                if (m.getName().startsWith(methodType)) {
                    SQL_CONTEXT_METHOD_CACHE.put(getMethodSignature(m, null), m);
                }
            }
        }
    }

    public static String getMethodSignature(Method method, String paramClassType) {
        StringBuilder signature = new StringBuilder(method.getName());
        if (paramClassType != null) {
            signature.append("#").append(paramClassType);
        }
        for (Class<?> paramType : method.getParameterTypes()) {
            signature.append("#").append(paramType.getName());
        }
        return signature.toString();
    }
}
