package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.table.TableUtils;
import com.pengwz.dynamic.sql2.utils.CollectionUtils;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class FetchResultImpl<R> extends AbstractFetchResult<R> {
    private static final Logger log = LoggerFactory.getLogger(FetchResultImpl.class);
    private Class<R> resultClass;

    public FetchResultImpl(Class<R> returnClass, List<Map<String, Object>> wrapperList) {
        super(wrapperList);
        this.resultClass = returnClass;
    }

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

    private List<R> convertObject() {
        List<R> resultList = new ArrayList<>();
        if (CollectionUtils.isEmpty(wrapperList)) {
            return resultList;
        }
        TableMeta tableMeta = TableProvider.getTableMeta(resultClass);
        if (tableMeta == null) {
            tableMeta = TableUtils.parseTableClass(resultClass);
        }
        List<ColumnMeta> columnMetas = tableMeta.getColumnMetas();
        Map<String, ColumnMeta> columnNameMap = columnMetas.stream().collect(Collectors.toMap(ColumnMeta::getColumnName, v -> v));
        Map<String, ColumnMeta> fieldNameMap = columnMetas.stream().collect(Collectors.toMap(k -> k.getField().getName(), v -> v));
        for (Map<String, Object> columnObjectMap : wrapperList) {
            R instance = ReflectUtils.instance(resultClass);
            resultList.add(instance);
            columnObjectMap.forEach((columnName, columnValue) -> {
                ColumnMeta columnMeta = getColumnMeta(columnName, columnNameMap, fieldNameMap);
                if (columnMeta == null) {
                    return;
                }
                Object value = null;
                if (null != columnMeta.getConverter()) {
                    AttributeConverter<Object, Object> objectObjectAttributeConverter =
                            ConverterUtils.loadCustomConverter(columnMeta.getConverter());
                    objectObjectAttributeConverter.convertToEntityAttribute(columnValue);
                } else {
                    value = ConverterUtils.convertToEntityAttribute(columnValue);
                }
                if (value != null) {
                    ReflectUtils.setFieldValue(instance, columnMeta.getField(), value);
                }
            });
        }
        return resultList;
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
}
