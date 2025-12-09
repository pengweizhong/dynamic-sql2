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

    /**
     * 判断两个字符串是否不相等（区分大小写）。
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 如果两个字符串不相等返回 true；否则返回 false。
     * 当两个字符串都为 null 时，返回 false（因为它们相等）。
     */
    public static boolean isNotEquals(String str1, String str2) {
        return !isEquals(str1, str2);
    }

    /**
     * 判断两个字符串是否相等（忽略大小写）。
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 如果两个字符串在忽略大小写的情况下相等返回 true；
     * 如果两个字符串都为 null，也返回 true；
     * 否则返回 false。
     */
    public static boolean isEqualsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equalsIgnoreCase(str2);
    }

    /**
     * 判断两个字符串是否不相等（忽略大小写）。
     *
     * @param str1 第一个字符串
     * @param str2 第二个字符串
     * @return 如果两个字符串在忽略大小写的情况下不相等返回 true；
     * 如果两个字符串都为 null，返回 false（因为它们相等）。
     */
    public static boolean isNotEqualsIgnoreCase(String str1, String str2) {
        return !isEqualsIgnoreCase(str1, str2);
    }

    /**
     * 将集合中的元素使用指定分隔符拼接成字符串。
     *
     * @param delimiter  分隔符，例如 "," 或 " | "
     * @param collection 要拼接的集合，可以包含任意对象
     * @return 拼接后的字符串；如果集合为空或为 null，则返回空字符串
     */
    public static String join(CharSequence delimiter, Collection<?> collection) {
        if (CollectionUtils.isEmpty(collection)) {
            return "";
        }
        return join(delimiter, collection.toArray());
    }

    /**
     * 将可变参数数组中的元素使用指定分隔符拼接成字符串。
     *
     * @param delimiter 分隔符，例如 "," 或 " | "
     * @param params    要拼接的对象数组，可以包含 null 值
     * @return 拼接后的字符串；如果数组为空或为 null，则返回空字符串。
     * null 元素会被转换为字符串 "null"
     */
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

    /**
     * 将字节数组转换为十六进制字符串表示（默认小写）。
     * <p>
     * 每个字节会被转换为两位十六进制数，例如：
     * <pre>
     *   byte[] data = {0x0F, (byte) 0xA0};
     *   String hex = StringUtils.bytesToHex(data);
     *   // hex = "0fa0"
     * </pre>
     * </p>
     *
     * @param bytes 字节数组，若为 {@code null} 或长度为 0，则返回空字符串
     * @return 对应的十六进制字符串，每个字节用两位小写十六进制表示
     */
    public static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, false);
    }

    /**
     * 将字节数组转换为十六进制字符串表示，可选择大小写。
     * <p>
     * 每个字节会被转换为两位十六进制数：
     * <ul>
     *   <li>当 {@code upperCase} 为 {@code false} 时，输出小写（例如 "0fa0"）</li>
     *   <li>当 {@code upperCase} 为 {@code true} 时，输出大写（例如 "0FA0"）</li>
     * </ul>
     * </p>
     *
     * @param bytes     字节数组，若为 {@code null} 或长度为 0，则返回空字符串
     * @param upperCase 是否使用大写字母表示十六进制
     * @return 对应的十六进制字符串，每个字节用两位十六进制表示
     */
    public static String bytesToHex(byte[] bytes, boolean upperCase) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder hexStringBuilder = new StringBuilder();
        for (byte b : bytes) {
            hexStringBuilder.append(String.format(upperCase ? "%02X" : "%02x", b));
        }
        return hexStringBuilder.toString();
    }

    /**
     * 将十六进制字符串转换为字节数组。
     * <p>
     * 输入字符串必须是偶数长度，每两个字符表示一个字节。
     * 支持大小写字母（例如 "0fa0" 或 "0FA0"）。
     * </p>
     *
     * @param hex 十六进制字符串，若为 {@code null} 或空字符串则返回空数组
     * @return 转换后的字节数组
     * @throws IllegalArgumentException 如果字符串长度不是偶数，或包含非法的十六进制字符
     *
     *                                  <pre>
     *                                                                                                    示例：
     *                                                                                                      hexToBytes("0fa0") -> {0x0F, (byte)0xA0}
     *                                                                                                      hexToBytes("FF01") -> {(byte)0xFF, 0x01}
     *                                                                                                    </pre>
     */
    public static byte[] hexToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException("十六进制字符串长度必须为偶数: " + hex);
        }

        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            String byteStr = hex.substring(i, i + 2);
            try {
                data[i / 2] = (byte) Integer.parseInt(byteStr, 16);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("非法的十六进制字符: " + byteStr, e);
            }
        }
        return data;
    }

    /**
     * 将整数转换为指定进制的字符串表示。
     * <p>
     * 支持常见的二进制（base=2）、八进制（base=8）、十六进制（base=16），
     * 以及任意 2~36 进制的转换。超过 10 的部分使用字母表示：
     * 例如：十六进制中的 10 表示为 'a'，35 表示为 'z'。
     * </p>
     *
     * @param number 要转换的整数
     * @param base   目标进制，取值范围为 2~36
     * @return 转换后的字符串表示；如果 base 超出范围则抛出 {@link IllegalArgumentException}
     *
     * <pre>
     * 示例：
     *   toBaseString(10, 2)  -> "1010"
     *   toBaseString(10, 8)  -> "12"
     *   toBaseString(255, 16) -> "ff"
     *   toBaseString(35, 36) -> "z"
     * </pre>
     */
    public static String toBaseString(int number, int base) {
        if (base < 2 || base > 36) {
            throw new IllegalArgumentException("进制必须在 2~36 之间: " + base);
        }
        return Integer.toString(number, base);
    }

    /**
     * 将长整型数转换为指定进制的字符串表示。
     *
     * @param number 要转换的长整型数
     * @param base   目标进制，取值范围为 2~36
     * @return 转换后的字符串表示；如果 base 超出范围则抛出 {@link IllegalArgumentException}
     */
    public static String toBaseString(long number, int base) {
        if (base < 2 || base > 36) {
            throw new IllegalArgumentException("进制必须在 2~36 之间: " + base);
        }
        return Long.toString(number, base);
    }


    /**
     * 判断给定字符串是否以指定的后缀结尾。
     *
     * @param str    原始字符串
     * @param suffix 后缀字符串
     * @return 当 {@code str} 以 {@code suffix} 结尾时返回 {@code true}，否则返回 {@code false}
     */
    public static boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        return str.startsWith(suffix, str.length() - suffix.length());
    }

    /**
     * 将字符串转换为驼峰命名格式（camelCase）。
     * <p>
     * 通常用于将下划线命名（snake_case）转换为驼峰命名，例如：
     * <pre>
     *   toCamelCase("hello_world") -> "helloWorld"
     *   toCamelCase("user_name_id") -> "userNameId"
     * </pre>
     * </p>
     *
     * @param str 输入字符串，可以为下划线命名格式；若为 {@code null} 或空字符串，则返回空字符串
     * @return 转换后的驼峰命名字符串
     */
    public static String toCamelCase(String str) {
        return NamingUtils.snakeToCamelCase(str);
    }

    /**
     * 将字符串转换为下划线命名格式（snake_case）。
     * <p>
     * 通常用于将驼峰命名（camelCase）转换为下划线命名，例如：
     * <pre>
     *   toSnakeCase("helloWorld") -> "hello_world"
     *   toSnakeCase("userNameId") -> "user_name_id"
     * </pre>
     * </p>
     *
     * @param str 输入字符串，可以为驼峰命名格式；若为 {@code null} 或空字符串，则返回空字符串
     * @return 转换后的下划线命名字符串
     */
    public static String toSnakeCase(String str) {
        return NamingUtils.camelToSnakeCase(str);
    }

    /**
     * 将字符串反转。
     *
     * @param str 输入字符串
     * @return 反转后的字符串；如果输入为 null 则返回 null
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * 判断字符串是否只包含数字。
     *
     * @param str 输入字符串
     * @return 如果字符串非空且只包含数字字符则返回 true，否则返回 false
     */
    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 左侧填充字符串到指定长度。
     *
     * @param str     原始字符串
     * @param size    目标长度
     * @param padChar 填充字符
     * @return 填充后的字符串；如果原始字符串长度 >= size 则直接返回原始字符串
     */
    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < pads; i++) {
            sb.append(padChar);
        }
        sb.append(str);
        return sb.toString();
    }

    /**
     * 右侧填充字符串到指定长度。
     * <p>
     * 如果原始字符串长度小于目标长度，则在右侧补充指定的字符；
     * 如果原始字符串长度大于或等于目标长度，则直接返回原始字符串。
     * </p>
     *
     * @param str     原始字符串，可以为 {@code null}
     * @param size    目标长度
     * @param padChar 填充字符
     * @return 填充后的字符串；如果 {@code str} 为 {@code null} 则返回 {@code null}
     *
     * <pre>
     * 示例：
     *   rightPad("abc", 6, '*') -> "abc***"
     *   rightPad("hello", 3, '-') -> "hello"
     * </pre>
     */
    public static String rightPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        StringBuilder sb = new StringBuilder(size);
        sb.append(str);
        for (int i = 0; i < pads; i++) {
            sb.append(padChar);
        }
        return sb.toString();
    }


}
