package com.qeeka.test.domain;

import com.qeeka.annotation.Column;
import com.qeeka.annotation.Entity;
import com.qeeka.annotation.Id;

/**
 * Created by Neal on 16/4/8.
 */
@Entity(table = "company")
public class Company {
    @Id
    public Long id;

    @Column("name")
    public String name;

    @Column("type")
    public Integer type;

    @Column("status")
    public Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
