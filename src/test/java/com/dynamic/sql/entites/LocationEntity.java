package com.dynamic.sql.entites;

import com.dynamic.sql.anno.Column;
import com.dynamic.sql.anno.GeneratedValue;
import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import com.dynamic.sql.model.Point;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table("t_location")
public class LocationEntity {
    //    `id`            int          NOT NULL AUTO_INCREMENT COMMENT '位置ID',
    @Id
    @GeneratedValue
    private Integer id;
    //    `province`      varchar(50)  NOT NULL COMMENT '省份',
    private String province;
    //    `city`          varchar(50)  NOT NULL COMMENT '城市',
    private String city;
    //    `district`      varchar(50)  NOT NULL COMMENT '区/县',
    private String district;
    //    `address`       varchar(255) NOT NULL COMMENT '详细地址',
    private String address;
    //    `location4326`  point        NOT NULL /*!80003 SRID 4326 */ COMMENT '地理坐标点(4326)',
    @Column(srid = 4236)
    private Point location4326;
    //    `location`      point        NULL COMMENT '地理坐标点(默认)',
    private Point location;
    //    `create_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    private LocalDateTime createTime;
    //    `update_time`   timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    private LocalDateTime updateTime;
}
