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

import java.util.function.Consumer;

public interface ICommonTableExpression {

    ICommonTableExpression with(Class<?> cteClass, Consumer<NestedSelect> nestedSelect);

    ICommonTableExpression withRecursive(Class<?> cteClass, Consumer<NestedSelect> nestedSelect);

}
