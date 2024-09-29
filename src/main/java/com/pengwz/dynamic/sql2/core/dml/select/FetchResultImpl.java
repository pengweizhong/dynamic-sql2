package com.pengwz.dynamic.sql2.core.dml.select;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.dml.select.build.SelectSpecification;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlSelectBuilder;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlSelectParam;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class FetchResultImpl<R> extends AbstractFetchResult<R> {

    public FetchResultImpl(SelectSpecification selectSpecification) {
        SqlSelectBuilder sqlSelectBuilder = SqlUtils.matchSqlSelectBuilder(selectSpecification);
        SqlSelectParam sqlSelectParam = sqlSelectBuilder.build();
        System.out.println("-- SQL解析后的结果：\n" + sqlSelectParam.getRawSql());
        System.out.println("-- ---------------------------------------------");
        StringBuilder stringBuilder =
                sqlSelectParam.getParameterBinder().replacePlaceholdersWithValues(sqlSelectParam.getRawSql().toString());
        System.out.println(stringBuilder.toString());
        System.out.println("-- SQL解析后的结果+参数：\n" +
                SqlFormatter.format(stringBuilder.toString()) );
        System.out.println("-- ---------------------------------------------");
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
}
