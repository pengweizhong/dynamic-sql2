/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.model;

import com.dynamic.sql.core.FieldFn;

/**
 * {@link KeyMapping} 的默认实现，用于表示主表与子表的键映射关系。
 *
 * @param <T> 主表类型，例如 Category
 * @param <C> 子表类型，例如 Product
 */
public class DefaultKeyMapping<T, C> implements KeyMapping<T, C> {
    private final FieldFn<T, ?> parentKey;
    private final FieldFn<C, ?> childKey;

    public DefaultKeyMapping(FieldFn<T, ?> parentKey, FieldFn<C, ?> childKey) {
        this.parentKey = parentKey;
        this.childKey = childKey;
    }

    @Override
    public FieldFn<T, ?> parentKey() {
        return parentKey;
    }

    @Override
    public FieldFn<C, ?> childKey() {
        return childKey;
    }
}
