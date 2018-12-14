package com.qeeka.test;

import com.qeeka.test.domain.Company;
import com.qeeka.test.domain.CompanyInfo;
import com.qeeka.test.service.CompanyService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Neal on 16/4/8.
 */
public class CompanyTest extends SpringTestWithDB {
    private CompanyService companyService;

    @Before
    @Transactional
    public void init() {
        companyService.updateNative("create table company(id int,name varchar(50),status int,type int)");
        companyService.updateNative("insert into company values(0,'company1',1,2)");
        companyService.updateNative("insert into company values(1,'company2',2,1)");
        companyService.updateNative("insert into company values(2,'company3',3,2)");

        companyService.updateNative("create table company_info(id int,title varchar(50),status int,first_name varchar(10),lastName varchar(10))");
        companyService.updateNative("insert into company_info values(1,'info1',1,'neal','xu')");
        companyService.updateNative("insert into company_info values(2,'info2',2,'neal','xu')");
        companyService.updateNative("insert into company_info values(3,'info3',3,'neal','xu')");
    }

    @After
    @Transactional
    public void after() {
        companyService.updateNative("drop table company");
        companyService.updateNative("drop table company_info");
    }

    @Test
    @Transactional
    public void testSimple() {
        List<CompanyInfo> companyInfoList = companyService.query("select * from company_info order by id asc", CompanyInfo.class);
        List<Company> companyList = companyService.query("select * from company");
        Assert.assertEquals(companyInfoList.size(), 3);
        Assert.assertEquals(companyList.size(), 3);


        CompanyInfo companyInfo1 = companyInfoList.get(0);
        Assert.assertEquals(companyInfo1.getStatus(), new Integer(1));
        Assert.assertEquals(companyInfo1.getId(), new Integer(1));
        Map<String, Object> params = new HashMap<>();
        params.put("id", companyInfo1.getId());
        params.put("status", 4);
        companyService.updateNative("update company_info set status = :status where id = :id", params);

        CompanyInfo companyInfo = companyService.queryUnique("select id,status from company_info where id = 1", CompanyInfo.class);
        Assert.assertNotNull(companyInfo);
        Assert.assertEquals(companyInfo.getStatus(), new Integer(4));

        int i = companyService.updateNative("delete from company where id > 1");
        Assert.assertEquals(i, 1);


        companyInfoList = companyService.query("select * from company order by id asc", CompanyInfo.class);
        Assert.assertEquals(companyInfoList.size(), 2);

    }

    @Test
    public void testQuery() {
        CompanyInfo company = companyService.queryUnique("select id,first_name as firstColumn ,lastName from company_info where id = :id", Collections.<String, Object>singletonMap("id", 1), CompanyInfo.class);
        Assert.assertEquals(company.getFirstColumn(), "neal");
        Assert.assertNull(company.getFirstName());
        Assert.assertEquals(company.getLastName(), "xu");
        Assert.assertNotNull(companyService.queryUnique("select * from company where id = 1"));
    }

    @Test
    public void testList() {
        List<Integer> objects = companyService.queryList("select id from company", Integer.class);
        Assert.assertEquals(objects.size(), 3);

        List<Integer> idList = companyService.queryList("select id from company where id >= :id", Collections.<String, Object>singletonMap("id", 1), Integer.class);
        Assert.assertEquals(idList.size(), 2);

        List<String> list2 = companyService.queryList("select id from company where id = 2", String.class);
        Assert.assertEquals(list2.get(0), "2");

    }

    @Test
    @Transactional
    public void testObject() {
        String name = companyService.queryForObject("select name from company where id = 1", String.class);
        Assert.assertEquals(name, "company2");

        Integer count = companyService.queryForObject("select count(1) from company where id >= :id", Collections.<String, Object>singletonMap("id", 0), Integer.class);
        Assert.assertEquals(count, new Integer(3));
    }

    @Test
    @Transactional
    public void testMap() {
        Map<String, Object> queryForMap = companyService.queryForMap("select * from company where id = 1");
        Assert.assertEquals(queryForMap.size(), 4);

        queryForMap = companyService.queryForMap("select * from company where id = :id", Collections.<String, Object>singletonMap("id", 1));
        Assert.assertEquals(queryForMap.size(), 4);

        List<Company> companies = companyService.queryWithRowMap("select * from company where id=:id", Collections.<String, Object>singletonMap("id", 1));
        Assert.assertEquals(companies.size(), 1);
        Assert.assertEquals(companies.get(0).getName(), "company2");

        companies = companyService.queryWithRowMap("select * from company");
        Assert.assertEquals(companies.size(), 3);

    }

    @Autowired
    public void setCompanyService(CompanyService companyService) {
        this.companyService = companyService;
    }
}
