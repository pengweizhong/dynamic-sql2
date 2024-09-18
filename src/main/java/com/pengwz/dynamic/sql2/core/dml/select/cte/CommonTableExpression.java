package com.pengwz.dynamic.sql2.core.dml.select.cte;

import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommonTableExpression implements ICommonTableExpression {
    private List<CteTable> cteList = new ArrayList<>();

    public CommonTableExpression with(String cteName, Consumer<NestedSelect> nestedSelect) {
        cteList.add(new CteTable(cteName, nestedSelect));
        return this;
    }

    public CommonTableExpression withRecursive(String cteName, Consumer<NestedSelect> nestedSelect) {
        cteList.add(new CteTable(cteName, nestedSelect));
        return this;
    }

    public CteTable cteTable(String cteName) {
        return cteList.stream().filter(cte -> cte.getCteName().equals(cteName)).findFirst().orElse(null);
    }

}
