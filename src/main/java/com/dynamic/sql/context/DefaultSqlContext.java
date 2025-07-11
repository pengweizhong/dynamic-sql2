package com.dynamic.sql.context;


/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */

import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.ddl.ColumnMetaDataHelper;
import com.dynamic.sql.core.ddl.SqlStatement;
import com.dynamic.sql.core.ddl.TableMetaDataHelper;
import com.dynamic.sql.core.dml.delete.DeleteHandler;
import com.dynamic.sql.core.dml.delete.EntitiesDeleter;
import com.dynamic.sql.core.dml.insert.EntitiesInserter;
import com.dynamic.sql.core.dml.insert.InsertHandler;
import com.dynamic.sql.core.dml.select.DefaultSelectHandler;
import com.dynamic.sql.core.dml.select.Select;
import com.dynamic.sql.core.dml.update.EntitiesUpdater;
import com.dynamic.sql.core.dml.update.UpdateHandler;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.model.ColumnMetaData;
import com.dynamic.sql.model.TableMetaData;
import com.dynamic.sql.utils.CollectionUtils;
import com.dynamic.sql.utils.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
    public <T> List<T> selectByPrimaryKey(Class<T> entityClass, Collection<?> pkValues) {
        if (entityClass == null) {
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(pkValues)) {
            throw new IllegalArgumentException("pkValues can not be empty");
        }
        return new DefaultSelectHandler().selectByPrimaryKey(entityClass, pkValues);
    }

    @Override
    public <T> T selectOne(String sql, Class<T> returnType) {
        return selectOne(sql, returnType, null);
    }

    @Override
    public <T> T selectOne(String sql, Class<T> returnType, ParameterBinder parameterBinder) {
        return selectOne(null, sql, returnType, null);
    }

    @Override
    public <T> T selectOne(String dataSourceName, String sql, Class<T> returnType) {
        return selectOne(dataSourceName, sql, returnType, null);
    }

    @Override
    public <T> T selectOne(String dataSourceName, String sql, Class<T> returnType, ParameterBinder parameterBinder) {
        List<T> ts = selectList(dataSourceName, sql, returnType, parameterBinder);
        if (CollectionUtils.isEmpty(ts)) {
            return null;
        }
        if (ts.size() > 1) {
            throw new IllegalStateException("Expected one result, but found: " + ts.size());
        }
        return ts.get(0);
    }

    @Override
    public <T> List<T> selectList(String sql, Class<T> returnType) {
        return selectList(sql, returnType, null);
    }

    @Override
    public <T> List<T> selectList(String sql, Class<T> returnType, ParameterBinder parameterBinder) {
        return selectList(null, sql, returnType, parameterBinder);
    }

    @Override
    public <T> List<T> selectList(String dataSourceName, String sql, Class<T> returnType) {
        return selectList(dataSourceName, sql, returnType, null);
    }

    @Override
    public <T> List<T> selectList(String dataSourceName, String sql, Class<T> returnType, ParameterBinder parameterBinder) {
        if (StringUtils.isBlank(sql)) {
            throw new IllegalArgumentException("sql can not be null");
        }
        return new DefaultSelectHandler().selectList(dataSourceName, sql, returnType, parameterBinder);
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
    public <T> int deleteByPrimaryKey(Class<T> entityClass, Collection<?> pkValues) {
        if (CollectionUtils.isEmpty(pkValues)) {
            throw new IllegalArgumentException("pkValues can not be null");
        }
        return new EntitiesDeleter(entityClass).deleteByPrimaryKey(pkValues, DeleteHandler::deleteByPrimaryKey);
    }

    @Override
    public <T> int delete(Class<T> entityClass, Consumer<GenericWhereCondition> condition) {
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
    public <T> int update(T entity, Consumer<GenericWhereCondition> condition) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesUpdater(Collections.singleton(entity), null, condition).update(UpdateHandler::update);
    }

    @Override
    public <T> int updateSelective(T entity, Consumer<GenericWhereCondition> condition) {
        if (entity == null) {
            return 0;
        }
        return new EntitiesUpdater(Collections.singleton(entity), null, condition).updateSelective(UpdateHandler::updateSelective);
    }

    @Override
    public <T> int updateSelective(T entity, Collection<Fn<T, ?>> forcedFields, Consumer<GenericWhereCondition> condition) {
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

    @Override
    public Object execute(String dataSourceName, String sql, ParameterBinder parameterBinder) {
        return new SqlStatement(dataSourceName, sql, parameterBinder).execute();
    }

    @Override
    public List<TableMetaData> getAllTableMetaData(String dataSourceName, String catalog, String schemaPattern, String tableNamePattern, String[] tableTypes) {
        return new TableMetaDataHelper(dataSourceName, catalog, schemaPattern, tableNamePattern, tableTypes).getAllTableMetaData();
    }

    @Override
    public List<ColumnMetaData> getAllColumnMetaData(String dataSourceName, String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {
        return new ColumnMetaDataHelper(dataSourceName, catalog, schemaPattern, tableNamePattern, columnNamePattern).getAllTableMetaData();
    }
}
