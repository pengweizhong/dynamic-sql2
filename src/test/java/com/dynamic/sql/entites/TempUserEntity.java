package com.dynamic.sql.entites;

import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import lombok.Data;

@Data
@Table("temp_user")
public class TempUserEntity {
    @Id
    private Integer id;
    private String name;
    private Integer age;
}
