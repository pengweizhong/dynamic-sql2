package com.dynamic.sql.entites;

import com.dynamic.sql.anno.Column;
import com.dynamic.sql.anno.GeneratedValue;
import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("users_ext")
public class UserExt {
    @Id
    @GeneratedValue
    private Integer id;
    @Column("user_id")
    private Integer userId;
    private String nickname;
    @Column("avatar_url")
    private String avatarUrl;
    private String mobile;
    private String email;
    private String wechat;
    private String qq;
    private String extra;
    @Column("created_at")
    private Date createdAt;
    @Column("updated_at")
    private Date updatedAt;
}