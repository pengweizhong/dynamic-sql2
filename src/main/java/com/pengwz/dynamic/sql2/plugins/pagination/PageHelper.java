package com.pengwz.dynamic.sql2.plugins.pagination;

import java.util.Collection;
import java.util.function.Supplier;

import static com.pengwz.dynamic.sql2.plugins.pagination.LocalPage.remove;
import static com.pengwz.dynamic.sql2.plugins.pagination.LocalPage.setCurrentPage;

public class PageHelper {

    private final int pageIndex;
    private final int pageSize;

    private PageHelper(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public static PageHelper of(int pageIndex, int pageSize) {
        return new PageHelper(pageIndex, pageSize);
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public <C extends Collection<T>, T> PageInfo<C, T> selectPageInfo(Supplier<C> selectSupplier) {
        try {
            PageInfo<C, T> pageInfo = new PageInfo<>(pageIndex, pageSize);
            setCurrentPage(pageInfo);
            pageInfo.setRecords(selectSupplier.get());
            return pageInfo;
        } finally {
            remove();
        }
    }

}
