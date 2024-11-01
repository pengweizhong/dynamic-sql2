package com.pengwz.dynamic.sql2.context;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.core.dml.insert.InsertHandler;
import com.pengwz.dynamic.sql2.core.dml.insert.impl.EntitiesInserter;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.Select;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class DefaultSqlContext implements SqlContext {
    protected DefaultSqlContext() {
    }

    @Override
    public AbstractColumnReference select() {
        return new Select().loadColumReference();
    }

    @Override
    public <T> int insertSelective(T entity) {
        return insertSelective(entity, new ArrayList<>());
    }

    @Override
    public <T> int insertSelective(T entity, Collection<Fn<T, ?>> forcedFields) {
        return new EntitiesInserter(entity, forcedFields.toArray(new Fn[]{})).insertSelective(InsertHandler::insertSelective);
    }

    @Override
    public <T> int insert(T entity) {
        return new EntitiesInserter(Collections.singleton(entity)).insert(InsertHandler::insert);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> int insertBatch(Collection<T> entities) {
        return new EntitiesInserter((Collection<Object>) entities).insertBatch(InsertHandler::insertBatch);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> int insertMultiple(Collection<T> entities) {
        return new EntitiesInserter((Collection<Object>) entities).insertMultiple(InsertHandler::insertMultiple);
    }

}
