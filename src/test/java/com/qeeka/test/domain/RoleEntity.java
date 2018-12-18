package com.qeeka.test.domain;

import com.qeeka.annotation.Column;
import com.qeeka.annotation.Entity;
import com.qeeka.annotation.Id;

import java.util.Date;

/**
 * Created by neal.xu on 2018/12/13.
 */
@Entity(table = "role")
public class RoleEntity {
    @Id
    private Integer id;
    @Column
    private String name;
    @Column("create_time")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
