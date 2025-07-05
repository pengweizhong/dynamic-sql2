/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.pagination;


import com.dynamic.sql.core.SqlContext;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class PageInfo<T> extends AbstractPage {
    private T records;
    //查询语句
    private Supplier<T> selectSupplier;

    protected PageInfo(int pageIndex, int pageSize) {
        super(pageIndex, pageSize);
    }

    @Override
    void setRecords(Supplier<?> selectSupplier) {
        this.selectSupplier = (Supplier<T>) selectSupplier;
        records = this.selectSupplier.get();
    }

    public T getRecords() {
        return records;
    }

    @Override
    public int getRealSize() {
        if (records instanceof Collection) {
            return ((Collection) records).size();
        }
        if (records instanceof Map) {
            return ((Map) records).size();
        }
        throw new IllegalStateException("Unsupported records type: " + records.getClass().getCanonicalName());
    }

    /**
     * 判断当前分页结果是否为空。
     * <p>
     * 该方法检查分页查询结果（{@link #records}）的实际大小。如果结果集是 {@link Collection} 或 {@link Map} 类型，
     * 则根据其大小判断；如果结果集类型不受支持，将抛出异常。
     * </p>
     *
     * @return {@code true} 如果当前分页结果为空（大小为 0）；{@code false} 如果结果非空（大小大于 0）。
     * @throws IllegalStateException 如果 {@link #records} 的类型不受支持（非 Collection 或 Map）。
     * @see #getRealSize() 获取实际记录数。
     * @see #records 存储的分页记录。
     */
    public boolean isEmpty() {
        return getRealSize() <= 0;
    }

    /**
     * 判断当前分页结果是否非空。
     * <p>
     * 该方法是 {@link #isEmpty()} 的逻辑否定，用于检查分页查询结果（{@link #records}）是否包含任何记录。
     * 如果结果集是 {@link Collection} 或 {@link Map} 类型，则根据其大小判断；如果结果集类型不受支持，将抛出异常。
     * </p>
     *
     * @return {@code true} 如果当前分页结果非空（大小大于 0）；{@code false} 如果结果为空（大小为 0）。
     * @throws IllegalStateException 如果 {@link #records} 的类型不受支持（非 Collection 或 Map）。
     * @see #isEmpty() 检查结果是否为空。
     * @see #getRealSize() 获取实际记录数。
     * @see #records 存储的分页记录。
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * 查询下一页的记录并返回相同的对象。
     * <p>
     * 此方法会将当前的页码 {@code pageIndex} 自增 ，然后执行查询 {@code Supplier}
     * 获取下一页的数据。该方法在实现时优化了查询逻辑，避免了每次都进行总记录数的统计（count 查询），
     * 从而提高查询性能。
     * </p>
     *
     * @param selectSupplier 查询方法，来源于{@link SqlContext#select()}
     * @return 返回下一页的 {@code MapPage} 对象，包含更新后的分页信息和查询结果。
     * @see PageInfo#selectNextPage()
     * @deprecated 方法设计不合理，不需要重新返回对象，也不需要重复传递查询语句
     */
    @Deprecated
    public PageInfo<T> selectNextPage(Supplier<T> selectSupplier) {
        pageIndex++;
        return PageHelper.of(this).selectPage(selectSupplier);
    }

    /**
     * 移动到下一页并查询数据，更新当前对象。
     * <p>
     * 此方法会将当前页码 {@code pageIndex} 自增，并使用已存储的查询方法 {@code selectSupplier}
     * 获取下一页的数据。调用后，当前 {@code PageInfo} 对象将被更新，而不会返回新的对象。
     * <p>
     * 该方法优化了查询逻辑，避免每次分页查询时重复统计总记录数（count 查询），
     * 适用于高性能分页场景。
     * </p>
     *
     * <p><strong>注意：</strong>调用此方法后，当前对象的数据会被更新，调用者无需重新赋值。</p>
     */
    public void selectNextPage() {
        pageIndex++;
        PageHelper.of(this).selectPage(selectSupplier);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageInfo{");
        sb.append("pageIndex=").append(pageIndex);
        sb.append(", pageSize=").append(pageSize);
        sb.append(", realSize=").append(getRealSize());
        sb.append(", total=").append(total);
        sb.append(", totalPage=").append(totalPage);
        sb.append(", records=").append(records);
        sb.append('}');
        return sb.toString();
    }
}
