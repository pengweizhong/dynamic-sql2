package com.dynamic.sql.core.dml.select.build;


import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.core.dml.select.CollectionColumnMapping;
import com.dynamic.sql.core.dml.select.HavingCondition;
import com.dynamic.sql.core.dml.select.NestedMeta;
import com.dynamic.sql.core.dml.select.build.column.ColumnQuery;
import com.dynamic.sql.core.dml.select.build.join.JoinTable;
import com.dynamic.sql.core.dml.select.order.OrderBy;
import com.dynamic.sql.model.GroupByObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SelectSpecification {
    private NestedMeta nestedMeta;
    private List<ColumnQuery> columFunctions = new ArrayList<>();
    private List<JoinTable> joinTables = new ArrayList<>();
    private Consumer<WhereCondition> whereCondition;
    private GroupByObject groupByObject;
    private Consumer<HavingCondition> havingCondition;
    private List<OrderBy> orderBys;
    private LimitInfo limitInfo;
    //一对多映射关系
    private CollectionColumnMapping collectionColumnMapping;

    public List<ColumnQuery> getColumFunctions() {
        return columFunctions;
    }

    public List<JoinTable> getJoinTables() {
        return joinTables;
    }


    public GroupByObject getGroupByObject() {
        if (groupByObject == null) {
            groupByObject = new GroupByObject();
        }
        return groupByObject;
    }

    public Consumer<WhereCondition> getWhereCondition() {
        return whereCondition;
    }

    public void setWhereCondition(Consumer<WhereCondition> whereCondition) {
        this.whereCondition = whereCondition;
    }

    public Consumer<HavingCondition> getHavingCondition() {
        return havingCondition;
    }

    public void setHavingCondition(Consumer<HavingCondition> havingCondition) {
        this.havingCondition = havingCondition;
    }

    public LimitInfo getLimitInfo() {
        return limitInfo;
    }

    public void setLimitInfo(LimitInfo limitInfo) {
        this.limitInfo = limitInfo;
    }

    public List<OrderBy> getOrderBys() {
        if (orderBys == null) {
            orderBys = new ArrayList<>();
        }
        return orderBys;
    }

    public NestedMeta getNestedMeta() {
        return nestedMeta;
    }

    public void setNestedMeta(NestedMeta nestedMeta) {
        this.nestedMeta = nestedMeta;
    }

    public CollectionColumnMapping getCollectionColumnMapping() {
        return collectionColumnMapping;
    }

    public void setCollectionColumnMapping(CollectionColumnMapping collectionColumnMapping) {
        this.collectionColumnMapping = collectionColumnMapping;
    }
}
