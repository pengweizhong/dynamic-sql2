package com.dynamic.sql.plugins.pagination;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PageHelperTest {

    @Test
    void ofLogic() {
        // 准备测试数据
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);

        // 测试第2页，每页3条
        PageInfo<List<Integer>> pageInfo = PageHelper.ofLogic(2, 3).selectPage(list);
        System.out.println(pageInfo);
        assertEquals(Arrays.asList(4, 5, 6), pageInfo.getRecords());
        assertEquals(7L, pageInfo.getTotal());
        assertEquals(3, pageInfo.getTotalPage());
        assertEquals(2, pageInfo.getPageIndex());
        assertEquals(3, pageInfo.getPageSize());

        // 测试第一页
        PageInfo<List<Integer>> firstPage = PageHelper.ofLogic(1, 3).selectPage(list);
        System.out.println(firstPage);
        assertEquals(Arrays.asList(1, 2, 3), firstPage.getRecords());

        // 测试最后一页（不足一页）
        PageInfo<List<Integer>> lastPage = PageHelper.ofLogic(3, 3).selectPage(list);
        System.out.println(lastPage);
        assertEquals(Collections.singletonList(7), lastPage.getRecords());

        // 测试页码超出范围
        PageInfo<List<Integer>> outOfRangePage = PageHelper.ofLogic(10, 3).selectPage(list);
        System.out.println(outOfRangePage);
        assertTrue(outOfRangePage.getRecords().isEmpty());

        // 测试空集合
        PageInfo<List<Integer>> emptyPage = PageHelper.ofLogic(1, 3).selectPage(Collections.emptyList());
        System.out.println(emptyPage);
        assertTrue(emptyPage.getRecords().isEmpty());
        assertEquals(0L, emptyPage.getTotal());
        assertEquals(0, emptyPage.getTotalPage());
    }
}