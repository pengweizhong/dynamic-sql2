package com.dynamic.sql.plugins.pagination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public abstract class AbstractPage {
    protected static final Logger log = LoggerFactory.getLogger(AbstractPage.class);
    // 当前页码，从 1 开始
    protected int pageIndex;
    //当前页数量
    protected final int pageSize;
    //总数量
    protected Long total;
    //总分页数量
    protected int totalPage;

    protected AbstractPage(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public final int getPageIndex() {
        return pageIndex;
    }

    public final int getPageSize() {
        return pageSize;
    }

    abstract void setRecords(Supplier<?> selectSupplier);

    abstract int getRealSize();

    /**
     * 判断是否有上一页
     */
    public final boolean hasPreviousPage() {
        return getPageIndex() > 1;
    }

    /**
     * 判断是否有下一页
     * <p>
     * 该方法首先判断当前页码是否小于总页数，然后检查当前页的实际记录数是否不为 0，
     * 以避免在最后一页没有记录时仍然提示有下一页的情况。
     * 比如极端情况下递归查询中，数据突然被大量删除
     * <p>
     * 如果当前页小于总页数且当前页有数据，则返回 true，表示有下一页；
     * 否则返回 false。
     * </p>
     *
     * @return 是否有下一页
     */
    public final boolean hasNextPage() {
        if (getPageIndex() < totalPage) {
            if (getRealSize() > 0) {
                return true;
            } else {
                log.warn("Expected to return data, but the current page data is empty.");
            }
        }
        return false;
    }

    public long getTotal() {
        if (total == null) {
            return 0;
        }
        return total;
    }

    public final int getTotalPage() {
        return totalPage;
    }

    protected void setTotal(long total) {
        this.total = total;
    }

    protected void initTotalPage() {
        long pages = (total + pageSize - 1L) / pageSize;
        totalPage = (int) pages;
    }

    protected Long getCacheTotal() {
        return total;
    }

}
