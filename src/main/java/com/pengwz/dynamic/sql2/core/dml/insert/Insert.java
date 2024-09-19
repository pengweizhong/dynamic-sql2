package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.core.Fn;

import java.util.Collection;

public class Insert implements DataInserter{


    @Override
    public <T> int insertSelective(T entity) {
        return 0;
    }

    @Override
    public <T, F> int insertSelective(T entity, Collection<Fn<T, F>> forcedFields) {
        return 0;
    }

    @Override
    public <T> int insert(T entity) {
        return 0;
    }

    @Override
    public <T> int batchInsert(Collection<T> entities) {
        return 0;
    }

    @Override
    public <T> int upsert(T entity) {
        return 0;
    }

    @Override
    public <T> int batchUpsert(Collection<T> entities) {
        return 0;
    }
}
