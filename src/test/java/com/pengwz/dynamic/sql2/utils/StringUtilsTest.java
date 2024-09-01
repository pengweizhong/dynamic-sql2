package com.pengwz.dynamic.sql2.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StringUtilsTest {

    @Test
    void isEmpty() {
        assertTrue(StringUtils.isEmpty(null));
        assertTrue(StringUtils.isEmpty(""));
        assertFalse(StringUtils.isEmpty(" "));
        assertFalse(StringUtils.isEmpty(" \t"));
        assertFalse(StringUtils.isEmpty(" \n"));
        assertFalse(StringUtils.isEmpty(" \r"));
        assertFalse(StringUtils.isEmpty(" \t\r"));
        assertFalse(StringUtils.isEmpty(" \n\r"));
        assertFalse(StringUtils.isEmpty(" \r "));
        assertFalse(StringUtils.isEmpty(" \t\r "));
        assertFalse(StringUtils.isEmpty(" \n\r "));
        assertFalse(StringUtils.isEmpty(" \r \t"));
        assertFalse(StringUtils.isEmpty(" \n\r \t"));
        assertFalse(StringUtils.isEmpty("hello"));
    }

    @Test
    void isNotEmpty() {
        assertFalse(StringUtils.isNotEmpty(null));
        assertFalse(StringUtils.isNotEmpty(""));
        assertTrue(StringUtils.isNotEmpty(" "));
        assertTrue(StringUtils.isNotEmpty(" \t"));
        assertTrue(StringUtils.isNotEmpty(" \n"));
        assertTrue(StringUtils.isNotEmpty(" \r"));
        assertTrue(StringUtils.isNotEmpty(" \t\r"));
        assertTrue(StringUtils.isNotEmpty(" \n\r "));
        assertTrue(StringUtils.isNotEmpty(" \r \t"));
        assertTrue(StringUtils.isNotEmpty(" \n\r \t"));
        assertTrue(StringUtils.isNotEmpty("hello"));
    }

    @Test
    void isBlank() {
        assertTrue(StringUtils.isBlank(null));
        assertTrue(StringUtils.isBlank(""));
        assertTrue(StringUtils.isBlank(" "));
        assertTrue(StringUtils.isBlank(" \t"));
        assertTrue(StringUtils.isBlank(" \n"));
        assertTrue(StringUtils.isBlank(" \r"));
        assertTrue(StringUtils.isBlank(" \t\r"));
        assertTrue(StringUtils.isBlank(" \n\r "));
        assertTrue(StringUtils.isBlank(" \r \t"));
        assertTrue(StringUtils.isBlank(" \n\r \t"));
        assertFalse(StringUtils.isBlank("hello"));
        assertFalse(StringUtils.isBlank(" hello "));
    }

    @Test
    void isNotBlank() {
        assertFalse(StringUtils.isNotBlank(null));
        assertFalse(StringUtils.isNotBlank(""));
        assertFalse(StringUtils.isNotBlank(" "));
        assertFalse(StringUtils.isNotBlank(" \t"));
        assertFalse(StringUtils.isNotBlank(" \n"));
        assertFalse(StringUtils.isNotBlank(" \r"));
        assertFalse(StringUtils.isNotBlank(" \t\r"));
        assertFalse(StringUtils.isNotBlank(" \n\r "));
        assertFalse(StringUtils.isNotBlank(" \r \t"));
        assertFalse(StringUtils.isNotBlank(" \n\r \t"));
        assertTrue(StringUtils.isNotBlank("hello"));
    }

    @Test
    void testEquals() {
        assertTrue(StringUtils.isEquals(null, null));
        assertFalse(StringUtils.isEquals(null, ""));
        assertFalse(StringUtils.isEquals("", null));
        assertTrue(StringUtils.isEquals("", ""));
        assertTrue(StringUtils.isEquals("hello", "hello"));
        assertFalse(StringUtils.isEquals("hello", "world"));
    }
}