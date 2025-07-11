/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.utils;


import java.util.Collection;

public class StringUtils {
    private StringUtils() {
    }

    /**
     * 判断字符串是否为空
     *
     * @param str 需要判断的字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 需要判断的字符串
     * @return 是否不为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串内容是否为空
     *
     * @param str 需要判断的字符串
     * @return 内容是否为空
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串内容是否不为空
     *
     * @param str 需要判断的字符串
     * @return 内容是否不为空
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 比较两个字符串是否相同
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 是否相同
     */
    public static boolean isEquals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equals(str2);
    }

    public static boolean isNotEquals(String str1, String str2) {
        return !isEquals(str1, str2);
    }

    public static boolean isEqualsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equalsIgnoreCase(str2);
    }

    public static boolean isNotEqualsIgnoreCase(String str1, String str2) {
        return !isEqualsIgnoreCase(str1, str2);
    }

    public static String join(CharSequence delimiter, Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return "";
        }
        return join(delimiter, collection.toArray());
    }

    public static String join(CharSequence delimiter, Object... params) {
        if (params == null || params.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param == null) {
                result.append("null");
            } else {
                result.append(param);
            }
            if (i < params.length - 1) {
                result.append(delimiter);
            }
        }
        return result.toString();
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            hexStringBuilder.append(String.format("%02x", b));
        }
        return hexStringBuilder.toString();
    }
}
