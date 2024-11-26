package com.pengwz.dynamic.sql2.table;

public class ColumnMeta extends FieldMeta {
    //是否为主键
    private boolean isPrimary;
    //主键生成策略
    private GeneratedStrategy generatedStrategy;


    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public GeneratedStrategy getGeneratedStrategy() {
        return generatedStrategy;
    }

    public void setGeneratedStrategy(GeneratedStrategy generatedStrategy) {
        this.generatedStrategy = generatedStrategy;
    }

}
