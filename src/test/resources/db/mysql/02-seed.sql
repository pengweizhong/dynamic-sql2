-- 在 01-schema.sql 后执行；固定主键、时间和金额，便于跨数据库比较。
SET NAMES utf8mb4;
SET SESSION time_zone = '+00:00';
START TRANSACTION;
INSERT INTO ds_department (id,parent_id,name) VALUES (10,NULL,'总部');
INSERT INTO ds_department (id,parent_id,name) VALUES (20,10,'研发'),(30,10,'销售'),(40,10,'空部门');
INSERT INTO ds_account (id,department_id,login_name,display_name,status,balance,created_at) VALUES
(100,20,'alice','张三','ACTIVE',100.2500,'2024-02-29 10:20:30.123456'),
(101,20,'bob','李四','ACTIVE',0,'2024-02-29 10:20:30.123456'),
(102,30,'carol','王五','DISABLED',-10.5000,'2024-03-01 00:00:00'),
(103,NULL,'no_department',NULL,'ACTIVE',0,'2024-03-02 00:00:00');
INSERT INTO ds_account_profile (account_id,birthday,biography,avatar) VALUES
(100,'2000-02-29','中文与 emoji 😀',X'00017FFF'),(101,NULL,'',NULL);
INSERT INTO ds_role VALUES (1,'ADMIN','管理员'),(2,'READER','读者'),(3,'UNASSIGNED','未分配');
INSERT INTO ds_account_role VALUES
(100,1,'2024-01-01 00:00:00'),(100,2,'2024-01-01 00:00:00'),(101,2,'2024-01-01 00:00:00');
INSERT INTO ds_product VALUES (200,'SKU-A','键盘',10.2500,100),(201,'SKU-B','鼠标',20,0),(202,'SKU-C','未售商品',0,5);
INSERT INTO ds_purchase_order VALUES
(1000,100,'ORDER-001','PAID',40.5000,'2024-03-01 10:00:00','2024-03-01 10:01:00'),
(1001,100,'ORDER-002','NEW',10.2500,'2024-03-02 10:00:00',NULL),
(1002,101,'ORDER-003','CANCELLED',0,'2024-03-02 10:00:00',NULL);
INSERT INTO ds_order_item VALUES
(1,1000,1,200,2,10.2500,0),(2,1000,2,201,1,20,0),(3,1001,1,200,1,10.2500,0);

-- 1 全 NULL；2 零/空串/空二进制；3 普通值及转义字符；4/5 数值上下界。
INSERT INTO ds_type_sample (id,sample_name) VALUES (1,'nulls');
INSERT INTO ds_type_sample
(id,sample_name,small_value,int_value,long_value,decimal_value,float_value,double_value,flag_value,char_value,varchar_value,text_value,binary_value,varbinary_value,blob_value,date_value,time_value,datetime_value,timestamp_value)
VALUES
(2,'zero-empty',0,0,0,0,0,0,0,'','','',X'0000000000000000',X'',X'','1970-01-01','00:00:00','1970-01-01 00:00:00','2000-01-01 00:00:00'),
(3,'unicode-escape',123,123456,9007199254740993,1234567890.1234567890,1.25,1.23456789012345,1,'ABC','中文😀 O''Reilly 100% _ : ?','第一行
第二行',X'00017F80FEFF1020',X'0000275CFF',X'000102FEFF','2024-02-29','23:59:59.123456','2024-02-29 23:59:59.123456','2024-02-29 23:59:59.123456'),
(4,'numeric-min',-32768,-2147483648,-9223372036854775808,-9999999999999999999999999999.9999999999,-1.25,-1.23456789012345,0,NULL,NULL,NULL,NULL,NULL,NULL,'1000-01-01',NULL,'1000-01-01 00:00:00',NULL),
(5,'numeric-max',32767,2147483647,9223372036854775807,9999999999999999999999999999.9999999999,1.25,1.23456789012345,1,NULL,NULL,NULL,NULL,NULL,NULL,'9999-12-31',NULL,'9999-12-31 23:59:59.999999',NULL);

INSERT INTO ds_mysql_type_sample (id) VALUES (1);
INSERT INTO ds_mysql_type_sample
(id,tiny_value,tiny_boolean,medium_value,unsigned_tiny,unsigned_small,unsigned_medium,unsigned_int,unsigned_big,bit_flag,bit_mask,bit_wide,year_value,duration_value,enum_value,set_value,json_value,tiny_text,medium_text,long_text,tiny_blob,medium_blob,long_blob,base_amount)
VALUES
(2,0,0,0,0,0,0,0,0,b'0',b'00000000',b'0',2000,'00:00:00','NEW','',JSON_OBJECT(),'','','',X'',X'',X'',0),
(3,127,1,8388607,255,65535,16777215,4294967295,18446744073709551615,b'1',b'10101010',b'1111111111111111111111111111111111111111111111111111111111111111',2155,'838:59:59','ACTIVE','READ,WRITE',JSON_OBJECT('name','中文','enabled',true,'tags',JSON_ARRAY('a','b'),'nullable',NULL),'短文本',REPEAT('中',1000),REPEAT('文',2000),X'00FF',X'0001FF',X'00FF00',12.3456),
(4,-128,NULL,-8388608,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,1901,'-838:59:59','CLOSED','ADMIN',JSON_ARRAY(1,'文本',NULL),NULL,NULL,NULL,NULL,NULL,NULL,-12.3456),
(5,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,CAST('null' AS JSON),NULL,NULL,NULL,NULL,NULL,NULL,NULL);

INSERT INTO ds_mysql_spatial_sample (id) VALUES (1);
INSERT INTO ds_mysql_spatial_sample VALUES
(2,ST_GeomFromText('POINT(10 20)',0),ST_GeomFromText('LINESTRING(0 0,1 1,2 0)',0),
ST_GeomFromText('POLYGON((0 0,4 0,4 4,0 4,0 0))',0),ST_GeomFromText('MULTIPOINT((0 0),(1 1))',0),
ST_GeomFromText('MULTILINESTRING((0 0,1 1),(2 2,3 3))',0),
ST_GeomFromText('MULTIPOLYGON(((0 0,1 0,1 1,0 1,0 0)))',0),ST_GeomFromText('POINT(0 0)',0),
ST_GeomFromText('GEOMETRYCOLLECTION(POINT(1 1),LINESTRING(0 0,1 1))',0));
COMMIT;
