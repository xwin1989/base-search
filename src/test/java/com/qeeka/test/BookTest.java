package com.qeeka.test;

import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryResponse;
import com.qeeka.enums.Direction;
import com.qeeka.enums.QueryOperate;
import com.qeeka.http.BaseRequest;
import com.qeeka.test.domain.Book;
import com.qeeka.test.domain.SimpleMapping;
import com.qeeka.test.service.BookService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

    @Before
    @Transactional
    public void init() {
        bookService.update("create table book(id int,name varchar(50),status int,type int,author_id int)");
        bookService.update("insert into book values(0,'book1',1,1,0)");
        bookService.update("insert into book values(1,'book2',1,2,null)");
        bookService.update("insert into book values(3,'book3',1,3,null)");

        bookService.update("create table book_info(id int,name varchar(50),book_id int)");
        bookService.update("insert into book_info values(0,'book1 info',0)");
        bookService.update("insert into book_info values(1,'book2 info',1)");

        bookService.update("create table book_author(id int,name varchar(50),book_id int)");
        bookService.update("insert into book_author values(0,'neal',0)");
        bookService.update("insert into book_author values(1,'kimi',1)");
        bookService.update("insert into book_author values(2,'jack',3)");

    }

    @After
    @Transactional
    public void after() {
        bookService.update("drop table book");
        bookService.update("drop table book_info");
        bookService.update("drop table book_author");
    }


    @Test
    public void testSelectAll() {
        QueryResponse<Book> response = bookService.search(new QueryGroup().needCount());
        Assert.assertTrue(response.getTotalRecords() == 3);
    }

    @Test
    public void testUnique() {
        Book book1 = bookService.findUnique("select * from Book where id = :id", Collections.<String, Object>singletonMap("id", 0));
        Book book2 = bookService.findUnique(new QueryGroup("id", 0));
        Assert.assertEquals(book1.getId(), book2.getId());
        Assert.assertEquals(book1.getName(), book2.getName());
    }

    @Test
    public void testBookSearch() {
        QueryGroup queryGroup = new QueryGroup("name", "book", QueryOperate.CONTAIN).and("status", 0, QueryOperate.GREAT_THAN)
                .sort(Direction.DESC, "type").needCount().setPageable(0, 2);
        QueryResponse<Book> response = bookService.search(queryGroup);
        Assert.assertTrue(response.getRecords().size() == 2);
        Assert.assertTrue(response.getRecords().get(0).getId() == 3);
        Assert.assertTrue(response.getTotalRecords() == 3);
    }

    @Test
    public void testBookSearchExcludeCount() {
        QueryGroup queryGroup = new QueryGroup("name", "book", QueryOperate.CONTAIN).and("status", 0, QueryOperate.GREAT_THAN)
                .sort(Direction.DESC, "type").setPageable(0, 2);

        QueryResponse<Book> response = bookService.search(queryGroup);
        Assert.assertTrue(response.getRecords().size() == 2);
        Assert.assertTrue(response.getRecords().get(0).getId() == 3);
        Assert.assertTrue(response.getTotalRecords() == null);
    }

    @Test
    public void testBookSearchExcludeRecord() {
        QueryGroup queryGroup = new QueryGroup("name", "book", QueryOperate.CONTAIN).and("status", 0, QueryOperate.GREAT_THAN)
                .sort(Direction.DESC, "type").needCount().setPageable(0, 2);

        QueryResponse<Book> response = bookService.search(queryGroup);
        Assert.assertEquals(response.getRecords().size(), 2);
        Assert.assertEquals(response.getTotalRecords(), Long.valueOf(3));
    }

    @Test
    @Transactional
    public void testSaveBook() {
        Book book = new Book();
        book.setId(4);
        book.setName("book4");
        book.setStatus(1);
        bookService.save(book);

        QueryGroup group = new QueryGroup("name", "book4").onlyCount();

        Assert.assertTrue(bookService.search(group).getTotalRecords() == 1);

        bookService.delete(book.getId());

        Assert.assertTrue(bookService.search(group).getTotalRecords() == 0);

    }

    @Test
    @Transactional
    public void testUpdateBook() {
        Book book = new Book();
        book.setName("book5");
        book.setStatus(0);
        book.setId(5);
        bookService.save(book);

        QueryGroup group = new QueryGroup("name", "book5");

        Book book2 = bookService.searchUnique(group);
        Assert.assertTrue(book2.getStatus() == 0);

        book2.setStatus(2);
        bookService.update(book2);

        Book book3 = bookService.searchUnique(group);
        Assert.assertTrue(book3.getStatus() == 2);

        bookService.delete(book3.getId());
    }

    @Test
    public void testGetBook() {
        Book book = bookService.getBook(1);
        Assert.assertEquals(book.getName(), "book2");
    }


    @Test
    public void testAllBook() {
        QueryResponse<Book> searchResponse = bookService.search(new QueryGroup().needCount().setPageSize(2));
        Assert.assertTrue(searchResponse.getRecords().size() == 2);
        Assert.assertTrue(searchResponse.getTotalRecords() == 3);
        Assert.assertTrue(searchResponse.getPageIndex() == 0);
        Assert.assertTrue(searchResponse.getPageSize() == 2);
    }

    @Test
    public void testSearchRequest() {
        BaseRequest baseRequest = new BaseRequest(0, 2);
        QueryResponse<Book> searchResponse = bookService.search(new QueryGroup().needCount().setSearchRequest(baseRequest));
        Assert.assertTrue(searchResponse.getRecords().size() == 2);
        Assert.assertTrue(searchResponse.getTotalRecords() == 3);
        Assert.assertTrue(searchResponse.getPageIndex() == 0);
        Assert.assertTrue(searchResponse.getPageSize() == 2);
    }

    @Test
    public void testQuery() {
        List<Book> all1 = bookService.queryAll();
        List<Book> all3 = bookService.findAll2();
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
    public void testGroup() {
        Map<String, Object> stringObjectMap = bookService.queryGroup();
        List<Book> objects = bookService.findGroup();
        Assert.assertEquals(objects.get(0).getStatus(), Integer.valueOf(1));
        Assert.assertEquals(objects.get(0).getTotal(), Integer.valueOf(3));

        Assert.assertEquals(stringObjectMap.get("total"), Long.valueOf(3));
    }


    @Test
    public void testBookMap() {
        BaseRequest request = new BaseRequest(0, 5);
        QueryResponse<Book> response = bookService.search(new QueryGroup().needCount().setSearchRequest(request));
        Map<Object, Book> recordMap = response.getRecordsMap();
        Assert.assertTrue(recordMap.size() == 3);
        Assert.assertTrue(recordMap.containsKey(0) && recordMap.containsKey(1) && recordMap.containsKey(3));
    }

    @Test
    public void testBookCount() {
        Long count = bookService.count(new QueryGroup());
        Assert.assertTrue(count == 3L);
    }

    @Test
    public void testBookMapWithParam() {
        BaseRequest request = new BaseRequest(0, 5);
        List<Integer> ids = Arrays.asList(1, 3);
        QueryGroup group = new QueryGroup("id", ids, QueryOperate.IN);
        QueryResponse<Book> queryResponse = bookService.search(group.needCount().setSearchRequest(request));
        Map<Object, Book> recordMap = queryResponse.getRecordsMap();
        Assert.assertTrue(recordMap.size() == 2);
        Assert.assertTrue(recordMap.containsKey(1) && recordMap.containsKey(3) && !recordMap.containsKey(0));
    }

    @Test
    public void testJoin() {
        QueryGroup group = new QueryGroup("BI.name", "book2", QueryOperate.CONTAIN)
                .join("book_info", "BI").on("BI.book_id", "E.id");
        QueryResponse<Book> queryResponse = bookService.search(group.needCount().needDistinct());
        Assert.assertEquals(queryResponse.getTotalRecords().intValue(), 1);
    }

    @Test
    public void testLeftJoin() {
        QueryGroup group = new QueryGroup("E.id", Arrays.asList(1, 2, 3, 4, 0), QueryOperate.IN)
                .leftJoin("book_info", "BI").on("BI.book_id", "E.id");
        QueryResponse<Book> queryResponse = bookService.search(group);
        Assert.assertEquals(queryResponse.getRecords().size(), 3);
    }


    @Test
    public void testLeftJoinFetch() {
        QueryGroup group = new QueryGroup().leftJoin("book_info", "BI").on("BI.book_id", "E.id")
                .and("BI.name", "book2", QueryOperate.CONTAIN);
        QueryResponse<Book> queryResponse = bookService.search(group);
        Assert.assertEquals(queryResponse.getRecords().size(), 1);
        List<Object> recordsKey = queryResponse.getRecordsKey();
        Assert.assertEquals(recordsKey.size(), 1);
        Map<Object, List<Book>> multiRecordMap = queryResponse.getMultiRecordMap();
        Assert.assertEquals(multiRecordMap.size(), 1);
    }

    //hsqldb can't support cross join
    @Test
    public void testCrossJoinSelect() {
//        QueryGroup group = new QueryGroup("E.status", 1)
//                .crossJoin("book_info", "BI").on("E.id", "BI.book_id")
//                .and(new QueryGroup("E.type", 1).or("E.type", 2).or("E.type", 3))
//                .and("BI.name", QueryOperate.IS_NOT_NULL).sort(Direction.ASC, "E.id");
//        QueryResponse<Book> response = bookService.search(group.needCount().setPageSize(5));
//        Assert.assertTrue(response.getRecords().size() == 1);
//        Assert.assertTrue(response.getTotalRecords() == 1);
//        Assert.assertTrue("book1".equals(response.getRecords().get(0).getName()));
    }

    @Test
    public void testLeftOutJoinFetch() {
        QueryGroup group = new QueryGroup().leftOutJoin("book_info", "BI").on("BI.book_id", "E.id")
                .and("BI.name", "book2", QueryOperate.CONTAIN);
        QueryResponse<Book> queryResponse = bookService.search(group);
        Assert.assertEquals(queryResponse.getRecords().size(), 1);
        List<Object> recordsKey = queryResponse.getRecordsKey();
        Assert.assertEquals(recordsKey.size(), 1);
        Map<Object, List<Book>> multiRecordMap = queryResponse.getMultiRecordMap();
        Assert.assertEquals(multiRecordMap.size(), 1);
    }


    @Test
    @Transactional
    public void testNativeUpdate() {
        int i = bookService.updateBookStatus(1, 3);
        Assert.assertEquals(i, 1);
        Book book = bookService.searchUnique(new QueryGroup("id", 1));
        Assert.assertTrue(book.getStatus() == 3);
        int count = bookService.updateAllBookStatus(5);
        Assert.assertEquals(count, 3);
        Long statusCount = bookService.count(new QueryGroup("status", 5));
        Assert.assertEquals(statusCount.intValue(), 3);

        count = bookService.updateNativeAllBookStatus(6);
        Assert.assertEquals(count, 3);
        statusCount = bookService.count(new QueryGroup("status", 6));
        Assert.assertEquals(statusCount.intValue(), 3);

        Long countAll = bookService.countAll();
        Assert.assertEquals(countAll.intValue(), 3);
    }

    @Test
    public void testFind() {
        List<SimpleMapping> list = bookService.findBySql("select type as key,count(id) as value from Book E Group by type", SimpleMapping.class);
        Assert.assertEquals(list.size(), 3);
    }

    @Test
    @Transactional
    public void testBatchSave() {
        List<Book> books = bookService.batchSave();
        for (int i = 1; i < 5; i++) {
            Book book = books.get(i - 1);
            Assert.assertEquals(book.getName(), "book" + i);
            Assert.assertEquals(book.getId(), Integer.valueOf(i));
            book.setName("new book" + i);
        }
        bookService.batchUpdate(books);
        for (int i = 1; i < 5; i++) {
            Book book = books.get(i - 1);
            Assert.assertEquals(book.getName(), "new book" + i);
            Assert.assertEquals(book.getId(), Integer.valueOf(i));
        }
    }

    @Test
    @Transactional
    public void testBatchNativeUpdate() {
        List<Book> books = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Book book = new Book();
            book.setName("book" + i);
            book.setId(i);
            books.add(book);
        }
        int[] updates = bookService.batchNativeUpdate("insert into book(name) values(:name)", books);
        Assert.assertEquals(updates.length, 10);

        updates = bookService.batchNativeUpdate("insert into book(name) values(:name)", books);
        Assert.assertEquals(updates.length, 10);
    }

    @Test
    public void testTransientQuery() {
        QueryGroup group = new QueryGroup()
                .join("book_info", "BI").on("BI.book_id", "E.id").on("BI.name", "hello", QueryOperate.NO_EQUALS)
                .leftJoin("book_author", "BA").on("BA.id", "E.author_id")
                .and("BI.name", "1", QueryOperate.CONTAIN);
        QueryResponse<Book> response = bookService.query(group.needCount().setPageSize(3)
                .selects("E.*", "BA.name as authorName", "BI.name AS bookDescription")
        );
        Assert.assertEquals(response.getRecords().size(), 1);
        Assert.assertEquals(response.getTotalRecords(), new Long(1));
        Assert.assertEquals(response.getRecords().get(0).getAuthorName(), "neal");
    }

    @Test
    public void testNativeQuery2() {
        QueryResponse<Book> response = bookService.query(new QueryGroup(1, 2));
        Assert.assertEquals(response.getRecords().size(), 1);
    }

    @Test
    public void testNativeQueryAll() {
        QueryResponse<Book> q2 = bookService.queryAll2();
        QueryResponse<Book> q3 = bookService.queryAll3();
        Assert.assertEquals(q2.getRecords().size(), q3.getRecords().size());
    }


    @Test
    public void testUnique2() {
        Book b1 = bookService.queryUnique(new QueryGroup("id", 0));
        Book b2 = bookService.queryUnique2(new QueryGroup("id", 3));
        Assert.assertEquals(b1.getStatus(), b2.getStatus());
    }

    @Test
    public void testSingle() {
        Book b1 = bookService.querySingle1(new QueryGroup("id", 0));
        Book b2 = bookService.querySingle2(new QueryGroup("id", 3));
        Assert.assertEquals(b1.getStatus(), b2.getStatus());
    }
}
