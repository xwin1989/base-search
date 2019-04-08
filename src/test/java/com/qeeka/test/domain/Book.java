package com.qeeka.test.domain;

import com.qeeka.annotation.Column;
import com.qeeka.annotation.Entity;
import com.qeeka.annotation.Id;
import com.qeeka.enums.GenerationType;

/**
 * Created by Neal on 8/9 0009.
 */
@Entity(table = "book")
public class Book {
    @Id(strategy = GenerationType.AUTO)
    private Integer id;

    @Column("name")
    private String name;

    @Column("type")
    private Integer type;

    @Column("status")
    private Integer status;

    @Column
    private Integer authorId;

    //skip column
    private Integer total;

    private String authorName;

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

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }
}
