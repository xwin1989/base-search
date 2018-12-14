package com.qeeka.test.domain;


import com.qeeka.annotation.Column;
import com.qeeka.annotation.Entity;
import com.qeeka.annotation.Id;

/**
 * Created by neal.xu on 2015/10/14
 */
@Entity(table = "book_info")
public class BookInfo {
    @Id
    private Integer id;

    @Column( "name")
    private String name;

    private Book book;

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

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }
}
