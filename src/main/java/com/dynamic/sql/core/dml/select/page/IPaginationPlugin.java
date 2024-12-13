package com.dynamic.sql.core.dml.select.page;

import javax.management.Query;

public interface IPaginationPlugin<R> {
    R applyPagination(Query query, int pageNumber, int pageSize);
}
