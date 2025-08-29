package com.dynamic.sql.entities2;

import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import lombok.Data;

@Table(schema = "test", value = "user_ext")
@Data
public class UserExtEntity {
    @Id
    private Integer id;
    private Integer userId;
    private String doSomething;
}
