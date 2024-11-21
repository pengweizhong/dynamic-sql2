package com.pengwz.dynamic.sql2.mapper;

public class MapperFactory {
    private MapperFactory() {
    }

//    public static <T> EntityMapper<T> createMapper(Class<T> entityClass) {
//        return EntityMapperProxy.createMapper(entityClass);
//    }

    public static <T, M extends EntityMapper<T>> EntityMapper<T> createMapper(Class<M> mapperClass) {
        return EntityMapperProxy.createMapper(mapperClass);
    }
}
