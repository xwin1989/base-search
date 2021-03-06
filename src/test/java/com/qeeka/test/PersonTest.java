package com.qeeka.test;

import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryResponse;
import com.qeeka.domain.UpdateGroup;
import com.qeeka.enums.QueryOperate;
import com.qeeka.test.domain.Person;
import com.qeeka.test.service.PersonService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Neal on 2015/7/27.
 */
public class PersonTest extends SpringTestWithDB {

    @Autowired
    private PersonService personService;

    @Before
    @Transactional
    public void init() {
        personService.update("create table person(id int,name varchar(50),password varchar(50),status int,type int)");
        personService.update("insert into person values(0,'neal1','p1',1,1)");
        personService.update("insert into person values(1,'neal2','p2',2,2)");
    }

    @After
    @Transactional
    public void after() {
        personService.update("drop table person");
    }


    @Test
    @Transactional
    public void testRemove() {
        int n1 = personService.delete(1);
        int n2 = personService.deleteById(2);
        int n3 = personService.delete2(0);
        Assert.assertEquals(n1, 1);
        Assert.assertEquals(n2, 0);
        Assert.assertEquals(n3, 1);
    }

    @Test
    public void testSearch() {
        List<Integer> ids = Arrays.asList(2, 3, 4, 5);
        QueryGroup group = new QueryGroup("name", "n", QueryOperate.CONTAIN)
                .and("type", 1).and("status", "type", QueryOperate.COLUMN_EQUALS)
                .and("password", "p1").and("id", ids, QueryOperate.NOT_IN).or("id", 0, QueryOperate.IN);
        QueryResponse<Person> response = personService.search(group);
        Assert.assertTrue(response.getRecords().get(0).getId() == 0);
        Long count = personService.count(group);
        Assert.assertTrue(count == 1);
    }


    @Test
    @Transactional
    public void testUpdate() {
        // update Person set status = 1, password = 'hello', type = type+1, name= 'helloName' where id = 0 and status = 1
        Integer update = personService.update(
                new UpdateGroup("status", 2).set("password", "hello")
                        .set("type=type+:t", Collections.singletonMap("t", 1)).set("name='helloName'")
                        .where(new QueryGroup("id", 0).and("status", 1)));
        Assert.assertEquals(update.intValue(), 1);
        Long status = personService.count(new QueryGroup("status", 2));
        Assert.assertEquals(status, Long.valueOf(2));
    }


    @Test
    @Transactional
    public void testUpdate2() {
        // update Person set password = 'hello' where id = 0
        Integer update = personService.update(new UpdateGroup("password", "hello").where("id", 0));
        Assert.assertEquals(update.intValue(), 1);
        Long status = personService.count(new QueryGroup("id", 0).and("password", "hello"));
        Assert.assertEquals(status, Long.valueOf(1));
    }

    @Test
    @Transactional
    public void testUpdateAll() {
        // update Person set type = type + 1
        Integer update = personService.update(new UpdateGroup("type = type+1"));
        Assert.assertEquals(update.intValue(), 2);
        Long status = personService.count(new QueryGroup("type", 1));
        Assert.assertEquals(status, Long.valueOf(0));
    }

    @Test
    @Transactional
    public void testUpdateNull() {
        // update Person set password = 'hello' where id is not null
        Integer update = personService.update(new UpdateGroup("type = type+1").where("id", QueryOperate.IS_NOT_NULL));
        Assert.assertEquals(update.intValue(), 2);
        Long status = personService.count(new QueryGroup("type", 1));
        Assert.assertEquals(status, Long.valueOf(0));
    }

    @Test
    @Transactional
    public void testUpdateNotEquals() {
        // update Person set type=type+1 where id <> 0
        Integer update = personService.update(new UpdateGroup("type = type+1").where("id", 0, QueryOperate.NO_EQUALS));
        Assert.assertEquals(update.intValue(), 1);
        Long status = personService.count(new QueryGroup("type", 1));
        Assert.assertEquals(status, Long.valueOf(1));
    }


}
