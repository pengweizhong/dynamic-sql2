package com.pengwz.dynamic.sql2.plugins.pagination;

import com.pengwz.dynamic.sql2.interceptor.SqlInterceptorChain;

import java.util.Collection;
import java.util.function.Supplier;

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
        PageInfo<C, T> pageInfo = new PageInfo<>(pageIndex, pageSize);
        PageSqlInterceptor interceptor = getPageSqlInterceptor();
        try {
            interceptor.setCurrentPage(pageInfo);
            C apply = selectSupplier.get();
            pageInfo.setRecords(apply);
        } finally {
            interceptor.removeCurrentPage();
        }
        return pageInfo;
    }


    private PageSqlInterceptor getPageSqlInterceptor() {
        PageSqlInterceptor interceptor = (PageSqlInterceptor) SqlInterceptorChain.getInstance()
                .getInterceptor(PageSqlInterceptor.class);
        if (interceptor == null) {
            throw new IllegalStateException("SQL paging plugin not found.");
        }
        return interceptor;
    }
}
