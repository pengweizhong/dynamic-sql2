/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.table.cte;

public class CTEEntityMapping {
    private String cteName;
    private boolean isCache;
    private Class<?> cteClass;
    public String getCteName() {
        return cteName;
    }

    public void setCteName(String cteName) {
        this.cteName = cteName;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public Class<?> getCteClass() {
        return cteClass;
    }

    public void setCteClass(Class<?> cteClass) {
        this.cteClass = cteClass;
    }
}