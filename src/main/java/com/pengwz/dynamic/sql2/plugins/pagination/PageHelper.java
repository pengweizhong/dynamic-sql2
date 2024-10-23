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

    public <T, Y> PageInfo<Y> doSelectPageInfo(Supplier<T> supplier) {
        PageInfo<Y> pageInfo = new PageInfo(pageIndex, pageSize);
        PageSqlInterceptor interceptor = (PageSqlInterceptor) SqlInterceptorChain.getInstance()
                .getInterceptor(PageSqlInterceptor.class);
        if (interceptor == null) {
            throw new IllegalStateException("SQL paging plugin not found.");
        }
        try {
            interceptor.setCurrentPage(pageInfo);
            T t = supplier.get();
            if (t instanceof Collection) {
                Collection collection = (Collection) t;
                pageInfo.setRecords(collection);
            }
        } finally {
            interceptor.removeCurrentPage();
        }

        return pageInfo;
    }
}
