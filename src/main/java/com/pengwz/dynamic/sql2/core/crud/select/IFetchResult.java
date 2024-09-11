package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.Fn;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IFetchResult<R> {
    R toOne();

    List<R> toList();

    Set<R> toSet();

    <T1, T2, K, V> Map<K, V> toMap(Fn<T1, K> fnKey, Fn<T2, V> fnValue);

    //TODO 回头研究一下分组方法
    //void toGroupingBy();

}
