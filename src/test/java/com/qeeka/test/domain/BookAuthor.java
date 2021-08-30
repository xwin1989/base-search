package com.qeeka.test.domain;


import com.qeeka.annotation.Entity;
import com.qeeka.annotation.Id;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Created by neal.xu on 2015/10/14
 */
@Table("book_author")
public class BookAuthor {
    @Id
    private Integer id;

    @Column("name")
    private String name;

    @Column("book_id")
    private Integer bookId;

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

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
}
