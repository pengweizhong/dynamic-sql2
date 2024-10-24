package com.pengwz.dynamic.sql2.plugins.pagination;

import java.util.Collection;
import java.util.function.Supplier;

import static com.pengwz.dynamic.sql2.plugins.pagination.LocalPage.setCurrentPage;

public class PageHelper {

    final PageInfo<?, ?> pageInfo;

    private PageHelper(int pageIndex, int pageSize) {
        pageInfo = new PageInfo<>(pageIndex, pageSize);
        setCurrentPage(pageInfo);
    }

    public static PageHelper of(int pageIndex, int pageSize) {
        return new PageHelper(pageIndex, pageSize);
    }

    @SuppressWarnings("unchecked")
    public <C extends Collection<T>, T> PageInfo<C, T> selectPageInfo(Supplier<C> selectSupplier) {
        PageInfo<C, T> page = (PageInfo<C, T>) pageInfo;
        page.setRecords(selectSupplier.get());
        return page;
    }

}
