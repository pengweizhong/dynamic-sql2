/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.conversion.attribute;

import com.dynamic.sql.plugins.conversion.AttributeConverterModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;


public class LocalDateAttributeConverter extends AttributeConverterModel<LocalDate> {
    private static final Logger log = LoggerFactory.getLogger(LocalDateAttributeConverter.class);

    @Override
    public Object convertToDatabaseColumn(LocalDate attribute) {
        if (attribute == null) {
            return null;
        }
        return Date.valueOf(attribute);
    }

    @Override
    public LocalDate convertToEntityAttribute(Object dbData) {
        if (dbData == null) {
            return null;
        }
        if (dbData instanceof Date) {
            Date sqlDate = (Date) dbData;
            return sqlDate.toLocalDate();
        }
        if (dbData instanceof Timestamp) {
            Timestamp sqlTimestamp = (Timestamp) dbData;
            return sqlTimestamp.toLocalDateTime().toLocalDate();
        }
        throw new IllegalArgumentException("Cannot convert value `" + dbData + "` to type LocalDate");
    }
}