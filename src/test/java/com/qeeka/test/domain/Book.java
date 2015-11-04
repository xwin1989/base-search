package com.qeeka.test.domain;

import com.qeeka.domain.MapHandle;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Neal on 8/9 0009.
 */
@Entity
@Table(name = "book")
public class Book implements MapHandle {
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private Integer type;

    @Column(name = "status")
    private Integer status;

    @OneToMany(mappedBy = "book")
    private List<BookInfo> bookInfoList;

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

    public List<BookInfo> getBookInfoList() {
        return bookInfoList;
    }

    public void setBookInfoList(List<BookInfo> bookInfoList) {
        this.bookInfoList = bookInfoList;
    }

    @Override
    public Object getPrimaryKey() {
        return this.id;
    }
}
