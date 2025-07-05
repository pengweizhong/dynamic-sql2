/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.mapper;


public class MapperRegistry<T> {

    private final EntityMapper<T> proxyMapper;
    private final Class<T> entityClass;

    public MapperRegistry(EntityMapper<T> proxyMapper, Class<T> entityClass) {
        this.proxyMapper = proxyMapper;
        this.entityClass = entityClass;
    }

    public EntityMapper<T> getProxyMapper() {
        return proxyMapper;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }
}
