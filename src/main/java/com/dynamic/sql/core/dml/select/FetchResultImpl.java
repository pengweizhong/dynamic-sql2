package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.plugins.conversion.FetchResultConverter;
import com.dynamic.sql.table.FieldMeta;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.table.TableUtils;
import com.dynamic.sql.utils.CollectionUtils;
import com.dynamic.sql.utils.ConverterUtils;
import com.dynamic.sql.utils.ReflectUtils;
import com.dynamic.sql.utils.StringUtils;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class FetchResultImpl<R> extends AbstractFetchResult<R> {

    private final Class<R> resultClass;
    private final HashSet<String> notUsedColumnTips = new HashSet<>();

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
    public <K, V, M extends Map<K, V>> Map<K, V> toMap(Function<R, ? extends K> keyMapper,
                                                       Function<R, ? extends V> valueMapper,
                                                       BinaryOperator<V> mergeFunction,
                                                       Supplier<M> mapSupplier) {
        return convertToMap(keyMapper, valueMapper, mergeFunction, mapSupplier);
    }

    @Override
    public <K, V, C extends Collection<V>, M extends Map<K, C>> Map<K, C> toGroupingBy(
            Function<R, ? extends K> keyMapper,
            Function<R, ? extends V> valueMapper,
            Supplier<C> collectionSupplier,
            Supplier<M> mapSupplier) {
        return convertToGroupingBy(keyMapper, valueMapper, collectionSupplier, mapSupplier);
    }

    private Collection<R> convertToCollection(Supplier<? extends Collection<R>> listSupplier) {
        Collection<R> collection = listSupplier.get();
        if (CollectionUtils.isEmpty(wrapperList)) {
            return collection;
        }
        FetchResultConverter fetchResultConverter = ConverterUtils.getFetchResultConverter(resultClass);
        if (fetchResultConverter != null) {
            for (Map<String, Object> stringObjectMap : wrapperList) {
                collection.add((R) fetchResultConverter.convertValueTo(stringObjectMap));
            }
            return collection;
        }
        //不需要进行任何转换
        if (resultClass == Object.class
                || Map.class.isAssignableFrom(resultClass)
                || Collection.class.isAssignableFrom(resultClass)) {
            return (Collection<R>) wrapperList;
        }
        //判断是否为原始对象
        if (resultClass.getClassLoader() == null || resultClass.isEnum()) {
            return convertToSystemClass(collection);
        }
        List<FieldMeta> fieldMetas = getFieldMetas("Collection");
        Map<String, FieldMeta> columnNameMap = fieldMetas.stream().collect(Collectors.toMap(FieldMeta::getColumnName, v -> v));
        Map<String, FieldMeta> fieldNameMap = fieldMetas.stream().collect(Collectors.toMap(k -> k.getField().getName(), v -> v));
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
            //对于null元素额外处理，以防止merge空值针，这样做更贴近业务场景
            if (val == null) {
                m.put(key, val);
            } else {
                // 如果 map 中已存在该键，使用 mergeFunction 处理值冲突
                m.merge(key, val, mergeFunction);
            }

        });
    }

    public <K, V, C extends Collection<V>, M extends Map<K, C>> Map<K, C> convertToGroupingBy(
            Function<R, ? extends K> keyMapper,
            Function<R, ? extends V> valueMapper,
            Supplier<C> collectionSupplier,
            Supplier<M> mapSupplier) {
        return convertTo(mapSupplier, (m, columnObjectMap, columnNameMap, fieldNameMap) -> {
            R result = reflectionInstance(columnObjectMap, columnNameMap, fieldNameMap);
            K key = keyMapper.apply(result);
            V value = valueMapper.apply(result);
            // 获取或创建对应键的集合
            C groupedCollection = (C) m.computeIfAbsent(key, k -> collectionSupplier.get());
            // 将当前值添加到集合中
            groupedCollection.add(value);
        });
    }

    private Collection<R> convertToSystemClass(Collection<R> collection) {
        for (Map<String, Object> stringObjectMap : wrapperList) {
            if (stringObjectMap.size() > 1) {
                throw new IllegalStateException("Expecting to return one column of results, but querying multiple columns: "
                        + StringUtils.join(", ", stringObjectMap.keySet()));
            }
            if (stringObjectMap.isEmpty()) {
                collection.add(null);
            }
            Map.Entry<String, Object> entry = stringObjectMap.entrySet().iterator().next();
            R value = ConverterUtils.convertToEntityAttribute(null, resultClass, entry.getValue());
            collection.add(value);
        }
        return collection;
    }

    private FieldMeta getColumnMeta(String columnName,
                                    Map<String, FieldMeta> columnNameMap,
                                    Map<String, FieldMeta> fieldNameMap) {
        FieldMeta columnMeta = columnNameMap.get(columnName);
        if (columnMeta == null) {
            columnMeta = fieldNameMap.get(columnName);
        }
        // 将来实现自动移除未使用的列（自动优化查询）？？？
        if (columnMeta == null && log.isTraceEnabled() && !notUsedColumnTips.contains(columnName)) {
            log.trace("Column '{}' was queried but not used.", columnName);
            notUsedColumnTips.add(columnName);
        }
        return columnMeta;
    }

    R reflectionInstance(Map<String, Object> columnObjectMap,
                         Map<String, FieldMeta> columnNameMap,
                         Map<String, FieldMeta> fieldNameMap) {
        R instance = ReflectUtils.instance(resultClass);
        columnObjectMap.forEach((columnName, columnValue) -> {
            FieldMeta columnMeta = getColumnMeta(columnName, columnNameMap, fieldNameMap);
            if (columnMeta == null) {
                return;
            }
            Object value = ConverterUtils.convertToEntityAttribute(columnMeta, columnMeta.getField().getType(), columnValue);
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
        List<FieldMeta> fieldMetas = getFieldMetas("Map");
        Map<String, FieldMeta> columnNameMap = fieldMetas.stream().collect(Collectors.toMap(FieldMeta::getColumnName, v -> v));
        Map<String, FieldMeta> fieldNameMap = fieldMetas.stream().collect(Collectors.toMap(k -> k.getField().getName(), v -> v));
        for (Map<String, Object> columnObjectMap : wrapperList) {
            operator.apply(m, columnObjectMap, columnNameMap, fieldNameMap);
        }
        return m;
    }

    private List<FieldMeta> getFieldMetas(String converterTips) {
        List<FieldMeta> fieldMetas;
        TableMeta tableMeta = TableProvider.getTableMeta(resultClass);
        if (tableMeta == null) {
            if (resultClass.getClassLoader() == null) {
                throw new IllegalStateException(resultClass.getCanonicalName() + " cannot be mapped to " + converterTips);
            }
            fieldMetas = TableUtils.parseViewClass(resultClass).getViewColumnMetas();
        } else {
            fieldMetas = tableMeta.getColumnMetas();
        }
        return fieldMetas;
    }

    interface Operator {
        @SuppressWarnings("all")
        void apply(Map map,
                   Map<String, Object> columnObjectMap,
                   Map<String, FieldMeta> columnNameMap,
                   Map<String, FieldMeta> fieldNameMap);
    }
}
