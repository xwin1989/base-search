package com.qeeka.test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.qeeka.domain.QueryGroup;
import com.qeeka.http.BaseSearchRequest;
import com.qeeka.http.BaseSearchResponse;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    public void testUnique() {
        QueryRequest request = new QueryRequest();
        request.setNeedCount(true);
        Book book1 = bookService.findUnique("from Book where id > :id order by id", Collections.<String, Object>singletonMap("id", 0));
        Book book2 = bookService.findUnique("from Book where id = 1");
        Assert.assertEquals(book1.getId(), book2.getId());
        Assert.assertEquals(book1.getName(), "book2");
        Assert.assertEquals(book2.getName(), "book2");
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
        ).uniqueResult();

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
    public void testQuery() {
        List<Book> all1 = bookService.queryAll();
        List<Book> all2 = bookService.findAll();
        List<Book> all3 = bookService.findAll2();
        Assert.assertEquals(all1.size(), all2.size());
        Assert.assertEquals(all1.size(), all3.size());
        Integer total = bookService.count();
        Integer typeById = bookService.getTypeById(1);
        Long count = bookService.countByUnique();
        Assert.assertTrue(all1.size() == 3);
        Assert.assertTrue(total == 3);
        Assert.assertTrue(typeById == 2);
        Assert.assertTrue(count == 3);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testGroup() {
        Map<String, Object> stringObjectMap = bookService.queryGroup();
        List<Object[]> objects = bookService.findGroup();
        Assert.assertTrue(objects.get(0)[0] == stringObjectMap.get("status"));
        Assert.assertTrue(objects.get(0)[1] == stringObjectMap.get("c1"));
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testCrossJoinSelect() {
        QueryGroup group = new QueryGroup("E.status", 1).crossJoin("BookInfo", "B").on("E.id", "B.book.id")
                .crossJoin("BookAuthor", "BA").on("BA.bookId", "E.id").and(
                        new QueryGroup("E.type", 1).or("E.type", 2).or("E.type", 3)
                ).and("BA.name", QueryOperate.IS_NOT_NULL).sort(new Sort(Direction.ASC, "E.id"));
        QueryResponse<Book> response = bookService.search(new QueryRequest(group).needCount().setPageSize(5));
        Assert.assertTrue(response.getRecords().size() == 1);
        Assert.assertTrue(response.getTotalRecords() == 1);
        Assert.assertTrue("book1".equals(response.getRecords().get(0).getName()));
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testBookMap() {
        BaseSearchRequest request = new BaseSearchRequest(0, 5);
        QueryResponse<Book> response = bookService.search(new QueryRequest(new QueryGroup()).needCount().setSearchRequest(request));
        Map<Object, Book> recordMap = response.getRecordsMap();
        Assert.assertTrue(recordMap.size() == 3);
        Assert.assertTrue(recordMap.containsKey(0) && recordMap.containsKey(1) && recordMap.containsKey(3));
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testBookCount() {
        QueryResponse<Book> response = bookService.search(new QueryRequest(new QueryGroup()).onlyCount());
        Assert.assertTrue(response.getRecords() == null);
        Assert.assertTrue(response.getTotalRecords() == 3L);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testBookMapWithParam() {
        BaseSearchRequest request = new BaseSearchRequest(0, 5);
        List<Integer> ids = Arrays.asList(1, 3);
        QueryGroup group = new QueryGroup("id", ids, QueryOperate.IN);
        QueryResponse<Book> queryResponse = bookService.search(new QueryRequest(group).needCount().setSearchRequest(request));
        Map<Object, Book> recordMap = queryResponse.getRecordsMap();
        Assert.assertTrue(recordMap.size() == 2);
        Assert.assertTrue(recordMap.containsKey(1) && recordMap.containsKey(3) && !recordMap.containsKey(0));
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testJoin() {
        QueryGroup group = new QueryGroup().join("E.bookInfoList", "BI").and("BI.name", "book2", QueryOperate.CONTAIN);
        QueryResponse<Book> queryResponse = bookService.search(new QueryRequest(group).needCount().needDistinct());
        Assert.assertEquals(queryResponse.getTotalRecords().intValue(), 1);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testLeftJoin2() {
        QueryGroup group = new QueryGroup("E.id", Arrays.asList(1, 2, 3, 4, 0), QueryOperate.IN).leftJoin("E.bookInfoList", "BI");
        QueryResponse<Book> queryResponse = bookService.search(new QueryRequest(group));
        Assert.assertEquals(queryResponse.getRecords().size(), 3);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testJoinFetch() {
        QueryGroup group = new QueryGroup().joinFetch("E.bookInfoList", "BI").and("BI.name", "book2", QueryOperate.CONTAIN);
        QueryResponse<Book> queryResponse = bookService.search(new QueryRequest(group).needCount().needDistinct());
        Assert.assertEquals(queryResponse.getTotalRecords().intValue(), 1);
    }


    @Test
    @DatabaseSetup("/BookData.xml")
    public void testLeftJoin() {
        QueryGroup group = new QueryGroup().leftJoin("E.bookInfoList", "BI").and("BI.name", "book2", QueryOperate.CONTAIN);
        QueryResponse<Book> queryResponse = bookService.search(group);
        Assert.assertEquals(queryResponse.getRecords().size(), 1);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    public void testLeftJoinFetch() {
        QueryGroup group = new QueryGroup().leftJoinFetch("E.bookInfoList", "BI").and("BI.name", "book2", QueryOperate.CONTAIN);
        QueryResponse<Book> queryResponse = bookService.search(group);
        Assert.assertEquals(queryResponse.getRecords().size(), 1);
    }

    @Test
    @DatabaseSetup("/BookData.xml")
    @Transactional
    public void testNativeUpdate() {
        int i = bookService.updateBookStatus(1, 3);
        Assert.assertEquals(i, 1);
        Book book = bookService.search(new QueryRequest(new QueryGroup("id", 1)).uniqueResult()).getEntity();
        Assert.assertTrue(book.getStatus() == 3);
        int count = bookService.updateAllBookStatus(5);
        Assert.assertEquals(count, 3);
        Long statusCount = bookService.count(new QueryGroup("status", 5).and("userId", 1));
        Assert.assertEquals(statusCount.intValue(), 3);

        count = bookService.updateNativeAllBookStatus(6);
        Assert.assertEquals(count, 3);
        statusCount = bookService.count(new QueryGroup("status", 6).and("userId", 2));
        Assert.assertEquals(statusCount.intValue(), 3);

        Long countAll = bookService.countAll();
        Assert.assertEquals(countAll.intValue(), 3);
    }

//    @Test
//    @Transactional
//    public void testBatchSave() {
//        List<Book> books = bookService.batchSave();
//        for (int i = 1; i < 5; i++) {
//            Book book = books.get(i - 1);
//            Assert.assertEquals(book.getName(), "book" + i);
//            Assert.assertEquals(book.getId(), Integer.valueOf(i));
//            book.setName("new book" + i);
//        }
//        books = bookService.batchUpdate(books);
//        for (int i = 1; i < 5; i++) {
//            Book book = books.get(i - 1);
//            Assert.assertEquals(book.getName(), "new book" + i);
//            Assert.assertEquals(book.getId(), Integer.valueOf(i));
//        }
//    }
//
//    @Test
//    @Transactional
//    public void testBatchNativeUpdate() {
//        List<Book> books = new ArrayList<>();
//        for (int i = 0; i < 100; i++) {
//            Book book = new Book();
//            book.setName("book" + i);
//            books.add(book);
//        }
//        bookService.batchNativeUpdate("insert into book(name) values(:name)", books);
//        Assert.assertEquals(bookService.count(), Integer.valueOf(100));
//
//        bookService.batchNativeUpdate("insert into book(name) values(:name)", books);
//        Assert.assertEquals(bookService.count(), Integer.valueOf(200));
//    }
//
//    @Test
//    @DatabaseSetup("/BookData.xml")
//    @Transactional
//    public void testBatchNativeUpdate2() {
//        List<Book> bookList = new ArrayList<>(10);
//        for (int i = 0; i < 2; i++) {
//            Book book1 = new Book();
//            book1.setId(i);
//            book1.setName("new book" + i);
//            book1.setUserId(i);
//            bookList.add(book1);
//        }
//        bookService.batchNativeUpdate("update book set name=:name,user_id=:userId where id = :id", bookList);
//        Book book = bookService.getBook(0);
//        Assert.assertEquals(book.getName(), "new book0");
//        Assert.assertEquals(book.getType(), Integer.valueOf(1));
//        Assert.assertEquals(book.getStatus(), Integer.valueOf(1));
//        Assert.assertEquals(book.getUserId(), Integer.valueOf(0));
//    }
//
//    @Test
//    @DatabaseSetup("/BookData.xml")
//    @Transactional
//    public void testBatchNativeUpdate3() {
//        Map<String, Object>[] values = new HashMap[2];
//        for (int i = 0; i < 2; i++) {
//            Map<String, Object> map = new HashMap<>();
//            map.put("name", "map book" + i);
//            map.put("id", i);
//            map.put("userId", i);
//            values[i] = map;
//        }
//        bookService.batchNativeUpdate("update book set name=:name,user_id=:userId where id = :id", values);
//        Book book = bookService.getBook(0);
//        Assert.assertEquals(book.getName(), "map book0");
//        Assert.assertEquals(book.getType(), Integer.valueOf(1));
//        Assert.assertEquals(book.getStatus(), Integer.valueOf(1));
//        Assert.assertEquals(book.getUserId(), Integer.valueOf(0));
//    }

    @Test
    public void testLog() {
        bookService.testLog();
    }
}
