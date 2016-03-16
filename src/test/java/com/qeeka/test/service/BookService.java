package com.qeeka.test.service;

import com.qeeka.domain.QueryGroup;
import com.qeeka.http.QueryRequest;
import com.qeeka.http.QueryResponse;
import com.qeeka.test.domain.Book;
import com.qeeka.test.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    public QueryResponse<Book> search(QueryGroup queryGroup) {
        return repository.search(queryGroup);
    }

    public List<Book> findAll() {
        String sql = "select * from book";
        return repository.findByNativeQuery(sql);
    }

    public List<Book> findAll2() {
        return repository.search().getRecords();
    }

    public Integer count() {
        String sql = "select count(1) from book";
        BigInteger total = repository.findUniqueNativeQuery(sql);
        return total.intValue();
    }

    public Long count(QueryGroup queryGroup) {
        return repository.count(queryGroup);
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
    public int updateBookStatus(Integer bookId, Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", bookId);
        params.put("status", status);
        return repository.updateNaive("update book set status = :status where id = :id", params);
    }

    @Transactional
    public int updateAllBookStatus(Integer status) {
        return repository.updateNaive("update book set status = :status", Collections.<String, Object>singletonMap("status", status));
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
