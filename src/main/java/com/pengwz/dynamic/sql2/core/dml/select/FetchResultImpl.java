package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class FetchResultImpl<R> extends AbstractFetchResult<R> {


    @Override
    public R toOne() {
        return null;
    }

    @Override
    public <L extends List<R>> L toList(Supplier<L> listSupplier) {
        return null;
    }

    @Override
    public <S extends Set<?>> S toSet(Supplier<S> setSupplier) {
        return null;
    }

    @Override
    public <T1, T2, K, V, M extends Map<K, V>> M toMap(Fn<T1, K> fnKey, Fn<T2, V> fnValue, Supplier<M> mapSupplier) {
        return null;
    }
}
