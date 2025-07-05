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

import com.dynamic.sql.core.SqlContext;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class MapperScanner {
    private MapperScanner() {
    }

    private static final Logger log = LoggerFactory.getLogger(MapperScanner.class);

    public static void scanAndInitMapper(String[] scanMapperPackage, SqlContext sqlContext) {
        if (scanMapperPackage == null) {
            return;
        }
        for (String forPackage : scanMapperPackage) {
            log.debug("Find the mapper based on the provided '{}' path. ", forPackage);
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                    .setUrls(ClasspathHelper.forPackage(forPackage)) // 包路径
                    .filterInputsBy(new FilterBuilder().includePackage(forPackage)) // 只扫描指定包
                    .setScanners(Scanners.SubTypes)); // 扫描子类和子接口
            // 查找所有接口或类
            Set<Class<? extends EntityMapper>> allClasses = reflections.getSubTypesOf(EntityMapper.class);
            for (Class<? extends EntityMapper> mapperClass : allClasses) {
                MapperProxyFactory.loadMapper(mapperClass);
            }
        }
        MapperProxyFactory.setSqlContext(sqlContext);
        MapperProxyFactory.initEntityMapperMethod();
    }
}
