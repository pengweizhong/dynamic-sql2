/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.pagination;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.core.condition.WhereCondition;
import com.dynamic.sql.exception.DynamicSqlException;
import com.dynamic.sql.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.dynamic.sql.plugins.pagination.LocalPage.clearCurrentPage;
import static com.dynamic.sql.plugins.pagination.LocalPage.setCurrentPage;


/**
 * PageHelper 用于处理不同类型的数据分页逻辑的辅助类。
 * <p>
 * {@link PageHelper#of(int, int)}：通用分页逻辑<br>
 * {@link PageHelper#ofCollection(int, int)}：集合类型分页逻辑<br>
 * {@link PageHelper#ofMap(int, int)}：Map类型分页逻辑
 */
public class PageHelper {
    private static final Logger log = LoggerFactory.getLogger(PageHelper.class);

    private PageHelper() {
    }

    /**
     * 创建一个通用的 GeneralPageHelper 实例，指定页码和页大小。
     *
     * @param pageIndex 要获取的页码（如果小于等于0，则页码默认为1）
     * @param pageSize  每页的大小（每页的项目数量）
     * @return GeneralPageHelper 实例，用于通用的分页处理
     * @throws IllegalArgumentException 如果 pageSize 小于等于0
     */
    public static GeneralPageHelper of(int pageIndex, int pageSize) {
        checkPageParams(pageIndex, pageSize);
        return new GeneralPageHelper(Math.max(pageIndex, 1), pageSize, DefaultPagePluginType.DYNAMIC_SQL2.getPluginName());
    }

    /**
     * 创建一个用于 MyBatis 分页插件的分页实例。
     *
     * @param pageIndex 页码，从 1 开始（若小于等于 0 则默认为 1）
     * @param pageSize  每页大小（必须大于 0）
     * @return 配置完成的 {@link GeneralPageHelper} 实例
     * @throws IllegalArgumentException 如果 {@code pageSize} 小于等于 0
     */
    public static GeneralPageHelper ofMybatis(int pageIndex, int pageSize) {
        checkPageParams(pageIndex, pageSize);
        return new GeneralPageHelper(Math.max(pageIndex, 1), pageSize, DefaultPagePluginType.MYBATIS.getPluginName());
    }

    /**
     * 创建一个 LogicPageHelper 实例，指定页码和页大小。
     * <p>
     * LogicPageHelper 用于对集合进行内存分页，不依赖数据库插件。
     * </p>
     *
     * @param pageIndex 页码（从 1 开始，若小于等于 0 则默认为 1）
     * @param pageSize  每页大小（必须大于 0）
     * @return LogicPageHelper 实例，用于集合的逻辑分页处理
     * @throws IllegalArgumentException 如果 {@code pageSize} 小于等于 0
     */
    public static LogicPageHelper ofLogic(int pageIndex, int pageSize) {
        checkPageParams(pageIndex, pageSize);
        return new LogicPageHelper(Math.max(pageIndex, 1), pageSize);
    }

    /**
     * 创建一个 CollectionPageHelper 实例，指定页码和页大小。
     * 针对处理集合类型的分页。
     *
     * @param pageIndex 要获取的页码（如果小于等于0，则页码默认为1）
     * @param pageSize  每页的大小（每页的项目数量）
     * @return CollectionPageHelper 实例，用于集合的分页处理
     * @throws IllegalArgumentException 如果 pageSize 小于等于0
     */
    @Deprecated
    public static CollectionPageHelper ofCollection(int pageIndex, int pageSize) {
        checkPageParams(pageIndex, pageSize);
        return new CollectionPageHelper(Math.max(pageIndex, 1), pageSize);
    }

    /**
     * 创建一个 MapPageHelper 实例，指定页码和页大小。
     * 针对处理 Map 类型的分页。
     *
     * @param pageIndex 要获取的页码（如果小于等于0，则页码默认为1）
     * @param pageSize  每页的大小（每页的项目数量）
     * @return MapPageHelper 实例，用于 Map 的分页处理
     * @throws IllegalArgumentException 如果 pageSize 小于等于0
     */
    @Deprecated
    public static MapPageHelper ofMap(int pageIndex, int pageSize) {
        checkPageParams(pageIndex, pageSize);
        return new MapPageHelper(Math.max(pageIndex, 1), pageSize);
    }

