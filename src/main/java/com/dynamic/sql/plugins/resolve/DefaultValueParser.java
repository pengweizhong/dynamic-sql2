/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.resolve;

import java.util.HashMap;
import java.util.Map;

public class DefaultValueParser implements ValueParser {
    private final Map<String, String> config;

    public DefaultValueParser() {
        this.config = new HashMap<>();
    }

    public DefaultValueParser(Map<String, String> config) {
        this.config = config;
    }

    @Override
    public ValueResolver getValueResolver() {
        return new ValueResolver(config);
    }
}
