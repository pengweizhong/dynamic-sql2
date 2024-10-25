package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.table.TableUtils;
import com.pengwz.dynamic.sql2.utils.CollectionUtils;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class FetchResultImpl<R> extends AbstractFetchResult<R> {

    private final Class<R> resultClass;

    public FetchResultImpl(Class<R> returnClass, List<Map<String, Object>> wrapperList) {
        super(wrapperList);
        this.resultClass = returnClass;
    }

    @Override
    public R toOne() {
        Collection<R> rs = convertToCollection(ArrayList::new);
        if (rs.isEmpty()) {
            return null;
        }
        if (rs.size() > 1) {
            throw new IllegalStateException("Expected one result, but found: " + rs.size());
        }
        return rs.iterator().next();
    }

    @Override
    public <L extends List<R>> L toList(Supplier<L> listSupplier) {
        return (L) convertToCollection(listSupplier);
    }

    @Override
    public <S extends Set<R>> S toSet(Supplier<S> setSupplier) {
        return (S) convertToCollection(setSupplier);
    }

    @Override
    public <T, K, V, M extends Map<K, V>> Map<K, V> toMap(Function<T, ? extends K> keyMapper,
                                                          Function<T, ? extends V> valueMapper,
                                                          BinaryOperator<V> mergeFunction,
                                                          Supplier<M> mapSupplier) {
        return convertToMap(keyMapper, valueMapper, mergeFunction, mapSupplier);
    }

    @Override
    public <T, K, C extends Collection<T>, M extends Map<K, C>> Map<K, C> toGroupingBy(
            Function<T, ? extends K> keyMapper, Supplier<C> collectionSupplier, Supplier<M> mapSupplier) {
        return convertToGroupingBy(keyMapper, collectionSupplier, mapSupplier);
    }

    private Collection<R> convertToCollection(Supplier<? extends Collection<R>> listSupplier) {
        Collection<R> collection = listSupplier.get();
        if (CollectionUtils.isEmpty(wrapperList)) {
            return collection;
        }
        List<ColumnMeta> columnMetas;
        TableMeta tableMeta = TableProvider.getTableMeta(resultClass);
        if (tableMeta == null) {
            if (resultClass.getClassLoader() == null) {
                return convertToSystemClass(collection);
            }
            columnMetas = TableUtils.parseViewClass(resultClass);
        } else {
            columnMetas = tableMeta.getColumnMetas();
        }
        Map<String, ColumnMeta> columnNameMap = columnMetas.stream().collect(Collectors.toMap(ColumnMeta::getColumnName, v -> v));
        Map<String, ColumnMeta> fieldNameMap = columnMetas.stream().collect(Collectors.toMap(k -> k.getField().getName(), v -> v));
        for (Map<String, Object> columnObjectMap : wrapperList) {
            collection.add(reflectionInstance(columnObjectMap, columnNameMap, fieldNameMap));
        }
        return collection;
    }

    private <T, K, V, M extends Map<K, V>> Map<K, V> convertToMap(Function<T, ? extends K> keyMapper,
                                                                  Function<T, ? extends V> valueMapper,
                                                                  BinaryOperator<V> mergeFunction,
                                                                  Supplier<M> mapSupplier) {
        return convertTo(mapSupplier, (m, columnObjectMap, columnNameMap, fieldNameMap) -> {
            T value = (T) reflectionInstance(columnObjectMap, columnNameMap, fieldNameMap);
            K key = keyMapper.apply(value); // 计算键
            V val = valueMapper.apply(value); // 计算值
            // 如果 map 中已存在该键，使用 mergeFunction 处理值冲突
            m.merge(key, val, mergeFunction);
        });
    }

    public <T, K, C extends Collection<T>, M extends Map<K, C>> Map<K, C> convertToGroupingBy(
            Function<T, ? extends K> keyMapper, Supplier<C> collectionSupplier, Supplier<M> mapSupplier) {
        return convertTo(mapSupplier, (m, columnObjectMap, columnNameMap, fieldNameMap) -> {
            T value = (T) reflectionInstance(columnObjectMap, columnNameMap, fieldNameMap);
            K key = keyMapper.apply(value);
            // 获取或创建对应键的集合
            C groupedCollection = (C) m.computeIfAbsent(key, k -> collectionSupplier.get());
            // 将当前值添加到集合中
            groupedCollection.add(value);
        });
    }

    private Collection<R> convertToSystemClass(Collection<R> collection) {
        for (Map<String, Object> stringObjectMap : wrapperList) {
            if (stringObjectMap.size() > 1) {
                throw new IllegalArgumentException("Multiple columns were queried: "
                        + StringUtils.join(", ", stringObjectMap.keySet()));
            }
            if (stringObjectMap.isEmpty()) {
                collection.add(null);
            }
            Map.Entry<String, Object> entry = stringObjectMap.entrySet().iterator().next();
            R value = ConverterUtils.convertToEntityAttribute(resultClass, entry.getValue());
            collection.add(value);
        }
        return collection;
    }

    private ColumnMeta getColumnMeta(String columnName,
                                     Map<String, ColumnMeta> columnNameMap,
                                     Map<String, ColumnMeta> fieldNameMap) {
        ColumnMeta columnMeta = columnNameMap.get(columnName);
        if (columnMeta == null) {
            columnMeta = fieldNameMap.get(columnName);
        }
        //将来实现自动移除未使用的列（自动优化查询）？？？
        if (columnMeta == null) {
            log.trace("Column '{}' was queried but not used.", columnName);
        }
        return columnMeta;
    }

    R reflectionInstance(Map<String, Object> columnObjectMap,
                         Map<String, ColumnMeta> columnNameMap,
                         Map<String, ColumnMeta> fieldNameMap) {
        R instance = ReflectUtils.instance(resultClass);
        columnObjectMap.forEach((columnName, columnValue) -> {
            ColumnMeta columnMeta = getColumnMeta(columnName, columnNameMap, fieldNameMap);
            if (columnMeta == null) {
                return;
            }
            Object value;
            if (null != columnMeta.getConverter()) {
                AttributeConverter<Object, Object> objectObjectAttributeConverter =
                        ConverterUtils.loadCustomConverter(columnMeta.getConverter());
                value = objectObjectAttributeConverter.convertToEntityAttribute(columnValue);
            } else {
                value = ConverterUtils.convertToEntityAttribute(columnMeta.getField().getType(), columnValue);
            }
            if (value != null) {
                ReflectUtils.setFieldValue(instance, columnMeta.getField(), value);
            }
        });
        return instance;
    }

    private <K, V, M extends Map<K, V>> Map<K, V> convertTo(Supplier<M> mapSupplier, Operator operator) {
        M m = mapSupplier.get();
        if (CollectionUtils.isEmpty(wrapperList)) {
            return m;
        }
        List<ColumnMeta> columnMetas;
        TableMeta tableMeta = TableProvider.getTableMeta(resultClass);
        if (tableMeta == null) {
            if (resultClass.getClassLoader() == null) {
                throw new IllegalStateException(resultClass.getCanonicalName() + " cannot be mapped to Map");
            }
            columnMetas = TableUtils.parseViewClass(resultClass);
        } else {
            columnMetas = tableMeta.getColumnMetas();
        }
        Map<String, ColumnMeta> columnNameMap = columnMetas.stream().collect(Collectors.toMap(ColumnMeta::getColumnName, v -> v));
        Map<String, ColumnMeta> fieldNameMap = columnMetas.stream().collect(Collectors.toMap(k -> k.getField().getName(), v -> v));
        for (Map<String, Object> columnObjectMap : wrapperList) {
            operator.apply(m, columnObjectMap, columnNameMap, fieldNameMap);
        }
        return m;
    }

    interface Operator {
        @SuppressWarnings("all")
        void apply(Map m, Map<String, Object> columnObjectMap,
                   Map<String, ColumnMeta> columnNameMap, Map<String, ColumnMeta> fieldNameMap);
    }
}
