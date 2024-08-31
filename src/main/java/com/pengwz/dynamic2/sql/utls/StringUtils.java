package com.pengwz.dynamic2.sql.utls;

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

}
