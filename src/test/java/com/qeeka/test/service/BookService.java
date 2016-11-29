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
import java.util.ArrayList;
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

    public Book findUnique(String hql, Map<String, Object> params) {
        return repository.findUnique(hql, params);
    }

    public Book findUnique(String hql) {
        return repository.findUnique(hql);
    }

    public Book queryUnique1(QueryRequest queryRequest) {
        return repository.queryUnique(queryRequest);
    }

    public Book queryUnique2(QueryGroup queryGroup) {
        return repository.queryUnique(queryGroup, Book.class);
    }


    public Book querySingle1(QueryRequest queryRequest) {
        return repository.querySingle(queryRequest);
    }

    public Book querySingle2(QueryGroup queryGroup) {
        return repository.querySingle(queryGroup);
    }

    public QueryResponse<Book> search(QueryGroup queryGroup) {
        return repository.search(queryGroup);
    }

    public QueryResponse<Book> query(QueryRequest queryRequest) {
        return repository.query(queryRequest);
    }

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

    public List<Book> findAll() {
        return repository.find("from Book");
    }

    public List<Book> findAll2() {
        return repository.search().getRecords();
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
        String hql = "select count(B) from Book B";
        return repository.findUnique(hql);
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

    public void testLog() {
        repository.testLog();
    }

    public List<Object[]> findGroup() {
        return repository.find("select status,COUNT(ID) from Book group by status");
    }

    public Map<String, Object> queryGroup() {
        return repository.queryForMap("select status,count(id) as c1 from book group by status");
    }

    @Transactional
    public int updateBookStatus(Integer bookId, Integer status) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", bookId);
        params.put("status", status);
        return repository.updateNative("update book set status = :status where id = :id", params);
    }

    @Transactional
    public int updateAllBookStatus(Integer status) {
        return repository.update("update Book set status = :status,userId=1", Collections.<String, Object>singletonMap("status", status));
    }

    @Transactional
    public int updateNativeAllBookStatus(Integer status) {
        return repository.updateNative("update book set status = :status,user_id=2", Collections.<String, Object>singletonMap("status", status));
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

    @Transactional
    public List<Book> batchSave() {
        List<Book> books = new ArrayList<>();
        for (int i = 1; i < 501; i++) {
            Book book = new Book();
            book.setName("book" + i);
            books.add(book);
        }
        return repository.batchSave(books);
    }

    @Transactional
    public List<Book> batchUpdate(List<Book> books) {
        return repository.batchUpdate(books);
    }

    @Transactional
    public int[] batchNativeUpdate(String sql, List<Book> books) {
        return repository.batchUpdateNative(sql, books);
    }

    @Transactional
    public int[] batchNativeUpdate(String sql, Map<String, ?>[] batchValues) {
        return repository.batchUpdateNative(sql, batchValues);
    }

}
