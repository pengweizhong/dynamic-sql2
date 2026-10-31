-- 只读人工核对；在 01、02 脚本成功执行后运行。
SET SESSION time_zone = '+00:00';
-- 预期：4,4,2,3,3,3,3,3,5,5,2。
SELECT 'department' AS table_name, COUNT(*) AS row_count FROM ds_department
UNION ALL SELECT 'account',COUNT(*) FROM ds_account
UNION ALL SELECT 'profile',COUNT(*) FROM ds_account_profile
UNION ALL SELECT 'role',COUNT(*) FROM ds_role
UNION ALL SELECT 'account_role',COUNT(*) FROM ds_account_role
UNION ALL SELECT 'product',COUNT(*) FROM ds_product
UNION ALL SELECT 'purchase_order',COUNT(*) FROM ds_purchase_order
UNION ALL SELECT 'order_item',COUNT(*) FROM ds_order_item
UNION ALL SELECT 'type_sample',COUNT(*) FROM ds_type_sample
UNION ALL SELECT 'mysql_type_sample',COUNT(*) FROM ds_mysql_type_sample
UNION ALL SELECT 'spatial_sample',COUNT(*) FROM ds_mysql_spatial_sample;
-- 预期：100:2单/50.7500，101:1单/0，102和103:0单/0。
SELECT a.id,COUNT(o.id) AS order_count,COALESCE(SUM(o.total_amount),0) AS total_amount
FROM ds_account a LEFT JOIN ds_purchase_order o ON o.account_id=a.id
GROUP BY a.id ORDER BY a.id;
-- 预期无记录：明细合计与订单金额一致，包括空单。
SELECT o.id FROM ds_purchase_order o LEFT JOIN ds_order_item i ON i.order_id=o.id
GROUP BY o.id,o.total_amount
HAVING COALESCE(SUM(i.quantity*i.unit_price-i.discount_amount),0) <> o.total_amount;
-- 预期 id=1 的三列为 NULL；id=2 varchar/blob 长度 0，固定 BINARY 长度 8。
SELECT id,sample_name,LENGTH(varchar_value),OCTET_LENGTH(blob_value),OCTET_LENGTH(binary_value),default_value
FROM ds_type_sample ORDER BY id;
-- SQL NULL: id=1 为 1；JSON null: id=5 的 JSON_TYPE 为字符串 NULL。
SELECT id,json_value IS NULL AS is_sql_null,JSON_TYPE(json_value) AS json_type,unsigned_big,
HEX(bit_wide),doubled_amount FROM ds_mysql_type_sample ORDER BY id;
-- 预期 id=2 为 POINT(10 20)，其余空间类型可继续用 ST_AsText 检查。
SELECT id,ST_AsText(point_value),ST_AsText(collection_value) FROM ds_mysql_spatial_sample ORDER BY id;
