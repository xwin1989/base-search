package com.qeeka.test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.qeeka.domain.QueryGroup;
import com.qeeka.http.QueryRequest;
import com.qeeka.http.QueryResponse;
import com.qeeka.operate.Direction;
import com.qeeka.operate.QueryOperate;
import com.qeeka.operate.Sort;
import com.qeeka.test.domain.Book;
import com.qeeka.test.service.BookService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
