package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.enums.SortOrder;

import java.util.ArrayList;
import java.util.List;

public class ThenSortOrder implements IFetchable {

    private List<OrderBy> orderBys = new ArrayList<>();
    private IFetchable fetchable;

    public ThenSortOrder(IFetchable fetchable) {
        this.fetchable = fetchable;
    }

    public <T, F> ThenSortOrder thenOrderBy(Fn<T, F> orderByValue, SortOrder sortOrder) {
        orderBys.add(new OrderBy(orderByValue, sortOrder));
        return this;
    }

    public ThenSortOrder thenOrderBy(String orderingFragment, SortOrder sortOrder) {
        orderBys.add(new OrderBy(orderingFragment, sortOrder));
        return this;
    }

    @Override
    public <R> IFetchResult<R> fetch() {
        return fetchable.fetch();
    }

    @Override
    public <T> IFetchResult<T> fetch(Class<T> returnClass) {
        return fetchable.fetch(returnClass);
    }
}
