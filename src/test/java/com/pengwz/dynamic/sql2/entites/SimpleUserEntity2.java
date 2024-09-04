package com.pengwz.dynamic.sql2.entites;

import com.google.gson.Gson;
import com.pengwz.dynamic.sql2.anno.Column;
import com.pengwz.dynamic.sql2.anno.GeneratedValue;
import com.pengwz.dynamic.sql2.anno.Id;
import com.pengwz.dynamic.sql2.anno.Table;
import com.pengwz.dynamic.sql2.enums.GenerationType;

/**
 *-- dynamic_sql2.t_simple_user definition
 *
 * CREATE TABLE `t_simple_user` (
 *   `id` int NOT NULL AUTO_INCREMENT,
 *   `username` varchar(100) DEFAULT NULL,
 *   `age` int DEFAULT NULL,
 *   `gender` enum('MALE','FEMALE') DEFAULT NULL,
 *   PRIMARY KEY (`id`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
 */
@Table("t_simple_user2")
public class SimpleUserEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,sequenceName = "")
    private Integer id;
    private String username;
    private Integer age;
    //故意设置一个不一样的名称
    @Column("gender")
    private Gender genderEnum;

    public enum Gender {
        MALE, FEMALE;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Gender getGenderEnum() {
        return genderEnum;
    }

    public void setGenderEnum(Gender genderEnum) {
        this.genderEnum = genderEnum;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
