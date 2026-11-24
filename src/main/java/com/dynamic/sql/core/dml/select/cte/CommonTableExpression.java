/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.cte;


import com.dynamic.sql.core.dml.select.NestedSelect;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommonTableExpression implements ICommonTableExpression {
    private List<CteTable> cteList = new ArrayList<>();

    public CommonTableExpression with(Class<?> cteClass, Consumer<NestedSelect> nestedSelect) {
        cteList.add(new CteTable(cteClass, nestedSelect));
        return this;
    }

    public CommonTableExpression withRecursive(Class<?> cteClass, Consumer<NestedSelect> nestedSelect) {
        cteList.add(new CteTable(cteClass, nestedSelect));
        return this;
    }

    public CteTable cteTable(Class<?> cteClass) {
        return cteList.stream().filter(cte -> cte.getCteClass().equals(cteClass)).findFirst().orElse(null);
    }

}
