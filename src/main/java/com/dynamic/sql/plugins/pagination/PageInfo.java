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
     * 查询下一页的记录并返回相同的对象。
     * <p>
     * 此方法会将当前的页码 {@code pageIndex} 自增 ，然后执行查询 {@code Supplier}
     * 获取下一页的数据。该方法在实现时优化了查询逻辑，避免了每次都进行总记录数的统计（count 查询），
     * 从而提高查询性能。
     * </p>
     *
     * @param selectSupplier 查询方法，来源于{@link SqlContext#select()}
     * @return 返回下一页的 {@code MapPage} 对象，包含更新后的分页信息和查询结果。
     * @see this#selectNextPage()
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
