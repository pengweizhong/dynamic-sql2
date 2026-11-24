/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.string;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.*;

/** 子串参数统一绑定，NULL 字符串保持 SQL NULL，重复渲染不新增占位符。 */
public class SubString extends FunctionExpression {
    /** 起始位置和长度也可由嵌套表达式计算。 */
    public SubString(AbstractColumFunction expression,Object start,Object length) { super("SUBSTRING",expression,start,length); }
    public <T,F> SubString(FieldFn<T,F> field,Object start,Object length) { this(new Column(field),start,length); }
    public <T,F> SubString(String alias,FieldFn<T,F> field,Object start,Object length) { this(new Column(alias,field),start,length); }
    public SubString(AbstractColumFunction expression,int start,int length) { super("SUBSTRING",expression,start,length); }
    public <T,F> SubString(FieldFn<T,F> field,int start,int length) { this(new Column(field),start,length); }
    public <T,F> SubString(String alias,FieldFn<T,F> field,int start,int length) { this(new Column(alias,field),start,length); }
    public SubString(String value,int start,int length) { this(new Value(value),start,length); }
}
