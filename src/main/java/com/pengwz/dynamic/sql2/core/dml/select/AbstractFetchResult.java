package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;

import java.util.*;

public abstract class AbstractFetchResult<R> implements IFetchResult<R> {

    @Override
    public List<R> toList() {
        return this.toList(ArrayList::new);
    }


    @Override
    public Set<R> toSet() {
        return this.toSet(HashSet::new);
    }


    @Override
    public <T1, T2, K, V> Map<K, V> toMap(Fn<T1, K> fnKey, Fn<T2, V> fnValue) {
        return this.toMap(fnKey, fnValue, HashMap::new);
    }

}
