package com.qeeka.test.service;

import com.qeeka.http.QueryRequest;
import com.qeeka.http.QueryResponse;
import com.qeeka.test.domain.Book;
import com.qeeka.test.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * Created by Neal on 2015/7/27.
 */
@Service
@Transactional
public class BookService {

    @Autowired
    private BookRepository repository;

    public QueryResponse<Book> search(QueryRequest request) {
        return repository.search(request);
    }

    public List<Book> findAll() {
        String sql = "select * from book";
        return repository.findByNativeQuery(sql);
    }

    public Integer count() {
        String sql = "select count(1) from book";
        BigInteger total = repository.findUniqueNativeQuery(sql);
        return total.intValue();
    }

    public Integer getTypeById(Integer id) {
        Book book = repository.findUniqueNativeQuery("select * from book where id = :id", Collections.<String, Object>singletonMap("id", id), Book.class);
        String sql = "select type from book where id = :id";
        List<Integer> list = repository.findByNativeQuery(sql, Collections.<String, Object>singletonMap("id", book.getId()));
        if (!list.isEmpty()) {
            Integer type = repository.findUniqueNativeQuery("select type from book where id = :id", Collections.<String, Object>singletonMap("id", id));
            return type;
        }
        return null;
    }

    @Transactional
    public void save(Book book) {
        repository.save(book);
    }

    @Transactional
    public void update(Book book) {
        repository.update(book);
    }

    public Book getBook(Integer id) {
        return repository.get(id);
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
