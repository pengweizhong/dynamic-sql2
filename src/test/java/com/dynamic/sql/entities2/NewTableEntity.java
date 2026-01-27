package com.dynamic.sql.entities2;

import com.dynamic.sql.anno.Table;
import lombok.Data;

@Data
@Table("newtable")
public class NewTableEntity {
    private Integer id;
    private String Column1;
    private String Column2;
    //模拟关键字
    private String describe;
}
