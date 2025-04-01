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
 *     <li>选择特定列，支持多种引用方式（如直接字段引用、带表别名的字段引用、自定义列名等）。</li>
 *     <li>支持应用聚合函数、窗口函数等高级 SQL 功能，增强查询表达能力。</li>
 *     <li>支持嵌套查询列的定义，方便构建复杂查询。</li>
 *     <li>支持全列选择（包括所有列或特定表的所有列）。</li>
 *     <li>支持与表或公共表表达式（CTE）建立关联关系，灵活定义数据源。</li>
 * </ul>
 *
 * <p>该类作为 SQL 构建的核心抽象部分，开发者可以通过继承该类实现更多高级操作或定制化行为。
 *
 * <p><b>注意事项：</b>
 * <ul>
 *     <li>具体实现需要确保与 {@link SelectSpecification} 的状态一致性，以保证查询构建的正确性。</li>
 *     <li>某些方法支持条件生效参数（`isEffective`），用于动态控制列的添加，需谨慎使用以避免逻辑错误。</li>
 *     <li>已过时的 `columnReference` 方法将在未来版本移除，建议使用 `includeColumns` 替代。</li>
 * </ul>
 *
 * @see SelectSpecification
 */
public abstract class AbstractColumnReference {
    /**
     * 维护查询的构建状态和规范。
     *
     * <p>该字段存储了查询的构建上下文，包括列选择、表关系等信息，确保查询的完整性。
     */
    protected final SelectSpecification selectSpecification;

    /**
     * 构造函数，初始化 {@link SelectSpecification} 实例。
     *
     * <p>通过传入 {@link SelectSpecification} 来初始化查询上下文，所有的列操作都基于此上下文进行。
     *
     * @param selectSpecification 查询规范对象，用于维护查询状态
     */
    protected AbstractColumnReference(SelectSpecification selectSpecification) {
        this.selectSpecification = selectSpecification;
    }

    /**
     * 添加去重关键字（DISTINCT）到查询中。
     *
     * <p>该方法会将 `DISTINCT` 关键字应用到当前查询的列选择中，确保返回结果中不包含重复记录。
     *
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference distinct();

    /**
     * 条件性地添加去重关键字（DISTINCT）到查询中。
     *
     * <p>根据 `isEffective` 参数决定是否应用 `DISTINCT` 关键字，仅当 `isEffective` 为 `true` 时生效。
     *
     * @param isEffective 是否启用去重，`true` 表示启用，`false` 表示忽略
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference distinct(boolean isEffective) {
        return isEffective ? this : distinct();
    }

    /**
     * 添加一个列到查询中，使用方法引用指定字段。
     *
     * <p>通过方法引用（`Fn`）选择实体类中的特定字段，添加到查询列列表中。
     *
     * @param fn  映射函数，用于引用实体类中的字段
     * @param <T> 表对应的实体类类型
     * @param <F> 字段的类型
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract <T, F> AbstractColumnReference column(Fn<T, F> fn);

    /**
     * 条件性地添加一个列到查询中。
     *
     * <p>根据 `isEffective` 参数决定是否添加字段，仅当 `isEffective` 为 `true` 时生效。
     *
     * @param isEffective 是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param fn          映射函数，用于引用实体类中的字段
     * @param <T>         表对应的实体类类型
     * @param <F>         字段的类型
     * @return 当前列引用的实例，用于链式调用
     */
    public <T, F> AbstractColumnReference column(boolean isEffective, Fn<T, F> fn) {
        return isEffective ? this : column(fn);
    }

    /**
     * 添加一个带有表别名的列到查询中。
     *
     * <p>通过指定表别名和方法引用选择字段，适用于多表查询场景。
     *
     * @param tableAlias 表的别名，用于区分同名字段
     * @param fn         映射函数，用于引用实体类中的字段
     * @param <T>        表对应的实体类类型
     * @param <F>        字段的类型
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract <T, F> AbstractColumnReference column(String tableAlias, FieldFn<T, F> fn);

    /**
     * 条件性地添加一个带有表别名的列到查询中。
     *
     * @param isEffective 是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param tableAlias  表的别名
     * @param fn          映射函数，用于引用实体类中的字段
     * @param <T>         表对应的实体类类型
     * @param <F>         字段的类型
     * @return 当前列引用的实例，用于链式调用
     */
    public <T, F> AbstractColumnReference column(boolean isEffective, String tableAlias, FieldFn<T, F> fn) {
        return isEffective ? this : column(tableAlias, fn);
    }

