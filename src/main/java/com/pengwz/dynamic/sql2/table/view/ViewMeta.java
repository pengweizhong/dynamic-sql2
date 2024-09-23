package com.pengwz.dynamic.sql2.table.view;

import java.util.List;

public class ViewMeta {
    //列成员
    private List<ViewColumnMeta> viewColumnMetas;

    public List<ViewColumnMeta> getViewColumnMetas() {
        return viewColumnMetas;
    }

    public void setViewColumnMetas(List<ViewColumnMeta> viewColumnMetas) {
        this.viewColumnMetas = viewColumnMetas;
    }
}