    static <T> GeneralPageHelper of(PageInfo<T> pageInfo) {
        return new GeneralPageHelper(pageInfo);
    }

    @Deprecated
    static <C extends Collection<T>, T> CollectionPageHelper ofCollection(CollectionPage<C, T> collectionPage) {
        return new CollectionPageHelper(collectionPage);
    }

    @Deprecated
    static <K, V, M extends Map<K, V>> MapPageHelper ofMap(MapPage<K, V, M> mapPage) {
        return new MapPageHelper(mapPage);
    }

    private static void checkPageParams(int pageIndex, int pageSize) {
        if (pageIndex < 1) {
            log.warn("Invalid pageIndex: {}, setting to 1", pageIndex);
        }
        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be >= 1");
        }
    }


    @SuppressWarnings("unchecked")
    public static class CollectionPageHelper {
        private int pageIndex;
        private int pageSize;
        private CollectionPage<?, ?> cacheCollectionPage;

        private CollectionPageHelper(int pageIndex, int pageSize) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
        }

        private <C extends Collection<T>, T> CollectionPageHelper(CollectionPage<C, T> collectionPage) {
            this.cacheCollectionPage = collectionPage;
        }

        public <C extends Collection<T>, T> CollectionPage<C, T> selectPage(Supplier<C> selectSupplier) {
            CollectionPage<C, T> collectionPage;
            if (cacheCollectionPage == null) {
                collectionPage = new CollectionPage<>(pageIndex, pageSize);
            } else {
                collectionPage = (CollectionPage<C, T>) cacheCollectionPage;
            }
            executeQuery(collectionPage, selectSupplier);
            return collectionPage;
        }
    }

    @SuppressWarnings("unchecked")
    public static class MapPageHelper {
        private int pageIndex;
        private int pageSize;
        private MapPage<?, ?, ?> cacheMapPage;

        private MapPageHelper(int pageIndex, int pageSize) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
        }

        private <K, V, M extends Map<K, V>> MapPageHelper(MapPage<K, V, M> mapPage) {
            this.cacheMapPage = mapPage;
        }

        public <K, V, M extends Map<K, V>> MapPage<K, V, M> selectPage(Supplier<M> selectSupplier) {
            MapPage<K, V, M> mapPage;
            if (cacheMapPage == null) {
                mapPage = new MapPage<>(pageIndex, pageSize);
            } else {
                mapPage = (MapPage<K, V, M>) cacheMapPage;
            }
            executeQuery(mapPage, selectSupplier);
            return mapPage;
        }
    }

    @SuppressWarnings("unchecked")
    public static class GeneralPageHelper {
        private PageInfo<?> pageInfo;

        private GeneralPageHelper(int pageIndex, int pageSize, String pagePluginTypeName) {
            pageInfo = new PageInfo<>(pageIndex, pageSize, pagePluginTypeName);
        }

        private <T> GeneralPageHelper(PageInfo<T> pageInfo) {
            this.pageInfo = pageInfo;
        }


        /**
         * 执行分页查询并返回分页结果 {@link PageInfo} 对象。
         * <p>
         * 此方法作为分页操作的入口，使用提供的 {@code selectSupplier} 来执行分页查询并返回查询结果。
         * 它将查询结果封装在 {@code PageInfo<T>} 对象中，该对象包含查询数据和分页信息（如当前页、总页数等）。
         * </p>
         *
         * <p><strong>分页逻辑：</strong></p>
         * <ul>
         *     <li>使用 {@code selectSupplier} 执行查询</li>
         *     <li>根据提供的页码和页大小进行分页</li>
         *     <li>返回的 {@code PageInfo} 对象包含分页数据及分页相关信息</li>
         * </ul>
         *
         * <p><strong>示例：</strong></p>
         * <pre>{@code
         * PageInfo<List<ProductView>> pageInfo = PageHelper.of(1, 3).selectPage(this::selectProductViewList);
         * }</pre>
         * 在上述示例中，调用 {@code selectPage} 方法执行查询，并返回第一页的 3 条数据。
         *
         * @param selectSupplier 查询数据的 {@link Supplier}
         * @param <T>            查询结果的类型。
         * @return 返回包含查询结果和分页信息的 {@link PageInfo} 对象。
         * @see SqlContext#select()
         */
        public <T> PageInfo<T> selectPage(Supplier<T> selectSupplier) {
            executeQuery(pageInfo, selectSupplier);
            return (PageInfo<T>) pageInfo;
        }

        /**
         * 将查询条件应用到当前的分页查询中，调用该方法时会将条件与现有分页信息合并，该方法必须在查询之前发起。
         * <p>
         * 示例：
         * <pre>{@code
         *         PageInfo<List<User>> pageInfo = PageHelper.of(1, 3)
         *                 .applyWhere(whereCondition -> whereCondition.andGreaterThanOrEqualTo(xxx, 3))
         *                 .selectPage(() -> sqlContext.select().allColumn().from(User.class).fetch().toList());
         *         pageInfo.getRecords().forEach(System.out::println);
         * }</pre>
         * <p>注意：此方法不会支持叠加效果，因为多次调用会让SQL不可预料，为避免数据污染，多次调用仅最后一次生效。</p>
         *
         * @param condition 需要应用的查询条件，使用 {@link WhereCondition} 来构建具体的查询条件。该参数是一个 Consumer 类型，
         *                  用于接收条件并进行操作。如果传入的 condition 为 {@code null}，则不会进行任何条件应用操作。
         * @return 返回当前的 {@link GeneralPageHelper} 实例
         * @apiNote 此方法为实验性功能，未来版本可能修改或移除。
         */
        public GeneralPageHelper applyWhere(Consumer<WhereCondition> condition) {
            if (!Objects.equals(pageInfo.getPagePluginTypeName(), DefaultPagePluginType.DYNAMIC_SQL2.getPluginName())) {
                throw new DynamicSqlException("仅" + DefaultPagePluginType.DYNAMIC_SQL2.getPluginName() + "支持在外部追加条件");
            }
            if (condition != null) {
                pageInfo = new ConditionPageInfo<>(pageInfo, condition);
                return this;
            }
            //reset
            pageInfo = new PageInfo<>(pageInfo.getPageIndex(), pageInfo.getPageSize());
            return this;
        }
    }

    static void executeQuery(AbstractPage abstractPage, Supplier<?> selectSupplier) {
        setCurrentPage(abstractPage);
        try {
            abstractPage.setRecords(selectSupplier);
        } finally {
            clearCurrentPage();
        }
    }

    /**
     * 逻辑分页工具类，用于对集合进行内存分页。
     */
    public static class LogicPageHelper {
        private final int pageIndex;
        private final int pageSize;

        private LogicPageHelper(int pageIndex, int pageSize) {
            this.pageIndex = pageIndex;
            this.pageSize = pageSize;
        }

        /**
         * 对集合进行逻辑分页，返回包含分页信息的 {@link PageInfo}。
         *
         * @param collection 原始集合，若为 {@code null} 或空集合则返回空分页对象
         * @param <T>        集合元素类型
         * @return 包含分页信息和当前页数据的 {@link PageInfo}
         *
         * <pre>
         * 示例：
         *   List<Integer> list = Arrays.asList(1,2,3,4,5,6,7);
         *   PageInfo<List<Integer>> pageInfo = PageHelper.ofLogic(2, 3).selectPage(list);
         *   // pageInfo.getRecords() = [4,5,6]
         *   // pageInfo.getTotal() = 7
         *   // pageInfo.getTotalPage() = 3
         * </pre>
         */
        public <T> PageInfo<List<T>> selectPage(Collection<T> collection) {
            if (CollectionUtils.isEmpty(collection)) {
                PageInfo<List<T>> emptyPage = new PageInfo<>(pageIndex, pageSize);
                emptyPage.setTotal(0L);
                emptyPage.setRecords(Collections.emptyList());
                return emptyPage;
            }

            List<T> list = (collection instanceof List) ? (List<T>) collection : new ArrayList<>(collection);
            int total = list.size();
            int fromIndex = (pageIndex - 1) * pageSize;
            if (fromIndex >= total) {
                fromIndex = total; // 超出范围时返回空页
            }
            int toIndex = Math.min(fromIndex + pageSize, total);
            List<T> pageRecords = list.subList(fromIndex, toIndex);
            PageInfo<List<T>> pageInfo = new PageInfo<>(pageIndex, pageSize);
            pageInfo.setTotal(total);
            pageInfo.setRecords(pageRecords);
            return pageInfo;
        }
    }
}
