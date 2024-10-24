package com.pengwz.dynamic.sql2.plugins.pagination;

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


    protected MapPage(int pageIndex, int pageSize, long total) {
        super(pageIndex, pageSize);
        this.total = total;
    }


    public M getRecords() {
        return records;
    }

    public int getRealSize() {
        return records.size();
    }

    /**
     * 是否有上一页
     */
    public boolean hasPreviousPage() {
        return getPageIndex() > 1;
    }

    /**
     * 是否有下一页
     */
    public boolean hasNextPage() {
        return getPageIndex() < totalPage;
    }


    protected void setRecords(M records) {
        this.records = records;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PageInfo{");
        sb.append("pageIndex=").append(pageIndex);
        sb.append(", pageSize=").append(pageSize);
        sb.append(", total=").append(total);
        sb.append(", totalPage=").append(totalPage);
        sb.append(", records=").append(records);
        sb.append('}');
        return sb.toString();
    }
}
