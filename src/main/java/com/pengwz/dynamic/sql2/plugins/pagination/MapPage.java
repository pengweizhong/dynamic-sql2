package com.pengwz.dynamic.sql2.plugins.pagination;

import com.pengwz.dynamic.sql2.core.SqlContext;

import java.util.Map;
import java.util.function.Supplier;

public class MapPage<K, V, M extends Map<K, V>> extends AbstractPage {
    //分页结果
    private M records;

    public MapPage(int pageIndex, int pageSize) {
        super(pageIndex, pageSize);
    }

    @Override
    @SuppressWarnings("unchecked")
    void setRecords(Supplier<?> selectSupplier) {
        records = (M) selectSupplier.get();
    }


    public M getRecords() {
        return records;
    }

    @Override
    public int getRealSize() {
        return records.size();
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
    public MapPage<K, V, M> selectNextPage(Supplier<M> selectSupplier) {
        pageIndex++;
        return PageHelper.ofMap(this).selectPage(selectSupplier);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MapPage{");
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