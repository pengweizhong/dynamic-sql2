package com.pengwz.dynamic.sql2.plugins.pagination;

import com.pengwz.dynamic.sql2.core.SqlContext;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class PageInfo<T> extends AbstractPage {
    private T records;

    protected PageInfo(int pageIndex, int pageSize) {
        super(pageIndex, pageSize);
    }

    @Override
    void setRecords(Supplier<?> selectSupplier) {
        records = (T) selectSupplier.get();
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
     * 查询下一页的记录并返回 {@code MapPage} 对象。
     * <p>
     * 此方法会将当前的页码 {@code pageIndex} 自增 ，然后执行查询 {@code Supplier}
     * 获取下一页的数据。该方法在实现时优化了查询逻辑，避免了每次都进行总记录数的统计（count 查询），
     * 从而提高查询性能。
     * </p>
     *
     * @param selectSupplier 查询方法，来源于{@link SqlContext#select()}
     * @return 返回下一页的 {@code MapPage} 对象，包含更新后的分页信息和查询结果。
     */
    public PageInfo<T> selectNextPage(Supplier<T> selectSupplier) {
        pageIndex++;
        return PageHelper.of(this).selectPage(selectSupplier);
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
