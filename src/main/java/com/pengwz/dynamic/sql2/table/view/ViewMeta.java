package com.pengwz.dynamic.sql2.table.view;

import com.pengwz.dynamic.sql2.table.FieldMeta;

import java.util.List;

public class ViewMeta {
    //列成员
    private List<ViewColumnMeta> viewColumnMetas;

    public <T extends FieldMeta> List<T> getViewColumnMetas() {
        return (List<T>) viewColumnMetas;
    }

    public void setViewColumnMetas(List<ViewColumnMeta> viewColumnMetas) {
        this.viewColumnMetas = viewColumnMetas;
    }
}
