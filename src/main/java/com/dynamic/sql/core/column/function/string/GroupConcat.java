/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.string;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.column.function.modifiers.Distinct;
import com.dynamic.sql.core.dml.select.build.DefaultSqlSelectBuilder;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;
import com.dynamic.sql.core.dml.select.build.SqlSelectBuilder;
import com.dynamic.sql.core.dml.select.order.DefaultOrderBy;
import com.dynamic.sql.core.dml.select.order.OrderBy;
import com.dynamic.sql.enums.SortOrder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.CollectionUtils;
import com.dynamic.sql.utils.ExceptionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * GROUP_CONCAT 函数实现。
 *
 * <p>GROUP_CONCAT 是 MySQL 特有的字符串聚合函数，用于将同一分组内的多行值拼接成一个字符串。
 * 支持 DISTINCT、ORDER BY、SEPARATOR 等子句，语法结构如下：</p>
 *
 * <pre>
 * GROUP_CONCAT(
 *     [DISTINCT] expr
 *     [ORDER BY col1 ASC, col2 DESC]
 *     [SEPARATOR 'separator']
 * )
 * </pre>
 *
 * <p>典型示例：</p>
 * <pre>
 * GROUP_CONCAT(users.name)
 * GROUP_CONCAT(users.name SEPARATOR '; ')
 * GROUP_CONCAT(DISTINCT users.name ORDER BY users.age ASC, users.id DESC SEPARATOR '; ')
 * </pre>
 *
 * <p>对于复杂构建，通过 {@link GroupConcatOptions} 对 DISTINCT、ORDER BY、SEPARATOR 等子句进行建模，
 * 并在 {@link #render(RenderContext)} 中生成最终 SQL。</p>
 */
public class GroupConcat extends ColumnFunctionDecorator implements TableFunction {
    private GroupConcatOptions options;

    /**
     * 构造一个最简单的 GROUP_CONCAT(expr)
     */
    public <T, F> GroupConcat(FieldFn<T, F> fn) {
        super(fn);
    }

    /**
     * 构造 GROUP_CONCAT(expr SEPARATOR 'xxx')
     */
    public <T, F> GroupConcat(FieldFn<T, F> fn, String separator) {
        super(fn);
        this.options = new GroupConcatOptions().separator(separator);
    }

    /**
     * 使用完整的 GROUP_CONCAT 配置对象
     */
    public GroupConcat(GroupConcatOptions options) {
        super(options.getFn());
        this.options = options;
    }

    @Override
    public String render(RenderContext context) {
        //GROUP_CONCAT( DISTINCT users.name ORDER BY users.age ASC, users.id DESC SEPARATOR '; ' )
        String _separator = "";
        String _orderBy = "";
        String _column;
        if (options != null) {
            _column = options.getDistinct() == null ? delegateFunction.render(context) : options.getDistinct().render(context);
            if (CollectionUtils.isNotEmpty(options.getOrderByList())) {
                SqlSelectBuilder sqlSelectBuilder = new DefaultSqlSelectBuilder(new SelectSpecification(),
                        context.getVersion(), context.getSqlDialect(), context.getDataSourceName());
                _orderBy = " " + sqlSelectBuilder.parseOrderBy(options.getOrderByList(), null);
            }
            _separator = options.getSeparator() == null ? "" : " SEPARATOR '" + options.getSeparator() + "'";
        } else {
            _column = delegateFunction.render(context);
        }
        if (context.getSqlDialect() == SqlDialect.MYSQL) {
            return "GROUP_CONCAT(" + _column + _orderBy + _separator + ")";
        }
        throw ExceptionUtils.unsupportedFunctionException("GROUP_CONCAT", context.getSqlDialect());
    }

    /**
     * group_concat 子句配置对象。
     * <p>用于描述 group_concat 的可选子句，包括：
     * </p>
     * <ul>  <li>distinct</li>  <li>order by 多字段</li>  <li>separator 分隔符</li>  </ul>
     * <p>
     *     示例：
     * </p>
     * <pre>
     * new GroupConcatOptions(User::getName)
     *     .distinct(User::getName)
     *     .orderBy(User::getAge, ASC)
     *     .thenOrderBy(User::getId, DESC)
     *     .separator("; ");
     * </pre>
     */
    public static class GroupConcatOptions {
        private FieldFn fn;
        private Distinct distinct;
        private final List<OrderBy> orderByList = new ArrayList<>();
        private String separator;

        public GroupConcatOptions() {
        }

        public <T, F> GroupConcatOptions(FieldFn<T, F> fn) {
            this.fn = fn;
        }

        /**
         * DISTINCT
         */
        public <T, F> GroupConcatOptions distinct(FieldFn<T, F> fn) {
            this.distinct = new Distinct(fn);
            return this;
        }

        /**
         * ORDER BY ASC
         */
        public <T2, F2> GroupConcatOptions orderBy(FieldFn<T2, F2> fn, SortOrder sortOrder) {
            this.orderByList.add(new DefaultOrderBy(fn, sortOrder));
            return this;
        }

        public <T2, F2> GroupConcatOptions thenOrderBy(FieldFn<T2, F2> fn, SortOrder sortOrder) {
            return this.orderBy(fn, sortOrder);
        }

        public GroupConcatOptions orderBy(OrderBy orderBy) {
            this.orderByList.add(orderBy);
            return this;
        }

        public GroupConcatOptions thenOrderBy(OrderBy orderBy) {
            return this.orderBy(orderBy);
        }

        /**
         * SEPARATOR
         */
        public GroupConcatOptions separator(String separator) {
            this.separator = separator;
            return this;
        }

        protected List<OrderBy> getOrderByList() {
            return orderByList;
        }

        protected String getSeparator() {
            return separator;
        }

        protected Distinct getDistinct() {
            return distinct;
        }

        protected FieldFn getFn() {
            if (distinct == null) {
                return fn;
            }
            return (FieldFn) distinct.getDelegateFunction().originColumn();
        }
    }

}
