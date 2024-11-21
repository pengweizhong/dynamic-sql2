package com.pengwz.dynamic.sql2.context;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.delete.DeleteHandler;
import com.pengwz.dynamic.sql2.core.dml.delete.EntitiesDeleter;
import com.pengwz.dynamic.sql2.core.dml.insert.EntitiesInserter;
import com.pengwz.dynamic.sql2.core.dml.insert.InsertHandler;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.DefaultSelectHandler;
import com.pengwz.dynamic.sql2.core.dml.select.Select;
import com.pengwz.dynamic.sql2.core.dml.update.EntitiesUpdater;
import com.pengwz.dynamic.sql2.core.dml.update.UpdateHandler;
import com.pengwz.dynamic.sql2.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;


public class DefaultSqlContext implements SqlContext {

    protected DefaultSqlContext() {
    }

    @Override
    public AbstractColumnReference select() {
        return new Select().loadColumReference();
    }

    @Override
    public <T> T selectByPrimaryKey(Class<T> entityClass, Object pkValue) {
        if (entityClass == null) {
            return null;
        }
        if (pkValue == null) {
            throw new IllegalArgumentException("pkValue can not be null");
        }
        return new DefaultSelectHandler().selectByPrimaryKey(entityClass, pkValue);
    }

    @Override
    public <T> int insertSelective(T entity) {
        if (entity == null) {
            return 0;
        }
        return insertSelective(entity, new ArrayList<>());
    }

    @Override
    public <T> int insertSelective(T entity, Collection<Fn<T, ?>> forcedFields) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesInserter(entity, forcedFields.toArray(new Fn[]{})).insertSelective(InsertHandler::insertSelective);
    }

    @Override
    public <T> int insert(T entity) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesInserter(Collections.singleton(entity)).insert(InsertHandler::insert);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> int insertBatch(Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }
        return new EntitiesInserter((Collection<Object>) entities).insertBatch(InsertHandler::insertBatch);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> int insertMultiple(Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }
        return new EntitiesInserter((Collection<Object>) entities).insertMultiple(InsertHandler::insertMultiple);
    }

    @Override
    public <T> int deleteByPrimaryKey(Class<T> entityClass, Object pkValue) {
        if (pkValue == null) {
            throw new IllegalArgumentException("pkValue can not be null");
        }
        return new EntitiesDeleter(entityClass).deleteByPrimaryKey(pkValue, DeleteHandler::deleteByPrimaryKey);
    }

    @Override
    public <T> int deleteByPrimaryKey(Class<T> entityClass, Collection<Object> pkValues) {
        if (CollectionUtils.isEmpty(pkValues)) {
            throw new IllegalArgumentException("pkValues can not be null");
        }
        return new EntitiesDeleter(entityClass).deleteByPrimaryKey(pkValues, DeleteHandler::deleteByPrimaryKey);
    }

    @Override
    public <T> int delete(Class<T> entityClass, Consumer<WhereCondition> condition) {
        return new EntitiesDeleter(entityClass).delete(condition, DeleteHandler::delete);
    }

    @Override
    public <T> int updateByPrimaryKey(T entity) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesUpdater(Collections.singleton(entity)).updateByPrimaryKey(UpdateHandler::updateByPrimaryKey);
    }

    @Override
    public <T> int updateSelectiveByPrimaryKey(T entity) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesUpdater(Collections.singleton(entity)).updateSelectiveByPrimaryKey(UpdateHandler::updateSelectiveByPrimaryKey);
    }

    @Override
    public <T> int updateSelectiveByPrimaryKey(T entity, Collection<Fn<T, ?>> forcedFields) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesUpdater(Collections.singleton(entity), forcedFields.toArray(new Fn[]{})).updateSelectiveByPrimaryKey(UpdateHandler::updateSelectiveByPrimaryKey);
    }

    @Override
    public <T> int update(T entity, Consumer<WhereCondition> condition) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesUpdater(Collections.singleton(entity), null, condition).update(UpdateHandler::update);
    }

    @Override
    public <T> int updateSelective(T entity, Consumer<WhereCondition> condition) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesUpdater(Collections.singleton(entity), null, condition).updateSelective(UpdateHandler::updateSelective);
    }

    @Override
    public <T> int updateSelective(T entity, Collection<Fn<T, ?>> forcedFields, Consumer<WhereCondition> condition) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesUpdater(Collections.singleton(entity), forcedFields.toArray(new Fn[]{}), condition).updateSelective(UpdateHandler::updateSelective);
    }

    @Override
    public <T> int upsert(T entity) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesInserter(entity, null).upsert(InsertHandler::upsert);
    }

    @Override
    public <T> int upsertSelective(T entity) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesInserter(entity, null).upsertSelective(InsertHandler::upsertSelective);
    }

    @Override
    public <T> int upsertSelective(T entity, Collection<Fn<T, ?>> forcedFields) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesInserter(entity, forcedFields.toArray(new Fn[]{})).upsertSelective(InsertHandler::upsertSelective);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> int upsertMultiple(Collection<T> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return 0;
        }
        return new EntitiesInserter((Collection<Object>) entities).upsertMultiple(InsertHandler::upsertMultiple);
    }
}
