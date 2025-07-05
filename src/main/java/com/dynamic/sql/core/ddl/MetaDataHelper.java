/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.ddl;

import com.dynamic.sql.datasource.DataSourceProvider;

public class MetaDataHelper {
    private final String dataSourceName;
    private final String catalog;

    public MetaDataHelper(String dataSourceName, String catalog) {
        this.dataSourceName = dataSourceName == null ? DataSourceProvider.getDefaultDataSourceName() : dataSourceName;
        this.catalog = catalog;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public String getCatalog() {
        return catalog;
    }

}
