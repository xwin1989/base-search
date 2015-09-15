package com.qeeka.test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.qeeka.domain.QueryGroup;
import com.qeeka.http.QueryRequest;
import com.qeeka.http.QueryResponse;
import com.qeeka.operate.QueryOperate;
import com.qeeka.test.domain.Person;
import com.qeeka.test.service.PersonService;
import com.qeeka.util.QueryJSONBinder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Neal on 2015/7/27.
 */
public class PersonTest extends SpringTestWithDB {


    @Autowired
    private PersonService personService;

    @Test
    @DatabaseSetup("/PersonData.xml")
    @Transactional
    public void testRemove() {
        personService.remove(1);
    }

    @Test
    @DatabaseSetup("/PersonData.xml")
    public void testSearch() {
        List<Integer> ids = Arrays.asList(2, 3, 4, 5);
        QueryGroup group = new QueryGroup("name", "%n%", QueryOperate.LIKE)
                .and("type", 1).and("status", "type", QueryOperate.COLUMN_EQUALS)
                .and("password", "p1").and("id", ids, QueryOperate.NOT_IN).or("id", 0, QueryOperate.IN);
        String json = QueryJSONBinder.toJSON(new QueryRequest(group));

        QueryResponse<Person> response = personService.search(QueryJSONBinder.fromJSON(json));
        Assert.assertTrue(response.getRecords().get(0).getId() == 0);
    }

}
