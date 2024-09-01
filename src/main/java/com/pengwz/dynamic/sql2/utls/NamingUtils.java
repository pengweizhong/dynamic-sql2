package com.pengwz.dynamic.sql2.utls;

public class NamingUtils {
    private NamingUtils() {
    }

    /**
     * 将驼峰命名转换为下划线命名
     * <p>
     * <b>转换示例</b>
     * <table border="1" summary="转换示例">
     *   <tr><th>输入</th><th>输出</th></tr>
     *   <tr><td>"_"</td><td>"_"</td></tr>
     *   <tr><td>null</td><td>null</td></tr>
     *   <tr><td>""</td><td>""</td></tr>
     *   <tr><td>"abc"</td><td>"abc"</td></tr>
     *   <tr><td>"Abc"</td><td>"abc"</td></tr>
     *   <tr><td>"aBc"</td><td>"a_bc"</td></tr>
     *   <tr><td>"abC"</td><td>"ab_c"</td></tr>
     *   <tr><td>"abc_"</td><td>"abc_"</td></tr>
     *   <tr><td>"_abc_"</td><td>"_abc_"</td></tr>
     *   <tr><td>"ABC"</td><td>"a_b_c"</td></tr>
     *   <tr><td>"A_BC"</td><td>"a_b_c"</td></tr>
     *   <tr><td>"A_bC"</td><td>"a_b_c"</td></tr>
     *   <tr><td>"_A_bC"</td><td>"_a_b_c"</td></tr>
     *   <tr><td>"__A_bC"</td><td>"__a_b_c"</td></tr>
     * </table>
     *
     * @param camelCaseString 驼峰命名的字段
     * @return 转换后的下划线命名字段
     */
    public static String camelToSnakeCase(String camelCaseString) {
        if (camelCaseString == null || camelCaseString.isEmpty()) {
            return camelCaseString;
        }

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCaseString.charAt(0)));

        for (int i = 1; i < camelCaseString.length(); i++) {
            char ch = camelCaseString.charAt(i);
            if (Character.isUpperCase(ch)) {
                //判断最后一个字符是不是下划线，如果是就不再追加下划线，就是说只保留一个下划线
                if (StringUtils.isNotEquals(result.substring(result.length() - 1), "_")) {
                    result.append('_');
                }
                result.append(Character.toLowerCase(ch));
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * 将下划线命名转换为驼峰命名
     * <p>
     * <b>转换示例</b>
     * <table border="1" summary="转换示例">
     *   <tr><th>输入</th><th>输出</th></tr>
     *   <tr><td>"_"</td><td>"_"</td></tr>
     *   <tr><td>null</td><td>null</td></tr>
     *   <tr><td>""</td><td>""</td></tr>
     *   <tr><td>"abc"</td><td>"abc"</td></tr>
     *   <tr><td>"a_bc"</td><td>"aBc"</td></tr>
     *   <tr><td>"ab_c"</td><td>"abC"</td></tr>
     *   <tr><td>"abc_"</td><td>"abc"</td></tr>
     *   <tr><td>"_abc_"</td><td>"Abc"</td></tr>
     *   <tr><td>"ABC"</td><td>"ABC"</td></tr>
     *   <tr><td>"A_BC"</td><td>"ABC"</td></tr>
     *   <tr><td>"A_bC"</td><td>"ABC"</td></tr>
     *   <tr><td>"_A_bC"</td><td>"ABC"</td></tr>
     *   <tr><td>"_A"</td><td>"A"</td></tr>
     *   <tr><td>"_A_"</td><td>"A"</td></tr>
     *   <tr><td>"__A_"</td><td>"A"</td></tr>
     *   <tr><td>"A"</td><td>"a"</td></tr>
     *   <tr><td>"A_"</td><td>"A"</td></tr>
     *   <tr><td>"a_b"</td><td>"aB"</td></tr>
     *   <tr><td>"ab_"</td><td>"ab"</td></tr>
     * </table>
     *
     * @param snakeCaseString 下划线命名的字段
     * @return 转换后的驼峰命名字段
     */
    public static String snakeToCamelCase(String snakeCaseString) {
        if (snakeCaseString == null || snakeCaseString.isEmpty()) {
            return snakeCaseString;
        }
        if (snakeCaseString.length() == 1) {
            if (snakeCaseString.charAt(0) == '_') {
                return "_";
            }
            return snakeCaseString.toLowerCase();
        }
        StringBuilder result = new StringBuilder();
        boolean toUpperCase = false;

        for (char ch : snakeCaseString.toCharArray()) {
            if (ch == '_') {
                toUpperCase = true;
            } else {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(ch));
                    toUpperCase = false;
                } else {
                    result.append(ch);
                }
            }
        }
        return result.toString();
    }
}
