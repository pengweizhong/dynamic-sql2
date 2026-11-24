/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.table;


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
