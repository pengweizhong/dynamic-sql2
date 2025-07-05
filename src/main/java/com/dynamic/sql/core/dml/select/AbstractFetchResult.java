/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select;

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
    public <K, V> Map<K, V> toMap(Function<R, ? extends K> keyMapper, Function<R, ? extends V> valueMapper) {
        return toMap(keyMapper, valueMapper, (u1, u2) -> {
            throw new IllegalStateException("Duplicate values encountered for the same key: value1=" + u1 + ", value2=" + u2);
        });
    }

    @Override
    public <K, V> Map<K, V> toMap(Function<R, ? extends K> keyMapper,
                                  Function<R, ? extends V> valueMapper, BinaryOperator<V> mergeFunction) {
        return toMap(keyMapper, valueMapper, mergeFunction, HashMap::new);
    }

    @Override
    public <K> Map<K, List<R>> toGroupingBy(Function<R, ? extends K> keyMapper) {
        return toGroupingBy(keyMapper, ArrayList::new);
    }

    @Override
    public <K, C extends Collection<R>> Map<K, C> toGroupingBy(Function<R, ? extends K> keyMapper,
                                                               Supplier<C> collectionSupplier) {
        return toGroupingBy(keyMapper, collectionSupplier, HashMap::new);
    }

    @Override
    public <K, C extends Collection<R>, M extends Map<K, C>> Map<K, C> toGroupingBy(Function<R, ? extends K> keyMapper,
                                                                                    Supplier<C> collectionSupplier,
                                                                                    Supplier<M> mapSupplier) {
        return toGroupingBy(keyMapper, v -> v, collectionSupplier, HashMap::new);
    }

}
