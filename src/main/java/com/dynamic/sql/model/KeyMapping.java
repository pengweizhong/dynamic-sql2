package com.dynamic.sql.model;

import com.dynamic.sql.core.FieldFn;

/**
 * 表示主表与子表之间的主键-外键映射关系，用于集合查询（如一对多）的数据绑定。
 *
 * <p>通常用于动态 SQL 查询中，将主表记录与子表记录进行关联，
 * 并在结果中以集合方式呈现（如将多个子记录封装进主记录的一个字段中）。</p>
 *
 * @param <T> 主表实体类型
 * @param <C> 子表实体类型
 */
public interface KeyMapping<T, C> {

    /**
     * 获取主表用于关联的键（通常是主键字段）。
     *
     * @return 主表字段的方法引用，例如 Category::getCategoryId
     */
    FieldFn<T, ?> parentKey();

    /**
     * 获取子表用于关联的键（通常是外键字段）。
     *
     * @return 子表字段的方法引用，例如 Product::getCategoryId
     */
    FieldFn<C, ?> childKey();

    /**
     * 创建一个 KeyMapping 映射对象，用于建立主表与子表之间的键值对应关系。
     *
     * <p>例如：
     * <pre>{@code
     * KeyMapping.of(Category::getCategoryId, Product::getCategoryId)
     * }</pre>
     * 用于将 Category 的 categoryId 与 Product 的 categoryId 建立一对多关系。</p>
     *
     * @param parentKey 主表键字段引用
     * @param childKey  子表键字段引用
     * @param <T>       主表类型
     * @param <C>       子表类型
     * @return KeyMapping 映射对象
     */
    static <T, C> KeyMapping<T, C> of(FieldFn<T, ?> parentKey, FieldFn<C, ?> childKey) {
        return new DefaultKeyMapping<>(parentKey, childKey);
    }
}
