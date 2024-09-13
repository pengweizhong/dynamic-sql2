package com.pengwz.dynamic.sql2.core.dml.select.page;

import javax.management.Query;

public interface IPaginationPlugin<R> {
    R applyPagination(Query query, int pageNumber, int pageSize);
}
