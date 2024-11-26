-- Oracle模拟Mysql的FIND_IN_SET函数
CREATE OR REPLACE FUNCTION FIND_IN_SET(
    substring VARCHAR2,
    string_list VARCHAR2
) RETURN NUMBER IS
    position NUMBER := 0;
BEGIN
    -- 遍历 string_list，查找 substring 的位置
FOR i IN 1 .. REGEXP_COUNT(string_list, ',') + 1 LOOP
        IF REGEXP_SUBSTR(string_list, '[^,]+', 1, i) = substring THEN
            position := i;
            EXIT;
END IF;
END LOOP;

RETURN position; -- 返回位置，0 表示未找到
END;
/
-- 查询示例
-- SELECT *
-- FROM my_table
-- WHERE FIND_IN_SET('target_value', tags) > 0
--   AND status = 'active';


