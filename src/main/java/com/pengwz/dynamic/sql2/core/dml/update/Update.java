package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.IWhereCondition;

import java.util.Collection;
import java.util.function.Consumer;

public class Update implements DataUpdater{


    @Override
    public <T> int update(T data, Consumer<IWhereCondition> condition) {
        return 0;
    }

    @Override
    public <T> int updateSelective(T entity, Consumer<IWhereCondition> condition) {
        return 0;
    }

    @Override
    public <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields) {
        return 0;
    }

    @Override
    public <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields, Consumer<IWhereCondition> condition) {
        return 0;
    }

    @Override
    public <T> int updateByPrimaryKey(T entity) {
        return 0;
    }

    @Override
    public <T> int updateSelectiveByPrimaryKey(T entity) {
        return 0;
    }
}