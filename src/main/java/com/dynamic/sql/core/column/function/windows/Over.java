/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.windows;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.ColumFunction;
import com.dynamic.sql.core.dml.select.order.DefaultOrderBy;
import com.dynamic.sql.core.dml.select.order.NullsFirst;
import com.dynamic.sql.core.dml.select.order.NullsLast;
import com.dynamic.sql.core.dml.select.order.OrderBy;
import com.dynamic.sql.enums.SortOrder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 窗口函数 over部分
 */
public class Over {
    //    private String partitionByClause = "";
    private List<OrderBy> orderByList = new ArrayList<>();
    //    private String frameSpecification = "";
    private String overClause;
    private final List<ColumFunction> partitions=new ArrayList<>();
    private String partitionClause="";
    private String frameClause="";

    /** 窗口边界只允许受控类型及非负偏移，禁止拼接任意 SQL。 */
    public static final class Bound {
        private final long position;
        private final String sql;
        private Bound(long position,String sql) { this.position=position;this.sql=sql; }
        public static Bound unboundedPreceding() { return new Bound(Long.MIN_VALUE,"UNBOUNDED PRECEDING"); }
        public static Bound currentRow() { return new Bound(0,"CURRENT ROW"); }
        public static Bound unboundedFollowing() { return new Bound(Long.MAX_VALUE,"UNBOUNDED FOLLOWING"); }
        public static Bound preceding(long offset) {
            if(offset<0 || offset==Long.MAX_VALUE) throw new IllegalArgumentException("Invalid frame offset");
            return new Bound(-offset,offset+" PRECEDING");
        }
        public static Bound following(long offset) {
            if(offset<0 || offset==Long.MAX_VALUE) throw new IllegalArgumentException("Invalid frame offset");
            return new Bound(offset,offset+" FOLLOWING");
        }
    }
    public <T,F> Over partitionBy(FieldFn<T,F> field) { partitions.add(new Column(field));return this; }
    public Over partitionBy(ColumFunction expression) { partitions.add(expression);return this; }
    public Over rowsBetween(Bound start,Bound end) { return frame("ROWS",start,end); }
    public Over rangeBetween(Bound start,Bound end) { return frame("RANGE",start,end); }
    private Over frame(String unit,Bound start,Bound end) {
        if(start==null || end==null || start.position>end.position
                || start.position==Long.MAX_VALUE || end.position==Long.MIN_VALUE)
            throw new IllegalArgumentException("Invalid window frame");
        frameClause=unit+" BETWEEN "+start.sql+" AND "+end.sql;return this;
    }
    /** 由 SELECT 构建器提供真实别名上下文，分区字段延迟解析。 */
    public void renderPartitions(RenderContext context) {
        StringBuilder sql=new StringBuilder();
        for(int i=0;i<partitions.size();i++) {
            if(i==0) sql.append("PARTITION BY ");else sql.append(", ");
            sql.append(partitions.get(i).render(context));
        }
        partitionClause=sql.toString();
    }


    public <T, F> Over orderBy(FieldFn<T, F> fn) {
        return orderBy(fn, SortOrder.ASC); // Default to ascending order
    }

    public Over orderBy(String tableAlias, String column) {
        return orderBy(tableAlias, column, SortOrder.ASC);
    }

    public <T, F> Over orderBy(FieldFn<T, F> fn, SortOrder sortOrder) {
        DefaultOrderBy defaultOrderBy = new DefaultOrderBy(fn, sortOrder);
        orderByList.add(defaultOrderBy);
        return this;
    }

    public Over orderBy(ColumFunction columFunction, SortOrder sortOrder) {
        DefaultOrderBy defaultOrderBy = new DefaultOrderBy(columFunction, sortOrder);
        orderByList.add(defaultOrderBy);
        return this;
    }

    public Over orderBy(String tableAlias, String column, SortOrder sortOrder) {
        DefaultOrderBy defaultOrderBy = new DefaultOrderBy(tableAlias, column, sortOrder);
        orderByList.add(defaultOrderBy);
        return this;
    }

    public Over nullsLast() {
        orderByList.add(new NullsLast());
        return this;
    }

    public Over nullsFirst() {
        orderByList.add(new NullsFirst());
        return this;
    }


    /** 收集分区表达式中的常量，顺序与窗口 SQL 一致。 */
    public ParameterBinder parameterBinder() {
        ParameterBinder binder=new ParameterBinder();
        for(ColumFunction partition:partitions) binder.addParameterBinder(partition.parameterBinder());
        return binder;
    }
    public List<OrderBy> getOrderByList() {
        return orderByList;
    }

    public void setOverClause(String overClause) {
        this.overClause = overClause;
    }

    public String toOverString(SqlDialect sqlDialect) {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "over(" + partitionClause + " " + (overClause==null ? "" : overClause) + " " + frameClause + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("over", sqlDialect);
    }


}
