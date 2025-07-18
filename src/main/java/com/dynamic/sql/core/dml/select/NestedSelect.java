/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.dml.select.build.SelectSpecification;

public class NestedSelect {
   Select select = new Select();

    public SelectSpecification getSelectSpecification() {
        return select.getSelectSpecification();
    }
}
