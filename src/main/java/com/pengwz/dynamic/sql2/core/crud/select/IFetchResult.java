package com.pengwz.dynamic.sql2.core.crud.select;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface IFetchResult {

    <R> R fetchOne();

    <T> T fetchOne(Class<T> returnType);

    <R> List<R> fetchList();

    <T> List<T> fetchList(Class<T> returnType);

    <R> Set<R> fetchSet();

    <T> Set<T> fetchSet(Class<T> returnType);

    <R> Stream<R> fetchStream();

    <T> Stream<T> fetchStream(Class<T> returnType);
}
