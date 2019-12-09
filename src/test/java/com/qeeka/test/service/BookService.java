package com.qeeka.test.service;

import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryResponse;
import com.qeeka.domain.UpdateGroup;
import com.qeeka.test.domain.Book;
import com.qeeka.test.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
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

    public QueryResponse<Book> search(QueryGroup request) {
//        return repository.query(request);
        return null;
    }

    public Book searchUnique(QueryGroup group) {
//        return repository.queryUnique(group);
        return null;}

    public Book findUnique(String hql, Map<String, Object> params) {
        return repository.queryUnique(hql, params);
    }

    public Book findUnique(QueryGroup group) {
//        return repository.queryUnique(group);
        return null;}

    public Book queryUnique(QueryGroup hql) {
//        return repository.queryUnique(hql);
        return null;}

    public Book queryUnique2(QueryGroup queryGroup) {
//        return repository.queryUnique(queryGroup, Book.class);
        return null;}


    public Book querySingle1(QueryGroup queryRequest) {
//        return repository.querySingle(queryRequest);
        return null;}

    public Book querySingle2(QueryGroup queryGroup) {
//        return repository.querySingle(queryGroup);
        return null;}

    public QueryResponse<Book> query(QueryGroup queryGroup) {
//        return repository.query(queryGroup);
        return null;}

    public QueryResponse<Book> queryAll2() {
        return repository.query(Book.class);
    }

    public QueryResponse<Book> queryAll3() {
        return repository.query();
    }

    public List<Book> queryAll() {
        String sql = "select * from book";
        return repository.query(sql);
    }

    public List<Book> find(String hql) {
        return repository.query(hql);
    }


    public <X> List<X> findBySql(String sql, Class<X> clazz) {
        return repository.query(sql, clazz);
    }

    public List<Book> findAll2() {
        return repository.query().getRecords();
    }

    public Integer count() {
        String sql = "select count(1) from book";
        BigInteger total = repository.queryForObject(sql, BigInteger.class);
        return total.intValue();
    }

    public Long count(QueryGroup queryGroup) {
        return repository.count(queryGroup);
    }

    public Long countAll() {
        return repository.count();
    }

    public Long countByUnique() {
        String hql = "select count(1) from Book";
        return repository.queryForObject(hql, Long.class);
    }

    public Integer getTypeById(Integer id) {
        Book book = repository.queryUnique("select * from book where id = :id", Collections.<String, Object>singletonMap("id", id), Book.class);
        String sql = "select type from book where id = :id";
        Integer type = repository.queryForObject(sql, Collections.<String, Object>singletonMap("id", book.getId()), Integer.class);
        if (type != null) {
            type = repository.queryForObject("select type from book where id = :id", Collections.<String, Object>singletonMap("id", id), Integer.class);
            return type;
        }
        return null;
    }

    public List<Book> findGroup() {
        return repository.query("select status,COUNT(id) as total from Book group by status");
    }

    public Map<String, Object> queryGroup() {
        return repository.queryForMap("select status,count(id) as total from book group by status");
    }

    @Transactional
    public int updateBookStatus(Integer bookId, Integer status) {
        return repository.update(new UpdateGroup("status", status).where("id", bookId));
    }

    @Transactional
    public int updateAllBookStatus(Integer status) {
        return repository.update("update book set status = :status", Collections.<String, Object>singletonMap("status", status));
    }

    @Transactional
    public int updateNativeAllBookStatus(Integer status) {
        return repository.update("update book set status = :status", Collections.<String, Object>singletonMap("status", status));
    }

    @Transactional
    public void save(Book book) {
        repository.save(book);
    }

    @Transactional
    public void update(Book book) {
        repository.update(book);
    }

    @Transactional
    public void update(String sql) {
        repository.update(sql);
    }

    public Book getBook(Integer id) {
        return repository.get(id);
    }

    @Transactional
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Transactional
    public List<Book> batchSave() {
        List<Book> books = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            Book book = new Book();
            book.setName("book" + i);
            book.setId(i);
            books.add(book);
        }
        return repository.batchSave(books);
    }

    @Transactional
    public int[] batchUpdate(List<Book> books) {
        return repository.batchUpdate(books);
    }

    @Transactional
    public int[] batchNativeUpdate(String sql, List<Book> books) {
        return repository.batchUpdate(sql, books);
    }

    @Transactional
    public int[] batchNativeUpdate(String sql, Map<String, ?>[] batchValues) {
        return repository.batchUpdate(sql, batchValues);
    }

}
