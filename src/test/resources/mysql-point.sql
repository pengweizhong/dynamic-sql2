DROP TABLE IF EXISTS t_location;

CREATE TABLE `t_location`
(
    `id`           int          NOT NULL AUTO_INCREMENT COMMENT '位置ID',
    `province`     varchar(50)  NULL COMMENT '省份',
    `city`         varchar(50)  NULL COMMENT '城市',
    `district`     varchar(50)  NULL COMMENT '区/县',
    `address`      varchar(255) NULL COMMENT '详细地址',
    `location4326` point        NOT NULL /*!80003 SRID 4326 */ COMMENT '地理坐标点(4326)',
    `location`     point        NOT NULL COMMENT '地理坐标点(默认)',
    `create_time`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    SPATIAL KEY `idx_location` (`location4326`),
    SPATIAL INDEX idx_location_spatial (location)
) ENGINE = InnoDB COMMENT ='位置表';

-- 插入数据
INSERT INTO t_location
(id, province, city, district, address, location4326, location, create_time, update_time)
VALUES (1, '北京市', '北京市', '海淀区', '西土城路4号',
        ST_SRID(ST_GeomFromText('POINT(116.355762 39.97161)'), 4326),
        ST_GeomFromText('POINT(116.255243 39.902088)'),
        '2025-02-07 11:24:18',
        '2025-02-07 11:24:18');

INSERT INTO t_location
(id, province, city, district, address, location4326, location, create_time, update_time)
VALUES (2, '北京市', '北京市', '海淀区', '玉泉路66号',
        ST_SRID(ST_GeomFromText('POINT (116.255243 39.902088)'), 4326),
        ST_GeomFromText('POINT(116.255243 39.902088)'),
        '2025-02-07 11:24:18',
        '2025-02-07 11:30:54');

INSERT INTO t_location
(province, city, district, address, location4326, location, create_time, update_time)
VALUES ('New York', 'New York', 'New York', 'New York',
        ST_SRID(ST_GeomFromText('POINT (116.255243 39.902088)'), 4326)
           , ST_GeomFromText('POINT(40.7128 -74.0060)'),
        '2025-02-07 11:24:18',
        '2025-02-07 11:24:18');

-- 通过ST_X()和ST_Y()来提取POINT的x和y坐标值
SELECT province, city, district, address, ST_X(location4326) AS latitude, ST_Y(location4326)
FROM t_location;
-- 查询POINT类型的字段并以文本格式返回坐标
SELECT province, city, district, address, ST_AsText(location4326)
FROM t_location;
