package com.dynamic.sql.core;


import com.dynamic.sql.core.column.conventional.NumberColumn;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.column.function.windows.Over;
import com.dynamic.sql.core.column.function.windows.WindowsFunction;
import com.dynamic.sql.core.dml.select.TableRelation;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;
import com.dynamic.sql.core.dml.select.build.column.ColumnQuery;
import com.dynamic.sql.core.dml.select.cte.CteTable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 该抽象类提供了 SQL 查询列的引用及其相关操作的基础实现。
 *
 * <p>主要功能包括：
 * <ul>
 *     <li>选择特定列（支持多种引用方式，如直接字段、表别名、函数列等）。</li>
 *     <li>支持应用聚合函数、窗口函数等高级 SQL 功能。</li>
 *     <li>支持嵌套查询列的引用。</li>
 *     <li>支持全列选择（所有列或特定表的所有列）。</li>
 *     <li>支持与表或公共表表达式（CTE）建立关联关系。</li>
 * </ul>
 *
 * <p>该类作为 SQL 构建的核心抽象部分，可以通过继承该类来实现更多高级操作或定制化行为。
 *
 * <p><b>注意：</b>该类的具体实现需要维护与 {@link SelectSpecification} 的状态一致性，
 * 以确保查询构建的逻辑正确。
 */
public abstract class AbstractColumnReference {
    /**
     * 维护查询的构建状态和规范。
     */
    protected final SelectSpecification selectSpecification;

    /**
     * 构造函数，初始化 {@link SelectSpecification}。
     *
     * @param selectSpecification 查询规范，用于维护查询状态
     */
    protected AbstractColumnReference(SelectSpecification selectSpecification) {
        this.selectSpecification = selectSpecification;
    }

    /**
     * 添加去重关键字（DISTINCT）。
     *
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference distinct();

    /**
     * 添加一个列到查询中。
     *
     * @param fn  映射函数，用于引用具体字段
     * @param <T> 表对应的实体类类型
     * @param <F> 字段类型
     * @return 当前列引用的实例
     */
    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn);

    /**
     * 添加一个带有表别名的列到查询中。
     *
     * @param tableAlias 表的别名
     * @param fn         映射函数，用于引用具体字段
     * @param <T>        表对应的实体类类型
     * @param <F>        字段类型
     * @return 当前列引用的实例
     */
    public abstract <T, F> AbstractColumnReference column(String tableAlias, FieldFn<T, F> fn);

    /**
     * 添加一个列到查询中，并设置别名。
     *
     * @param fn          映射函数，用于引用具体字段
     * @param columnAlias 列的别名
     * @param <T>         表对应的实体类类型
     * @param <F>         字段类型
     * @return 当前列引用的实例
     */
    public abstract <T, F> AbstractColumnReference column(FieldFn<T, F> fn, String columnAlias);

    /**
     * 添加一个带有表别名和列别名的列到查询中。
     *
     * @param tableAlias  表的别名
     * @param fn          映射函数，用于引用具体字段
     * @param columnAlias 列的别名
     * @param <T>         表对应的实体类类型
     * @param <F>         字段类型
     * @return 当前列引用的实例
     */
    public abstract <T, F> AbstractColumnReference column(String tableAlias, FieldFn<T, F> fn, String columnAlias);

    /**
     * 添加一个自定义列到查询中。
     *
     * @param tableAlias 表的别名
     * @param columnName 列名
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference column(String tableAlias, String columnName);

    /**
     * 添加一个带有别名的列到查询中。
     *
     * @param tableAlias  表的别名
     * @param columnName  列名
     * @param columnAlias 列的别名
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference column(String tableAlias, String columnName, String columnAlias);

    /**
     * 添加一个函数列到查询中。
     *
     * @param iColumFunction 函数列的定义
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference column(AbstractColumFunction iColumFunction);

    /**
     * 添加一个带别名的函数列到查询中。
     *
     * @param iColumFunction 函数列的定义
     * @param columnAlias    列的别名
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference column(AbstractColumFunction iColumFunction, String columnAlias);

    public abstract AbstractColumnReference column(NumberColumn numberColumn);

    /**
     * 添加一个窗口函数列到查询中。
     *
     * @param windowsFunction 窗口函数
     * @param over            窗口定义
     * @param columnAlias     列的别名
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference column(WindowsFunction windowsFunction, Consumer<Over> over, String columnAlias);

    /**
     * 添加一个嵌套查询列到当前查询中。
     *
     * @param nestedSelect 嵌套查询的定义
     * @param columnAlias  列的别名
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference column(Consumer<AbstractColumnReference> nestedSelect, String columnAlias);

    /**
     * 添加另一个列引用到当前查询中。
     * <p>
     * 已过时，将在未来版本删除
     *
     * @param columnReference 另一个列引用实例
     * @return 当前列引用的实例
     * @see this#includeColumns(AbstractColumnReference)
     */
    @Deprecated
    public abstract AbstractColumnReference columnReference(AbstractColumnReference columnReference);

    /**
     * 将指定的列添加到当前查询中。
     *
     * @param columnReference 另一个列引用实例
     * @return 当前列引用的实例
     * @see ColumnReference#withColumns()
     */
    public AbstractColumnReference includeColumns(AbstractColumnReference columnReference) {
        List<ColumnQuery> columFunctions = columnReference.getSelectSpecification().getColumFunctions();
        selectSpecification.getColumFunctions().addAll(columFunctions);
        return this;
    }

    /**
     * 选择所有列。
     *
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference allColumn();

    /**
     * 选择特定表的所有列。
     *
     * @param tableClass 表对应的实体类
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference allColumn(Class<?> tableClass);

    /**
     * 选择指定表别名的所有列。
     *
     * @param tableAlias 表别名
     * @return 当前列引用的实例
     */
    public abstract AbstractColumnReference allColumn(String tableAlias);

    /**
     * 设置查询的主表。
     *
     * @param tableClass 表对应的实体类
     * @param <T>        表对应的实体类类型
     * @return 表关系的定义
     */
    public abstract <T> TableRelation<T> from(Class<T> tableClass);

    /**
     * 设置带有别名的主表。
     *
     * @param tableClass 表对应的实体类
     * @param tableAlias 表别名
     * @param <T>        表对应的实体类类型
     * @return 表关系的定义
     */
    public abstract <T> TableRelation<T> from(Class<T> tableClass, String tableAlias);

    public abstract <T> TableRelation<T> from(Supplier<TableFunction> tableFunction, String tableAlias);

    /**
     * 设置一个公共表表达式（CTE）为主表。
     *
     * @param cteTable 公共表表达式
     * @return 表关系的定义
     */
    public abstract TableRelation<?> from(CteTable cteTable);

    /**
     * 设置一个嵌套查询作为主表。
     *
     * @param nestedSelect 嵌套查询定义
     * @param tableAlias   嵌套查询的别名
     * @return 表关系的定义
     */
    public abstract TableRelation<?> from(Consumer<AbstractColumnReference> nestedSelect, String tableAlias);

    protected SelectSpecification getSelectSpecification() {
        return selectSpecification;
    }
}