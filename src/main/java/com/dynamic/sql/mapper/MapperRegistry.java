package com.dynamic.sql.mapper;


public class MapperRegistry<T> {

    private final EntityMapper<T> proxyMapper;
    private final Class<T> entityClass;

    public MapperRegistry(EntityMapper<T> proxyMapper, Class<T> entityClass) {
        this.proxyMapper = proxyMapper;
        this.entityClass = entityClass;
    }

    public EntityMapper<T> getProxyMapper() {
        return proxyMapper;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}
