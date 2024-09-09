package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.crud.select.IFetchResult;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * 表关联关系
 */
public class TableRelation<R> implements IFetchResult {
    private WhereCondition whereRelation;
    private Class<R> tableClass;

    public TableRelation(Class<R> tableClass) {
        this.tableClass = tableClass;
    }

    public IFetchResult where(WhereCondition condition) {
        return null;
    }

    @Override
    public <R> R fetchOne() {
        return null;
    }

    @Override
    public <T> T fetchOne(Class<T> returnType) {
        return null;
    }

    @Override
    public <R> List<R> fetchList() {
        return Collections.emptyList();
    }

    @Override
    public <T> List<T> fetchList(Class<T> returnType) {
        return Collections.emptyList();
    }

    @Override
    public <R> Set<R> fetchSet() {
        return Collections.emptySet();
    }

    @Override
    public <T> Set<T> fetchSet(Class<T> returnType) {
        return Collections.emptySet();
    }

    @Override
    public <R> Stream<R> fetchStream() {
        return Stream.empty();
    }

    @Override
    public <T> Stream<T> fetchStream(Class<T> returnType) {
        return Stream.empty();
    }


    static class Relation {
        String canonicalName;

        public Relation(String canonicalName) {
            this.canonicalName = canonicalName;
        }

        public String getCanonicalName() {
            return canonicalName;
        }

    }
}
