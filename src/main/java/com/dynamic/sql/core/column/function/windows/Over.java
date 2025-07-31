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


    public List<OrderBy> getOrderByList() {
        return orderByList;
    }

    public void setOverClause(String overClause) {
        this.overClause = overClause;
    }

    public String toOverString(SqlDialect sqlDialect) {
        if (sqlDialect == SqlDialect.MYSQL) {
            return "over(" + overClause + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("over", sqlDialect);
    }


//    public static class OverBuilder {
//        private Over over = new Over();
//
//        private OverBuilder() {
//        }
//
//        public Over build() {
//            return over;
//        }
//
//        public <T, F> OverBuilder orderBy(Fn<T, F> fn) {
//            return orderBy(fn, SortOrder.ASC); // Default to ascending order
//        }
//
//        public <T, F> OverBuilder orderBy(Fn<T, F> fn, SortOrder sortOrder) {
//            // Convert fn to column name or expression
//            Column column = new Column(null, fn);
//            over.orderByColumns.add(column + " " + sortOrder.name());
//            return this;
//        }
//
//        public OverBuilder partitionBy(Fn<?, ?> fn) {
//            // Convert fn to column name or expression
//            over.partitionByClause = "PARTITION BY " + fn.toString();
//            return this;
//        }
//
//        public OverBuilder frameSpecification(String frameSpec) {
//            over.frameSpecification = frameSpec;
//            return this;
//        }
//
//        public OverBuilder unboundedPrecedingToCurrentRow() {
//            over.frameSpecification = "ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW";
//            return this;
//        }
//
//        public OverBuilder currentRowToUnboundedFollowing() {
//            over.frameSpecification = "ROWS BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING";
//            return this;
//        }
//
//        public OverBuilder nPrecedingToCurrentRow(int n) {
//            over.frameSpecification = "ROWS BETWEEN " + n + " PRECEDING AND CURRENT ROW";
//            return this;
//        }
//
//        public OverBuilder nFollowingToCurrentRow(int n) {
//            over.frameSpecification = "ROWS BETWEEN CURRENT ROW AND " + n + " FOLLOWING";
//            return this;
//        }
//
//        public OverBuilder nPrecedingToNFollowing(int nPreceding, int nFollowing) {
//            over.frameSpecification = "ROWS BETWEEN " + nPreceding + " PRECEDING AND " + nFollowing + " FOLLOWING";
//            return this;
//        }
//    }
}
