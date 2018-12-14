package com.qeeka.test.domain;

import com.qeeka.annotation.Column;
import com.qeeka.annotation.Entity;
import com.qeeka.annotation.Id;
import com.qeeka.annotation.Transient;
import com.qeeka.enums.GenerationType;

import java.util.Date;

/**
 * Created by neal.xu on 2018/12/12.
 */
@Entity(table = "user")
public class UserEntity {
    @Id(strategy = GenerationType.AUTO)
    @Column("user_id")
    private Integer userId;
    private String name;
    @Column("role_id")
    private Integer roleId;
    @Column("create_time")
    private Date createTime;
    @Transient
    private Integer age;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
