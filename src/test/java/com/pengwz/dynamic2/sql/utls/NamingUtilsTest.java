package com.pengwz.dynamic2.sql.utls;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NamingUtilsTest {

    @Test
    void camelToSnakeCase() {
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("_"), "_"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase(null), null));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase(""), ""));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("abc"), "abc"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("Abc"), "abc"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("aBc"), "a_bc"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("abC"), "ab_c"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("abc_"), "abc_"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("_abc_"), "_abc_"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("ABC"), "a_b_c"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("A_BC"), "a_b_c"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("A_bC"), "a_b_c"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("_A_bC"), "_a_b_c"));
        assertTrue(StringUtils.isEquals(NamingUtils.camelToSnakeCase("__A_bC"), "__a_b_c"));
    }

    @Test
    void snakeToCamelCase() {
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("_"), "_"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase(null), null));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase(""), ""));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("abc"), "abc"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("a_bc"), "aBc"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("ab_c"), "abC"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("abc_"), "abc"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("_abc_"), "Abc"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("ABC"), "ABC"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("A_BC"), "ABC"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("A_bC"), "ABC"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("_A_bC"), "ABC"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("_A"), "A"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("_A_"), "A"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("__A_"), "A"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("A"), "a"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("A_"), "A"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("a_b"), "aB"));
        assertTrue(StringUtils.isEquals(NamingUtils.snakeToCamelCase("ab_"), "ab"));
    }
}