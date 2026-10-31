-- 显式重置：删除本套测试表及数据。仅在专用测试库按需执行。
-- 不关闭外键检查，按依赖顺序删除；不自动由建表脚本调用。
DROP TABLE IF EXISTS ds_mysql_spatial_sample;
DROP TABLE IF EXISTS ds_mysql_type_sample;
DROP TABLE IF EXISTS ds_type_sample;
DROP TABLE IF EXISTS ds_order_item;
DROP TABLE IF EXISTS ds_purchase_order;
DROP TABLE IF EXISTS ds_product;
DROP TABLE IF EXISTS ds_account_role;
DROP TABLE IF EXISTS ds_role;
DROP TABLE IF EXISTS ds_account_profile;
DROP TABLE IF EXISTS ds_account;
DROP TABLE IF EXISTS ds_department;
