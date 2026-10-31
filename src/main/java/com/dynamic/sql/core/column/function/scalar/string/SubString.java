package com.dynamic.sql.core.column.function.scalar.string;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.*;

/** 子串参数统一绑定，NULL 字符串保持 SQL NULL，重复渲染不新增占位符。 */
public class SubString extends FunctionExpression {
    public SubString(AbstractColumFunction expression,int start,int length) { super("SUBSTRING",expression,start,length); }
    public <T,F> SubString(FieldFn<T,F> field,int start,int length) { this(new Column(field),start,length); }
    public <T,F> SubString(String alias,FieldFn<T,F> field,int start,int length) { this(new Column(alias,field),start,length); }
    public SubString(String value,int start,int length) { this(new Value(value),start,length); }
}
