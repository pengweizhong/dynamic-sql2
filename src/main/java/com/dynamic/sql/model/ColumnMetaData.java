package com.dynamic.sql.model;

/**
 * 表示数据库表列元数据的类，用于存储列的详细信息。
 * 通过 {@link java.sql.DatabaseMetaData#getColumns} 获取的列元数据封装在此类中。
 * 注意：某些字段（如 SCOPE_CATALOG、SOURCE_DATA_TYPE）在 MySQL 中通常为空，
 * 因为 MySQL 不支持这些高级元数据概念。
 */
public class ColumnMetaData {
    // 表的目录，可能为 null（MySQL 中通常为数据库名）
    private String tableCatalog;
    // 表的模式，可能为 null（MySQL 中通常为空）
    private String tableSchema;
    // 表名
    private String tableName;
    // 列名
    private String columnName;
    // SQL 数据类型编号（参考 java.sql.Types）
    private int dataType;
    // 数据类型名称（如 "INT", "VARCHAR"）
    private String typeName;
    // 列大小（字符最大长度或数字精度）
    private int columnSize;
    // 小数位数（对于数字类型）
    private Integer decimalDigits;
    // 基数（通常为 10 或 2）
    private Integer numPrecRadix;
    // 是否可空（0 = NO, 1 = YES, 2 = UNKNOWN）
    private int nullable;
    // 列的注释或描述，可能为 null
    private String remarks;
    // 默认值，可能为 null
    private String columnDef;
    // 列在表中的位置（从 1 开始）
    private int ordinalPosition;
    // 是否可空（"YES" 或 "NO"）
    private String isNullable;
    // MySQL 通常不支持的字段，可能为空
    private String scopeCatalog;
    private String scopeSchema;
    private String scopeTable;
    private Short sourceDataType;

    /**
     * 获取表的目录名称。
     *
     * @return 目录名称，可能为 null
     */
    public String getTableCatalog() {
        return tableCatalog;
    }

    /**
     * 设置表的目录名称。
     *
     * @param tableCatalog 目录名称
     */
    public void setTableCatalog(String tableCatalog) {
        this.tableCatalog = tableCatalog;
    }

    /**
     * 获取表的模式名称。
     *
     * @return 模式名称，可能为 null
     */
    public String getTableSchema() {
        return tableSchema;
    }

    /**
     * 设置表的模式名称。
     *
     * @param tableSchema 模式名称
     */
    public void setTableSchema(String tableSchema) {
        this.tableSchema = tableSchema;
    }

    /**
     * 获取表名。
     *
     * @return 表名，不为 null
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * 设置表名。
     *
     * @param tableName 表名
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * 获取列名。
     *
     * @return 列名，不为 null
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * 设置列名。
     *
     * @param columnName 列名
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * 获取列的 SQL 数据类型编号。
     *
     * @return 数据类型编号（参考 java.sql.Types）
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * 设置列的 SQL 数据类型编号。
     *
     * @param dataType 数据类型编号
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * 获取数据类型名称。
     *
     * @return 数据类型名称（如 "INT", "VARCHAR"），可能为 null
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * 设置数据类型名称。
     *
     * @param typeName 数据类型名称
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 获取列大小。
     *
     * @return 列大小（字符最大长度或数字精度）
     */
    public int getColumnSize() {
        return columnSize;
    }

    /**
     * 设置列大小。
     *
     * @param columnSize 列大小
     */
    public void setColumnSize(int columnSize) {
        this.columnSize = columnSize;
    }

    /**
     * 获取小数位数。
     *
     * @return 小数位数，可能为 null（非数字类型）
     */
    public Integer getDecimalDigits() {
        return decimalDigits;
    }

    /**
     * 设置小数位数。
     *
     * @param decimalDigits 小数位数
     */
    public void setDecimalDigits(Integer decimalDigits) {
        this.decimalDigits = decimalDigits;
    }

    /**
     * 获取基数。
     *
     * @return 基数，可能为 null
     */
    public Integer getNumPrecRadix() {
        return numPrecRadix;
    }

