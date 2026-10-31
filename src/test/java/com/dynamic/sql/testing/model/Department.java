package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.*;
import lombok.Data;

@Data
@Table("ds_department")
public class Department {
    @Id
    private Long id;
    private Long parentId;
    private String name;
}
