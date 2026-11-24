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

import java.math.BigDecimal;

public class BigDecimalAttributeConverter extends AttributeConverterModel<BigDecimal> {
    private static final Logger log = LoggerFactory.getLogger(BigDecimalAttributeConverter.class);

    @Override
    public Object convertToDatabaseColumn(BigDecimal attribute) {
        return attribute;
    }

    @Override
    public BigDecimal convertToEntityAttribute(Object dbData) {
        try {
            if (dbData instanceof String) {
                return new BigDecimal(dbData.toString());
            }
            if (dbData instanceof Number) {
                Number number = (Number) dbData;
                return BigDecimal.valueOf(number.doubleValue());
            }
        } catch (Exception e) {
            log.error("", e);
        }
        throw new IllegalArgumentException("Cannot convert value `" + dbData + "` to type BigDecimal");
    }
}