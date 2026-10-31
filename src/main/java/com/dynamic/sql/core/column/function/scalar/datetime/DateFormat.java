package com.dynamic.sql.core.column.function.scalar.datetime;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.*;

/** 日期格式参数通过公共模型绑定，重复执行保持 SQL 和参数稳定。 */
public class DateFormat extends FunctionExpression implements DatetimeFunction {
    public DateFormat(AbstractColumFunction expression,String format) { super("DATE_FORMAT",expression,format); }
    public <T,F> DateFormat(FieldFn<T,F> field,String format) { this(new Column(field),format); }
    public <T,F> DateFormat(String alias,FieldFn<T,F> field,String format) { this(new Column(alias,field),format); }
}
