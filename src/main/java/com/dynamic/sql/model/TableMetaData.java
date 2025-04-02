package com.dynamic.sql.model;

/**
 * 表示数据库表元数据的类，用于存储表的详细信息。
 * 通过 {@link java.sql.DatabaseMetaData#getTables} 获取的元数据封装在此类中。
 * 注意：某些字段（如 typeCat、typeSchem、typeName、selfReferencingColName、refGeneration）在 MySQL 中通常为空，
 * 因为 MySQL 不支持这些高级元数据概念。
 */
public class TableMetaData {

    // 目录，数据库的目录名称，可能为 null（MySQL 中通常为数据库名）
    private String tableCatalog;
    // 模式，数据库的模式名称，可能为 null（MySQL 中通常为空）
    private String tableSchema;
    // 表名，表的名称
    private String tableName;
    // 表类型，表的类型（如 "TABLE", "VIEW"），始终有效
    private String tableType;
    // 备注，表的注释或描述，可能为 null
    private String remarks;
    // 类型目录，通常为空（MySQL 不支持）
    private String typeCat;
    // 类型模式，通常为空（MySQL 不支持）
    private String typeSchem;
    // 类型名称，通常为空（MySQL 不支持）
    private String typeName;
    // 自引用列名，通常为空（MySQL 不支持自引用元数据）
    private String selfReferencingColName;
    // 引用生成规则，通常为空（MySQL 不支持）
    private String refGeneration;

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
     * 获取表类型。
     *
     * @return 表类型（如 "TABLE", "VIEW"），不为 null
     */
    public String getTableType() {
        return tableType;
    }

    /**
     * 设置表类型。
     *
     * @param tableType 表类型
     */
    public void setTableType(String tableType) {
        this.tableType = tableType;
    }

    /**
     * 获取表的备注。
     *
     * @return 备注，可能为 null
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * 设置表的备注。
     *
     * @param remarks 备注
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * 获取类型目录（MySQL 中通常为空）。
     *
     * @return 类型目录，可能为 null
     */
    public String getTypeCat() {
        return typeCat;
    }

    /**
     * 设置类型目录。
     *
     * @param typeCat 类型目录
     */
    public void setTypeCat(String typeCat) {
        this.typeCat = typeCat;
    }

    /**
     * 获取类型模式（MySQL 中通常为空）。
     *
     * @return 类型模式，可能为 null
     */
    public String getTypeSchem() {
        return typeSchem;
    }

    /**
     * 设置类型模式。
     *
     * @param typeSchem 类型模式
     */
    public void setTypeSchem(String typeSchem) {
        this.typeSchem = typeSchem;
    }

    /**
     * 获取类型名称（MySQL 中通常为空）。
     *
     * @return 类型名称，可能为 null
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * 设置类型名称。
     *
     * @param typeName 类型名称
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * 获取自引用列名（MySQL 中通常为空）。
     *
     * @return 自引用列名，可能为 null
     */
    public String getSelfReferencingColName() {
        return selfReferencingColName;
    }

    /**
     * 设置自引用列名。
     *
     * @param selfReferencingColName 自引用列名
     */
    public void setSelfReferencingColName(String selfReferencingColName) {
        this.selfReferencingColName = selfReferencingColName;
    }

    /**
     * 获取引用生成规则（MySQL 中通常为空）。
     *
     * @return 引用生成规则，可能为 null
     */
    public String getRefGeneration() {
        return refGeneration;
    }

    /**
     * 设置引用生成规则。
     *
     * @param refGeneration 引用生成规则
     */
    public void setRefGeneration(String refGeneration) {
        this.refGeneration = refGeneration;
    }

    @Override
    public String toString() {
        return "TableMetaData{" +
                "tableCatalog='" + tableCatalog + '\'' +
                ", tableSchema='" + tableSchema + '\'' +
                ", tableName='" + tableName + '\'' +
                ", tableType='" + tableType + '\'' +
                ", remarks='" + remarks + '\'' +
                ", typeCat='" + typeCat + '\'' +
                ", typeSchem='" + typeSchem + '\'' +
                ", typeName='" + typeName + '\'' +
                ", selfReferencingColName='" + selfReferencingColName + '\'' +
                ", refGeneration='" + refGeneration + '\'' +
                '}';
    }
}