    /**
     * 添加一个列到查询中，并设置列别名。
     *
     * <p>通过方法引用选择字段，并为列指定别名，便于结果映射或查询结果识别。
     *
     * @param fn          映射函数，用于引用实体类中的字段
     * @param columnAlias 列的别名
     * @param <T>         表对应的实体类类型
     * @param <F>         字段的类型
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract <T, F> AbstractColumnReference column(FieldFn<T, F> fn, String columnAlias);

    /**
     * 条件性地添加一个带别名的列到查询中。
     *
     * @param isEffective 是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param fn          映射函数，用于引用实体类中的字段
     * @param columnAlias 列的别名
     * @param <T>         表对应的实体类类型
     * @param <F>         字段的类型
     * @return 当前列引用的实例，用于链式调用
     */
    public <T, F> AbstractColumnReference column(boolean isEffective, FieldFn<T, F> fn, String columnAlias) {
        return isEffective ? this : column(fn, columnAlias);
    }

    /**
     * 添加一个带有表别名和列别名的列到查询中。
     *
     * <p>适用于多表查询场景，通过表别名和列别名明确指定字段。
     *
     * @param tableAlias  表的别名
     * @param fn          映射函数，用于引用实体类中的字段
     * @param columnAlias 列的别名
     * @param <T>         表对应的实体类类型
     * @param <F>         字段的类型
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract <T, F> AbstractColumnReference column(String tableAlias, FieldFn<T, F> fn, String columnAlias);

    /**
     * 条件性地添加一个带有表别名和列别名的列到查询中。
     *
     * @param isEffective 是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param tableAlias  表的别名
     * @param fn          映射函数，用于引用实体类中的字段
     * @param columnAlias 列的别名
     * @param <T>         表对应的实体类类型
     * @param <F>         字段的类型
     * @return 当前列引用的实例，用于链式调用
     */
    public <T, F> AbstractColumnReference column(boolean isEffective, String tableAlias, FieldFn<T, F> fn, String columnAlias) {
        return isEffective ? this : column(tableAlias, fn, columnAlias);
    }

