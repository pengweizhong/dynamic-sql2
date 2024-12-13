package com.dynamic.sql.table.cte;


import java.util.List;

public class CTEMeta {
    //CTE名称
    private String cteName;
    //    //CTE别名
//    private String cteAlias;
    //列成员
    private List<CTEColumnMeta> cteColumnMetas;

    public String getCteName() {
        return cteName;
    }

    public void setCteName(String cteName) {
        this.cteName = cteName;
    }

    public List<CTEColumnMeta> getCteColumnMetas() {
        return cteColumnMetas;
    }

    public void setCteColumnMetas(List<CTEColumnMeta> cteColumnMetas) {
        this.cteColumnMetas = cteColumnMetas;
    }
}
