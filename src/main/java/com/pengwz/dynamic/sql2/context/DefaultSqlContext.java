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
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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
    public <T, F> int insertSelective(T entity, Collection<Fn<T, F>> forcedFields) {
        return new EntitiesInserter(entity, forcedFields.toArray(new Fn[]{})).insertSelective(InsertHandler::insertSelective);
    }

    @Override
    public <T> int insert(T entity) {
        return new EntitiesInserter(Collections.singleton(entity)).insert(InsertHandler::insert);
    }

    @Override
    public <T> int batchInsert(Collection<T> entities) {
        return 0;
    }


}