    /**
     * 添加一个自定义列到查询中。
     *
     * <p>通过指定表别名和列名，适合手动定义列或非实体字段。
     *
     * @param tableAlias 表的别名
     * @param columnName 列名
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference column(String tableAlias, String columnName);

    /**
     * 条件性地添加一个自定义列到查询中。
     *
     * @param isEffective 是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param tableAlias  表的别名
     * @param columnName  列名
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference column(boolean isEffective, String tableAlias, String columnName) {
        return isEffective ? this : column(tableAlias, columnName);
    }

    /**
     * 添加一个带有别名的自定义列到查询中。
     *
     * <p>通过表别名、列名和列别名定义自定义字段。
     *
     * @param tableAlias  表的别名
     * @param columnName  列名
     * @param columnAlias 列的别名
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference column(String tableAlias, String columnName, String columnAlias);

    /**
     * 条件性地添加一个带有别名的自定义列到查询中。
     *
     * @param isEffective 是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param tableAlias  表的别名
     * @param columnName  列名
     * @param columnAlias 列的别名
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference column(boolean isEffective, String tableAlias, String columnName, String columnAlias) {
        return isEffective ? this : column(tableAlias, columnName, columnAlias);
    }

    /**
     * 添加一个函数列到查询中。
     *
     * <p>通过 {@link AbstractColumFunction} 定义聚合函数或标量函数（如 `SUM`、`ROUND`）。
     *
     * @param iColumFunction 函数列的定义
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference column(AbstractColumFunction iColumFunction);

    /**
     * 条件性地添加一个函数列到查询中。
     *
     * @param isEffective    是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param iColumFunction 函数列的定义
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference column(boolean isEffective, AbstractColumFunction iColumFunction) {
        return isEffective ? this : column(iColumFunction);
    }

    /**
     * 添加一个带别名的函数列到查询中。
     *
     * <p>为函数列指定别名，便于结果映射。
     *
     * @param iColumFunction 函数列的定义
     * @param columnAlias    列的别名
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference column(AbstractColumFunction iColumFunction, String columnAlias);

    /**
     * 条件性地添加一个带别名的函数列到查询中。
     *
     * @param isEffective    是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param iColumFunction 函数列的定义
     * @param columnAlias    列的别名
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference column(boolean isEffective, AbstractColumFunction iColumFunction, String columnAlias) {
        return isEffective ? this : column(iColumFunction, columnAlias);
    }

    /**
     * 添加一个数字列到查询中。
     *
     * <p>通过 {@link NumberColumn} 定义数字相关的列操作。
     *
     * @param numberColumn 数字列对象
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference column(NumberColumn numberColumn);

    /**
     * 条件性地添加一个数字列到查询中。
     *
     * @param isEffective  是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param numberColumn 数字列对象
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference column(boolean isEffective, NumberColumn numberColumn) {
        return isEffective ? this : column(numberColumn);
    }

    /**
     * 添加一个窗口函数列到查询中。
     *
     * <p>通过 {@link WindowsFunction} 和 {@link Over} 定义窗口函数（如 `RANK`、`LAG`），并指定窗口定义。
     *
     * @param windowsFunction 窗口函数实例
     * @param over            窗口定义的消费者，用于配置排序或分区
     * @param columnAlias     列的别名
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference column(WindowsFunction windowsFunction, Consumer<Over> over, String columnAlias);

    /**
     * 条件性地添加一个窗口函数列到查询中。
     *
     * @param isEffective     是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param windowsFunction 窗口函数实例
     * @param over            窗口定义的消费者，用于配置排序或分区
     * @param columnAlias     列的别名
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference column(boolean isEffective, WindowsFunction windowsFunction, Consumer<Over> over, String columnAlias) {
        return isEffective ? this : column(windowsFunction, over, columnAlias);
    }

    /**
     * 添加一个嵌套查询列到当前查询中。
     *
     * <p>通过嵌套查询定义一个子查询列，适合复杂查询场景。
     *
     * @param nestedSelect 嵌套查询的定义，接受一个消费者来构建子查询
     * @param columnAlias  列的别名
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference column(Consumer<AbstractColumnReference> nestedSelect, String columnAlias);

    /**
     * 条件性地添加一个嵌套查询列到当前查询中。
     *
     * @param isEffective  是否添加字段，`true` 表示添加，`false` 表示忽略
     * @param nestedSelect 嵌套查询的定义
     * @param columnAlias  列的别名
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference column(boolean isEffective, Consumer<AbstractColumnReference> nestedSelect, String columnAlias) {
        return isEffective ? this : column(nestedSelect, columnAlias);
    }

    /**
     * 添加另一个列引用到当前查询中。
     *
     * <p><b>注意：</b>该方法已过时，将在未来版本中移除，建议使用 {@link #includeColumns(AbstractColumnReference)} 替代。
     *
     * @param columnReference 另一个列引用实例
     * @return 当前列引用的实例，用于链式调用
     * @deprecated 该方法将在未来版本删除，请使用 {@link #includeColumns(AbstractColumnReference)}
     */
    @Deprecated
    public abstract AbstractColumnReference columnReference(AbstractColumnReference columnReference);

    /**
     * 将指定的列添加到当前查询中。
     *
     * <p>将另一个列引用的所有列（通过 {@link ColumnQuery} 列表）合并到当前查询中。
     *
     * @param columnReference 另一个列引用实例
     * @return 当前列引用的实例，用于链式调用
     * @see ColumnReference#withColumns()
     */
    public AbstractColumnReference includeColumns(AbstractColumnReference columnReference) {
        List<ColumnQuery> columFunctions = columnReference.getSelectSpecification().getColumFunctions();
        selectSpecification.getColumFunctions().addAll(columFunctions);
        return this;
    }

