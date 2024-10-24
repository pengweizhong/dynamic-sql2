package com.pengwz.dynamic.sql2.core.dml.select;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractFetchResult<R> implements FetchResult<R> {
    protected List<Map<String, Object>> wrapperList;
    protected static final Logger log = LoggerFactory.getLogger(AbstractFetchResult.class);

    protected AbstractFetchResult(List<Map<String, Object>> wrapperList) {
        this.wrapperList = wrapperList;
    }

    @Override
    public List<R> toList() {
        return this.toList(ArrayList::new);
    }


    @Override
    public Set<R> toSet() {
        return this.toSet(HashSet::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, K, V> Map<K, V> toMap(Function<T, ? extends K> keyMapper, Function<T, ? extends V> valueMapper) {
        return toMap(keyMapper, valueMapper, (u, v) -> {
            throw new IllegalStateException("Duplicate key: " + keyMapper.apply((T) u) +
                    "\nValue1: " + u +
                    "\nValue2: " + v);
        });
    }

    @Override
    public <T, K, V> Map<K, V> toMap(Function<T, ? extends K> keyMapper,
                                     Function<T, ? extends V> valueMapper, BinaryOperator<V> mergeFunction) {
        return toMap(keyMapper, valueMapper, mergeFunction, HashMap::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, K, C extends Collection<T>> Map<K, C> toGroupingBy(Function<T, ? extends K> keyMapper) {
        return toGroupingBy(keyMapper, () -> (C) new ArrayList<T>());
    }

    @Override
    public <T, K, C extends Collection<T>> Map<K, C> toGroupingBy(Function<T, ? extends K> keyMapper,
                                                                  Supplier<C> collectionSupplier) {
        return toGroupingBy(keyMapper, collectionSupplier, HashMap::new);
    }
}
