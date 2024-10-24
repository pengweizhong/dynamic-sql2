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
        if (wrapperList.isEmpty()) {
            return mapSupplier.get();
        }
        Map<K, V> map = mapSupplier.get();
        Collection<R> collection = convertToCollection(ArrayList::new);
        for (R item : collection) {
            T value = (T) item; // 将 Map 转换为 T 类型
            K key = keyMapper.apply(value); // 计算键
            V val = valueMapper.apply(value); // 计算值
            // 如果 map 中已存在该键，使用 mergeFunction 处理值冲突
            map.merge(key, val, mergeFunction);
        }
        return map;
    }


    private Collection<R> convertToCollection(Supplier<? extends Collection<R>> listSupplier) {//NOSONAR
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
            R instance = ReflectUtils.instance(resultClass);
            collection.add(instance);
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
        }
        return collection;
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
            Object value = ConverterUtils.convertToEntityAttribute(resultClass, entry.getValue());
            collection.add((R) value);
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
        //TODO 将来实现自动移除未使用的列（自动优化查询）？？？
        if (columnMeta == null) {
            log.trace("Column '{}' was queried but not used.", columnName);
        }
        return columnMeta;
    }
}
