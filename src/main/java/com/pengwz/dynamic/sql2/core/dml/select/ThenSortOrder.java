package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.enums.SortOrder;

import java.util.ArrayList;
import java.util.List;

public class ThenSortOrder<R> implements IFetchable {

    private List<OrderBy> orderBys = new ArrayList<>();
    private IFetchable fetchable;

    public ThenSortOrder(IFetchable fetchable) {
        this.fetchable = fetchable;
    }

    public <T, F> ThenSortOrder<R> thenOrderBy(Fn<T, F> orderByValue, SortOrder sortOrder) {
        orderBys.add(new OrderBy(orderByValue, sortOrder));
        return this;
    }

    public ThenSortOrder<R> thenOrderBy(String orderingFragmentSql) {
        orderBys.add(new OrderBy(orderingFragmentSql));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public IFetchResult<R> fetch() {
        return fetchable.fetch();
    }

    @Override
    public <T> IFetchResult<T> fetch(Class<T> returnClass) {
        return fetchable.fetch(returnClass);
    }

}
