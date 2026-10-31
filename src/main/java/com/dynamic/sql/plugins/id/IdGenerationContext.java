package com.dynamic.sql.plugins.id;

import com.dynamic.sql.table.ColumnMeta;

/** 主键生成上下文，使自定义生成器能按实体、字段和数据源实现自己的策略。 */
public final class IdGenerationContext {
    private final Object entity;
    private final ColumnMeta column;
    private final String dataSourceName;
    public IdGenerationContext(Object entity,ColumnMeta column,String dataSourceName) {
        this.entity=entity;this.column=column;this.dataSourceName=dataSourceName;
    }
    public Object getEntity() { return entity; }
    public ColumnMeta getColumn() { return column; }
    public String getDataSourceName() { return dataSourceName; }
}