    /**
     * 设置基数。
     *
     * @param numPrecRadix 基数
     */
    public void setNumPrecRadix(Integer numPrecRadix) {
        this.numPrecRadix = numPrecRadix;
    }

    /**
     * 获取是否可空。
     *
     * @return 是否可空（0 = NO, 1 = YES, 2 = UNKNOWN）
     */
    public int getNullable() {
        return nullable;
    }

    /**
     * 设置是否可空。
     *
     * @param nullable 是否可空
     */
    public void setNullable(int nullable) {
        this.nullable = nullable;
    }

    /**
     * 获取列的注释。
     *
     * @return 列的注释，可能为 null
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置列的注释。
     *
     * @param remarks 列的注释
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取列的默认值。
     *
     * @return 默认值，可能为 null
     */
    public String getColumnDef() {
        return columnDef;
    }

    /**
     * 设置列的默认值。
     *
     * @param columnDef 默认值
     */
    public void setColumnDef(String columnDef) {
        this.columnDef = columnDef;
    }

    /**
     * 获取列在表中的位置。
     *
     * @return 位置（从 1 开始）
     */
    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    /**
     * 设置列在表中的位置。
     *
     * @param ordinalPosition 位置
     */
    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    /**
     * 获取是否可空（字符串形式）。
     *
     * @return "YES" 或 "NO"，可能为 null
     */
    public String getIsNullable() {
        return isNullable;
    }

    /**
     * 设置是否可空（字符串形式）。
     *
     * @param isNullable "YES" 或 "NO"
     */
    public void setIsNullable(String isNullable) {
        this.isNullable = isNullable;
    }

    /**
     * 获取引用范围的目录（MySQL 中通常为空）。
     *
     * @return 引用范围目录，可能为 null
     */
    public String getScopeCatalog() {
        return scopeCatalog;
    }

    /**
     * 设置引用范围的目录。
     *
     * @param scopeCatalog 引用范围目录
     */
    public void setScopeCatalog(String scopeCatalog) {
        this.scopeCatalog = scopeCatalog;
    }

    /**
     * 获取引用范围的模式（MySQL 中通常为空）。
     *
     * @return 引用范围模式，可能为 null
     */
    public String getScopeSchema() {
        return scopeSchema;
    }

    /**
     * 设置引用范围的模式。
     *
     * @param scopeSchema 引用范围模式
     */
    public void setScopeSchema(String scopeSchema) {
        this.scopeSchema = scopeSchema;
    }

    /**
     * 获取引用范围的表（MySQL 中通常为空）。
     *
     * @return 引用范围表，可能为 null
     */
    public String getScopeTable() {
        return scopeTable;
    }

    /**
     * 设置引用范围的表。
     *
     * @param scopeTable 引用范围表
     */
    public void setScopeTable(String scopeTable) {
        this.scopeTable = scopeTable;
    }

    /**
     * 获取源数据类型（MySQL 中通常为空）。
     *
     * @return 源数据类型，可能为 null
     */
    public Short getSourceDataType() {
        return sourceDataType;
    }

    /**
     * 设置源数据类型。
     *
     * @param sourceDataType 源数据类型
     */
    public void setSourceDataType(Short sourceDataType) {
        this.sourceDataType = sourceDataType;
    }

    @Override
    public String toString() {
        return "ColumnMetaData{" +
                "tableCatalog='" + tableCatalog + '\'' +
                ", tableSchema='" + tableSchema + '\'' +
                ", tableName='" + tableName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", dataType=" + dataType +
                ", typeName='" + typeName + '\'' +
                ", columnSize=" + columnSize +
                ", decimalDigits=" + decimalDigits +
                ", numPrecRadix=" + numPrecRadix +
                ", nullable=" + nullable +
                ", remarks='" + remarks + '\'' +
                ", columnDef='" + columnDef + '\'' +
                ", ordinalPosition=" + ordinalPosition +
                ", isNullable='" + isNullable + '\'' +
                ", scopeCatalog='" + scopeCatalog + '\'' +
                ", scopeSchema='" + scopeSchema + '\'' +
                ", scopeTable='" + scopeTable + '\'' +
                ", sourceDataType=" + sourceDataType +
                '}';
    }
}