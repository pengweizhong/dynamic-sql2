package com.dynamic.sql.mapper;


import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.utils.ReflectUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MapperProxyFactory {
    private MapperProxyFactory() {
    }

    //代理SQL_CONTEXT应用到的所有方法
    private static final Map<String, Method> SQL_CONTEXT_METHOD_CACHE = new ConcurrentHashMap<>();
    private static final LinkedHashSet<String> ENTITY_MAPPER_METHOD_NAME = new LinkedHashSet<>();
    //Key 是mapper， value是mapper对应的实体类
    private static final Map<Class<EntityMapper<?>>, MapperRegistry<?>> ENTITY_MAPPER_CLASS_CACHE = new ConcurrentHashMap<>();

    private static SqlContext sqlContext;

    @SuppressWarnings("unchecked")
    public static <T> MapperRegistry<T> getMapperRegistry(Class<EntityMapper<?>> entityMapperClass) {
        return (MapperRegistry<T>) ENTITY_MAPPER_CLASS_CACHE.get(entityMapperClass);
    }

    public static List<MapperRegistry<?>> getMapperRegistrys() {
        return new ArrayList<>(ENTITY_MAPPER_CLASS_CACHE.values());
    }

    @SuppressWarnings("all")
    public static <T, M extends EntityMapper<T>> M loadMapper(Class<M> mapperClass) {
        MapperRegistry<T> mapperRegistry = (MapperRegistry<T>) ENTITY_MAPPER_CLASS_CACHE.get(mapperClass);
        if (mapperRegistry != null) {
            return (M) mapperRegistry.getProxyMapper();
        }
        List<Class<?>> genericTypes = ReflectUtils.getGenericTypes(mapperClass, EntityMapper.class);
        if (genericTypes.isEmpty()) {
            throw new IllegalArgumentException("Entity type must be specified in Mapper generics.");
        }
        Class<T> entityClass = (Class<T>) genericTypes.get(0);
        EntityMapper<T> entityMapper = createMapper(mapperClass);
        ENTITY_MAPPER_CLASS_CACHE.put((Class<EntityMapper<?>>) mapperClass, new MapperRegistry(entityMapper, entityClass));
        return (M) entityMapper;
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
        if (!SQL_CONTEXT_METHOD_CACHE.isEmpty()) {
            return;
        }
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

    public static void initEntityMapperMethod() {
        if (!ENTITY_MAPPER_METHOD_NAME.isEmpty()) {
            return;
        }
        Method[] methods = EntityMapper.class.getMethods();
        List<String> methodTypes = Arrays.asList("delete", "insert", "upsert", "update", "select");
        for (Method m : methods) {
            for (String methodType : methodTypes) {
                if (m.getName().startsWith(methodType)) {
                    ENTITY_MAPPER_METHOD_NAME.add(m.getName());
                }
            }
        }
    }

    public static LinkedHashSet<String> getEntityMapperMethodNames() {
        return ENTITY_MAPPER_METHOD_NAME;
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
