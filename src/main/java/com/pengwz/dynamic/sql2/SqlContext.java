package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.delete.DataDeleter;
import com.pengwz.dynamic.sql2.core.dml.delete.Delete;
import com.pengwz.dynamic.sql2.core.dml.insert.DataInserter;
import com.pengwz.dynamic.sql2.core.dml.insert.Insert;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.Select;
import com.pengwz.dynamic.sql2.core.dml.update.DataUpdater;
import com.pengwz.dynamic.sql2.core.dml.update.Update;

import java.util.Collection;
import java.util.function.Consumer;


public class SqlContext implements DataInserter, DataUpdater, DataDeleter {

    private SqlContext() {
    }

    //TODO 回头把扫描包配置作为参数传进来  比如指定扫描的数据源位置、实体类位置、是否开启候补实时加载表等等
    //当涉及子表嵌套查询时，是否分开多条SQL执行？
    //设置单独的数据库模式，比如大陆产的数据库，数据库太多适配麻烦，但是通常会使用mysql或者oracle语法
    //设置如果数据库版本不支持，是否启用兼容模式？用以实现同等效果
    public static SqlContext createSqlContext() {
        return new SqlContext();
    }

    public AbstractColumnReference select() {
        return new Select().loadColumReference();
    }

    @Override
    public <T> int delete(Class<T> entityClass, Consumer<WhereCondition> condition) {
        return new Delete().delete(entityClass, condition);
    }

    @Override
    public <T> int deleteByPrimaryKey(Class<T> entityClass, Object key) {
        return new Delete().deleteByPrimaryKey(entityClass, key);
    }

    @Override
    public <T> int insertSelective(T entity) {
        return new Insert().insertSelective(entity);
    }

    @Override
    public <T, F> int insertSelective(T entity, Collection<Fn<T, F>> forcedFields) {
        return new Insert().insertSelective(entity, forcedFields);
    }

    @Override
    public <T> int insert(T entity) {
        return new Insert().insert(entity);
    }

    @Override
    public <T> int batchInsert(Collection<T> entities) {
        return new Insert().batchInsert(entities);
    }

    @Override
    public <T> int upsert(T entity) {
        return new Insert().upsert(entity);
    }

    @Override
    public <T> int batchUpsert(Collection<T> entities) {
        return new Insert().batchUpsert(entities);
    }

    @Override
    public <T> int update(T data, Consumer<WhereCondition> condition) {
        return new Update().update(data, condition);
    }

    @Override
    public <T> int updateSelective(T entity, Consumer<WhereCondition> condition) {
        return new Update().updateSelective(entity, condition);
    }

    @Override
    public <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields) {
        return new Update().updateSelective(entity, forcedFields);
    }

    @Override
    public <T, F> int updateSelective(T entity, Collection<Fn<T, F>> forcedFields, Consumer<WhereCondition> condition) {
        return new Update().updateSelective(entity, forcedFields, condition);
    }

    @Override
    public <T> int updateByPrimaryKey(T entity) {
        return new Update().updateByPrimaryKey(entity);
    }

    @Override
    public <T> int updateSelectiveByPrimaryKey(T entity) {
        return new Update().updateSelectiveByPrimaryKey(entity);
    }

}