    /**
     * 条件性地将指定的列添加到当前查询中。
     *
     * @param isEffective     是否添加列，`true` 表示添加，`false` 表示忽略
     * @param columnReference 另一个列引用实例
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference includeColumns(boolean isEffective, AbstractColumnReference columnReference) {
        return isEffective ? this : includeColumns(columnReference);
    }

    /**
     * 选择所有列。
     *
     * <p>添加当前查询的所有列（无条件选择），适用于选择表中的全部字段。
     *
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference allColumn();

    /**
     * 条件性地选择所有列。
     *
     * @param isEffective 是否选择所有列，`true` 表示选择，`false` 表示忽略
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference allColumn(boolean isEffective) {
        return isEffective ? this : allColumn();
    }

    /**
     * 选择特定表的所有列。
     *
     * <p>根据指定的实体类选择表中的所有列。
     *
     * @param tableClass 表对应的实体类
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference allColumn(Class<?> tableClass);

    /**
     * 条件性地选择特定表的所有列。
     *
     * @param isEffective 是否选择所有列，`true` 表示选择，`false` 表示忽略
     * @param tableClass  表对应的实体类
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference allColumn(boolean isEffective, Class<?> tableClass) {
        return isEffective ? this : allColumn(tableClass);
    }

    /**
     * 选择指定表别名的所有列。
     *
     * <p>根据表别名选择对应的所有列，适用于多表查询。
     *
     * @param tableAlias 表别名
     * @return 当前列引用的实例，用于链式调用
     */
    public abstract AbstractColumnReference allColumn(String tableAlias);

    /**
     * 条件性地选择指定表别名的所有列。
     *
     * @param isEffective 是否选择所有列，`true` 表示选择，`false` 表示忽略
     * @param tableAlias  表别名
     * @return 当前列引用的实例，用于链式调用
     */
    public AbstractColumnReference allColumn(boolean isEffective, String tableAlias) {
        return isEffective ? this : allColumn(tableAlias);
    }

    /**
     * 设置查询的主表。
     *
     * <p>指定实体类作为主表，初始化查询的数据源。
     *
     * @param tableClass 表对应的实体类
     * @param <T>        表对应的实体类类型
     * @return {@link TableRelation} 对象，用于定义表关系
     */
    public abstract <T> TableRelation<T> from(Class<T> tableClass);

    /**
     * 设置带有别名的主表。
     *
     * <p>指定实体类和表别名作为主表，适用于多表或别名查询。
     *
     * @param tableClass 表对应的实体类
     * @param tableAlias 表别名
     * @param <T>        表对应的实体类类型
     * @return {@link TableRelation} 对象，用于定义表关系
     */
    public abstract <T> TableRelation<T> from(Class<T> tableClass, String tableAlias);

    /**
     * 设置表函数作为主表。
     *
     * <p>通过 {@link TableFunction} 和别名定义动态表，适合复杂查询。
     *
     * @param tableFunction 表函数的供应商
     * @param tableAlias    表别名
     * @param <T>           表对应的实体类类型
     * @return {@link TableRelation} 对象，用于定义表关系
     */
    public abstract <T> TableRelation<T> from(Supplier<TableFunction> tableFunction, String tableAlias);

    /**
     * 设置公共表表达式（CTE）为主表。
     *
     * <p>使用 {@link CteTable} 定义 CTE 作为查询的数据源。
     *
     * @param cteTable 公共表表达式对象
     * @return {@link TableRelation} 对象，用于定义表关系
     */
    public abstract TableRelation<?> from(CteTable cteTable);

    /**
     * 设置嵌套查询作为主表。
     *
     * <p>通过嵌套查询定义子查询作为主表，适合复杂数据源。
     *
     * @param nestedSelect 嵌套查询的定义，接受一个消费者来构建子查询
     * @param tableAlias   嵌套查询的别名
     * @return {@link TableRelation} 对象，用于定义表关系
     */
    public abstract TableRelation<?> from(Consumer<AbstractColumnReference> nestedSelect, String tableAlias);

    /**
     * 获取当前查询的规范对象。
     *
     * <p>提供对 {@link SelectSpecification} 的访问，便于内部状态管理。
     *
     * @return 当前查询的规范对象
     */
    protected SelectSpecification getSelectSpecification() {
        return selectSpecification;
    }
}