/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.conversion;


public class DefaultAttributeConverter implements AttributeConverter {
    @Override
    public Object convertToDatabaseColumn(Object attribute) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object convertToEntityAttribute(Object dbData) {
        throw new UnsupportedOperationException();
    }
}
