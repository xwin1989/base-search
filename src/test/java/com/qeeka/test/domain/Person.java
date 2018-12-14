package com.qeeka.test.domain;


import com.qeeka.annotation.Column;
import com.qeeka.annotation.Entity;
import com.qeeka.annotation.Id;

/**
 * Created by Neal on 2015/7/27.
 */
@Entity(table = "person")
public class Person {
    @Id
    public Integer id;

    @Column("name")
    public String name;

    @Column("password")
    public String password;

    @Column("type")
    public Integer type;

    @Column("status")
    public Integer status;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
