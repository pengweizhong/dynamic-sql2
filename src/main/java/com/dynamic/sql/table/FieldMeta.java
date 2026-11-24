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


import com.dynamic.sql.plugins.conversion.AttributeConverter;

import java.lang.reflect.Field;

public class FieldMeta {
    //数据库字段名
    private String columnName;
    //实体类字段
    private Field field;
    //转换策略
    private Class<? extends AttributeConverter> converter;
    //写库字段格式化
    private String format;
    //SRID
    private int srid;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Class<? extends AttributeConverter> getConverter() {
        return converter;
    }

    public void setConverter(Class<? extends AttributeConverter> converter) {
        this.converter = converter;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getSrid() {
        return srid;
    }

    public void setSrid(int srid) {
        this.srid = srid;
    }
}
