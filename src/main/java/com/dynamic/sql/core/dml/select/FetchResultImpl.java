package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.plugins.conversion.AttributeConverter;
import com.dynamic.sql.plugins.conversion.FetchResultConverter;
import com.dynamic.sql.table.FieldMeta;
import com.dynamic.sql.table.TableMeta;
import com.dynamic.sql.table.TableProvider;
import com.dynamic.sql.table.TableUtils;
import com.dynamic.sql.utils.CollectionUtils;
import com.dynamic.sql.utils.ConverterUtils;
import com.dynamic.sql.utils.ReflectUtils;
import com.dynamic.sql.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class FetchResultImpl<R> extends AbstractFetchResult<R> {

    private final Class<R> resultClass;
    //    private final HashSet<String> notUsedColumnTips = new HashSet<>();
    private final CollectionColumnMapping collectionColumnMapping;

    public FetchResultImpl(Class<R> returnClass, List<Map<String, Object>> wrapperList, CollectionColumnMapping collectionColumnMapping) {
        super(wrapperList);
        this.resultClass = returnClass;
        this.collectionColumnMapping = collectionColumnMapping;
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
        //映射单个对象
        if (AttributeConverter.class.isAssignableFrom(resultClass)) {
            AttributeConverter<Object, Object> customAttributeConverter = ConverterUtils.getOrSetCustomAttributeConverter(resultClass);
            for (Map<String, Object> stringObjectMap : wrapperList) {
                //这种情况下只会存在一种值
                Object next = stringObjectMap.values().iterator().next();
                if (!customAttributeConverter.isSkipConvertToEntityAttribute(next)) {
                    collection.add((R) customAttributeConverter.convertToEntityAttribute(next));
                }
            }
            return collection;
        }
        //映射实体类或者视图
        List<FieldMeta> fieldMetas = getFieldMetas("Collection");
        Map<String, FieldMeta> columnNameMap = fieldMetas.stream().collect(Collectors.toMap(FieldMeta::getColumnName, v -> v));
        Map<String, FieldMeta> fieldNameMap = fieldMetas.stream().collect(Collectors.toMap(k -> k.getField().getName(), v -> v));
        //是否为一对多关系  不是直接映射后返回
        if (collectionColumnMapping == null) {
            for (Map<String, Object> columnObjectMap : wrapperList) {
                collection.add(reflectionInstance(columnObjectMap, columnNameMap, fieldNameMap));
            }
            return collection;
        }
        System.out.println(collectionColumnMapping);
        String targetProperty = collectionColumnMapping.getTargetProperty();
        FieldMeta targetFieldMeta = resolveFieldMeta(targetProperty, fieldNameMap, columnNameMap);
        if (targetFieldMeta == null) {
            throw new IllegalArgumentException("Index column [" + targetProperty + "] does not exist");
        }
        //targetFieldMeta 取出的是用户设置的集合字段
        Field targetField = targetFieldMeta.getField();
        Class<?> childElementClass = ReflectUtils.getUserGenericType(targetField);
        List<FieldMeta> collectionColumnMetas = TableUtils.parseViewClass(childElementClass).getViewColumnMetas();
        Map<String, FieldMeta> childColumnNameMap = collectionColumnMetas.stream().collect(Collectors.toMap(FieldMeta::getColumnName, v -> v));
        Map<String, FieldMeta> childFieldNameMap = collectionColumnMetas.stream().collect(Collectors.toMap(k -> k.getField().getName(), v -> v));
        //先暴力默认指定实现，具体细节应当根据用户指定的类型、用户是否已经实例化参数等等进行赋值，目前先这么写死，后期有时间再做变更
        HashMap<Object, Collection<Object>> childHashMap = new HashMap<>();
        HashMap<Object, Object> parentHashMap = new HashMap<>();
        FieldFn<?, ?> parentKey = collectionColumnMapping.getParentKey();
        String parentColumn = ReflectUtils.fnToFieldName(parentKey);
        FieldMeta parentFieldMeta = resolveFieldMeta(parentColumn, fieldNameMap, columnNameMap);
        //处理映射一对多
        for (Map<String, Object> columnObjectMap : wrapperList) {
            R r = reflectionInstance(childHashMap.keySet(), parentFieldMeta, columnObjectMap, columnNameMap, fieldNameMap);
            Object parentValue = ReflectUtils.getFieldValue(r, parentFieldMeta.getField());
            if (!parentHashMap.containsKey(parentValue)) {
                parentHashMap.put(parentValue, r);
            } else {
                r = (R) parentHashMap.get(parentValue);
            }
            Object child = childReflectionInstance(childElementClass, columnObjectMap, childColumnNameMap, childFieldNameMap);
            if (child != null) {
                Collection<Object> childCollection = childHashMap.computeIfAbsent(
                        parentValue,
                        key -> Set.class.isAssignableFrom(targetField.getType())
                                ? new LinkedHashSet<>()
                                : new ArrayList<>()
                );
                //添加子元素
                childCollection.add(child);
                //不断刷新结果集
                ReflectUtils.setFieldValue(r, targetField, childCollection);
            }
        }
        collection.addAll((Collection<? extends R>) parentHashMap.values());
        return collection;
    }

    private FieldMeta resolveFieldMeta(String name, Map<String, FieldMeta> fieldNameMap, Map<String, FieldMeta> columnNameMap) {
        FieldMeta meta = fieldNameMap.get(name);
        return meta != null ? meta : columnNameMap.get(name);
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
//        // 将来实现自动移除未使用的列（自动优化查询）？？？
//        if (columnMeta == null && log.isTraceEnabled() && !notUsedColumnTips.contains(columnName)) {
//            log.trace("Column '{}' was queried but not used.", columnName);
//            notUsedColumnTips.add(columnName);
//        }
        return columnMeta;
    }

    R reflectionInstance(Map<String, Object> columnObjectMap,
                         Map<String, FieldMeta> columnNameMap,
                         Map<String, FieldMeta> fieldNameMap) {
        return reflectionInstance(null, null, columnObjectMap, columnNameMap, fieldNameMap);
    }

    R reflectionInstance(Set<Object> objects,
                         FieldMeta parentFieldMeta,
                         Map<String, Object> columnObjectMap,
                         Map<String, FieldMeta> columnNameMap,
                         Map<String, FieldMeta> fieldNameMap) {
        R instance = ReflectUtils.instance(resultClass);
        if (objects != null && parentFieldMeta != null) {
            String parentColumnName = parentFieldMeta.getColumnName();
            Object columnValue = columnObjectMap.get(parentColumnName);
            FieldMeta columnMeta = getColumnMeta(parentColumnName, columnNameMap, fieldNameMap);
            if (columnMeta != null) {
                Object value = ConverterUtils.convertToEntityAttribute(columnMeta, columnMeta.getField().getType(), columnValue);
                if (value != null) {
                    ReflectUtils.setFieldValue(instance, columnMeta.getField(), value);
                }
                if (objects.contains(value)) {
                    return instance;
                }
            }
        }
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

    Object childReflectionInstance(Class<?> childElementClass,
                                   Map<String, Object> columnObjectMap,
                                   Map<String, FieldMeta> childColumnNameMap,
                                   Map<String, FieldMeta> childFieldNameMap) {
        Object childInstance = ReflectUtils.instance(childElementClass);
        AtomicBoolean isReturnNull = new AtomicBoolean(true);
        columnObjectMap.forEach((columnName, columnValue) -> {
            FieldMeta childColumnMeta = getColumnMeta(columnName, childColumnNameMap, childFieldNameMap);
            if (childColumnMeta != null) {
                Object value = ConverterUtils.convertToEntityAttribute(childColumnMeta, childColumnMeta.getField().getType(), columnValue);
                if (value != null) {
                    isReturnNull.set(false);
                    ReflectUtils.setFieldValue(childInstance, childColumnMeta.getField(), value);
                }
            }
        });
        return isReturnNull.get() ? null : childInstance;
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
