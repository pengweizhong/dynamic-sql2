package com.dynamic.sql.core.placeholder;

import org.junit.jupiter.api.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ParameterBinderTest {

    @Test
    void test() {
        String sql = "select `t1`.`teacher_id` as id, `t1`.`first_name` as firstName, " +
                "upper(md5(`t1`.`first_name`)) as firstName, " +
                "(select `t1`.`gender` as gender from `teacher` as `t1` limit :527f16c407014d63b7310126f7fd55d0) as nested1 " +
                "from `teacher` as `t1` inner join `subject` as `subject` " +
                "on `t1`.`teacher_id` = `subject`.`teacher_id` or `t1`.`teacher_id` < :069f1eb0f2dc41389adf10aeddda4466 " +
                "limit :316c3a38fbb14b70b5a64f5e5f414188";

        // UUID 正则表达式
        Pattern uuidPattern = Pattern.compile(":[0-9a-f]{32}");
        Matcher matcher = uuidPattern.matcher(sql);
        ParameterBinder parameterBinder = new ParameterBinder();
        parameterBinder.add(":527f16c407014d63b7310126f7fd55d0", 1);
        parameterBinder.add(":069f1eb0f2dc41389adf10aeddda4466", 1);
        parameterBinder.add(":316c3a38fbb14b70b5a64f5e5f414188", 1);

        StringBuilder modifiedSql = new StringBuilder(sql);

        // 替换占位符
        while (matcher.find()) {
            String placeholder = matcher.group();
            if (parameterBinder.contains(placeholder)) {
                Object value = parameterBinder.getValue(placeholder);
                // 替换占位符为对应的值
                int start = matcher.start();
                int end = matcher.end();
                modifiedSql.replace(start, end, value.toString());
                matcher = uuidPattern.matcher(modifiedSql);
            }
        }
        // 输出修改后的 SQL
        System.out.println(modifiedSql.toString());
    }

}