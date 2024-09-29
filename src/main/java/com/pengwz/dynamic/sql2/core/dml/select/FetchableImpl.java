package com.pengwz.dynamic.sql2.core.dml.select;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.pengwz.dynamic.sql2.core.dml.select.build.SelectSpecification;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlSelectBuilder;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlSelectParam;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

public class FetchableImpl implements Fetchable {

    public FetchableImpl(SelectSpecification selectSpecification) {
        SqlSelectBuilder sqlSelectBuilder = SqlUtils.matchSqlSelectBuilder(selectSpecification);
        SqlSelectParam sqlSelectParam = sqlSelectBuilder.build();
        System.out.println("-- SQL解析后的结果：\n" + sqlSelectParam.getRawSql());
        System.out.println("-- ---------------------------------------------");
        StringBuilder stringBuilder =
                sqlSelectParam.getParameterBinder().replacePlaceholdersWithValues(sqlSelectParam.getRawSql().toString());
        System.out.println(stringBuilder.toString());
        System.out.println("-- SQL解析后的结果+参数：\n" +
                SqlFormatter.format(stringBuilder.toString()));
        System.out.println("-- ---------------------------------------------");
    }

    @Override
    public <R> FetchResult<R> fetch() {
        return new FetchResultImpl<>();
    }

    @Override
    public <T> FetchResult<T> fetch(Class<T> returnClass) {
        return new FetchResultImpl<>();
    }
}
