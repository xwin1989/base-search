package com.qeeka.test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.qeeka.domain.QueryGroup;
import com.qeeka.http.BaseSearchRequest;
import com.qeeka.http.BaseSearchResponse;
import com.qeeka.http.QueryRequest;
import com.qeeka.http.QueryResponse;
import com.qeeka.operate.Direction;
import com.qeeka.operate.QueryOperate;
import com.qeeka.operate.QueryResultType;
import com.qeeka.operate.Sort;
import com.qeeka.test.domain.Book;
import com.qeeka.test.service.BookService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by Neal on 2015/7/27.
 */
public class BookTest extends SpringTestWithDB {


    @Autowired
    private BookService bookService;


    @Test
    @DatabaseSetup("/BookData.xml")
    public void testSelectAll() {
        QueryRequest request = new QueryRequest();
        request.setNeedCount(true);
        QueryResponse<Book> response = bookService.search(request);
        Assert.assertTrue(response.getTotalRecords() == 3);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testBookSearch() {
        QueryRequest request = new QueryRequest(0, 2);
        QueryGroup queryGroup = new QueryGroup("name", "book", QueryOperate.CONTAIN).and("status", 0, QueryOperate.GREAT_THAN)
                .sort(new Sort(Direction.DESC, "type"));
        request.setQueryGroup(queryGroup);
        request.setNeedCount(true);
        QueryResponse<Book> response = bookService.search(request);
        Assert.assertTrue(response.getRecords().size() == 2);
        Assert.assertTrue(response.getRecords().get(0).getId() == 3);
        Assert.assertTrue(response.getTotalRecords() == 3);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testBookSearchExcludeCount() {
        QueryRequest request = new QueryRequest(0, 2);
        QueryGroup queryGroup = new QueryGroup("name", "book", QueryOperate.CONTAIN).and("status", 0, QueryOperate.GREAT_THAN)
                .sort(new Sort(Direction.DESC, "type"));
        request.setQueryGroup(queryGroup);
        request.setNeedCount(false);

        QueryResponse<Book> response = bookService.search(request);
        Assert.assertTrue(response.getRecords().size() == 2);
        Assert.assertTrue(response.getRecords().get(0).getId() == 3);
        Assert.assertTrue(response.getTotalRecords() == null);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testBookSearchExcludeRecord() {
        QueryRequest request = new QueryRequest(0, 2);
        QueryGroup queryGroup = new QueryGroup("name", "book", QueryOperate.CONTAIN).and("status", 0, QueryOperate.GREAT_THAN)
                .sort(new Sort(Direction.DESC, "type"));
        request.setQueryGroup(queryGroup);
        request.setNeedRecord(false);
        request.setNeedCount(true);

        QueryResponse<Book> response = bookService.search(request);
        Assert.assertTrue(response.getRecords() == null);
        Assert.assertTrue(response.getTotalRecords() == 3);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testBookSearchExcludeALL() {
        QueryRequest request = new QueryRequest(0, 2);
        QueryGroup queryGroup = new QueryGroup("name", "book", QueryOperate.CONTAIN).and("status", 0, QueryOperate.GREAT_THAN)
                .sort(new Sort(Direction.DESC, "type"));
        request.setQueryGroup(queryGroup);
        request.setNeedRecord(false);
        request.setNeedCount(false);

        QueryResponse<Book> response = bookService.search(request);
        Assert.assertTrue(response.getRecords() == null);
        Assert.assertTrue(response.getTotalRecords() == null);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    @Transactional
    public void testSaveBook() {
        Book book = new Book();
        book.setName("book4");
        book.setStatus(1);
        bookService.save(book);

        QueryRequest request = new QueryRequest(
                new QueryGroup("name", "book4")
        ).setNeedCount(true).setNeedRecord(false);

        Assert.assertTrue(bookService.search(request).getTotalRecords() == 1);

        bookService.delete(book.getId());

        Assert.assertTrue(bookService.search(request).getTotalRecords() == 0);

    }

    @Test
    @DatabaseSetup("/BookData.xml")
    @Transactional
    public void testUpdateBook() {
        Book book = new Book();
        book.setName("book5");
        book.setStatus(0);
        bookService.save(book);

        QueryRequest request = new QueryRequest(
                new QueryGroup("name", "book5")
        ).setQueryResultType(QueryResultType.UNIQUE);

        Book book2 = bookService.search(request).getEntity();
        Assert.assertTrue(book2.getStatus() == 0);

        book2.setStatus(2);
        bookService.update(book2);

        Book book3 = bookService.search(request).getEntity();
        Assert.assertTrue(book3.getStatus() == 2);

        bookService.delete(book3.getId());
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testGetBook() {
        Book book = bookService.getBook(1);
        Assert.assertEquals(book.getName(), "book2");
    }


    @Test
    @DatabaseSetup("/BookData.xml")
    public void testAllBook() {
        QueryResponse<Book> response = bookService.search(new QueryRequest(new QueryGroup()).needCount().setPageSize(2));
        BaseSearchResponse<Book> bookBaseSearchResponse = new BaseSearchResponse<>();
        BaseSearchResponse<Book> searchResponse = response.assignmentToResponse(bookBaseSearchResponse);
        Assert.assertTrue(response.getRecords().size() == 2);
        Assert.assertTrue(searchResponse.getTotalRecords() == 3);
        Assert.assertTrue(searchResponse.getPageIndex() == 0);
        Assert.assertTrue(searchResponse.getPageSize() == 2);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testSearchRequest() {
        BaseSearchRequest baseSearchRequest = new BaseSearchRequest(0, 2);
        QueryResponse<Book> response = bookService.search(new QueryRequest(new QueryGroup()).needCount().setSearchRequest(baseSearchRequest));
        BaseSearchResponse<Book> bookBaseSearchResponse = new BaseSearchResponse<>();
        BaseSearchResponse<Book> searchResponse = response.assignmentToResponse(bookBaseSearchResponse);
        Assert.assertTrue(response.getRecords().size() == 2);
        Assert.assertTrue(searchResponse.getTotalRecords() == 3);
        Assert.assertTrue(searchResponse.getPageIndex() == 0);
        Assert.assertTrue(searchResponse.getPageSize() == 2);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testNativeQuery() {
        List<Book> all = bookService.findAll();
        Integer total = bookService.count();
        Integer typeById = bookService.getTypeById(1);
        Assert.assertTrue(all.size() == 3);
        Assert.assertTrue(total == 3);
        Assert.assertTrue(typeById == 2);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testJoinSelect() {
        QueryGroup group = new QueryGroup("E.status", 1).join("BookInfo", "B").on("E.id", "B.bookId")
                .join("BookAuthor", "BA").on("BA.bookId", "E.id").and(
                        new QueryGroup("E.type", 1).or("E.type", 2).or("E.type", 3)
                ).and("BA.name", QueryOperate.IS_NOT_NULL).sort(new Sort(Direction.ASC, "E.id"));
        QueryResponse<Book> response = bookService.search(new QueryRequest(group));
        Assert.assertTrue(response.getRecords().size() == 1);
        Assert.assertTrue("book1".equals(response.getRecords().get(0).getName()));
    }
}
