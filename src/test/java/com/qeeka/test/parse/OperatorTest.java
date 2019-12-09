package com.qeeka.test.parse;

import com.qeeka.domain.QueryModel;
import com.qeeka.query.Criteria;
import com.qeeka.util.CriteriaParserHandle;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Neal on 8/6 0006.
 */
public class OperatorTest {

    @Test
    public void testEquals() {
        Criteria criteria = Criteria.where("a").eq("test1");
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "a = :a0");
    }


    @Test
    public void testNoEquals() {
        Criteria criteria = Criteria.where("a").ne("test1");
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "a <> :a0");
    }

    @Test
    public void testIsNull() {
        Criteria criteria = Criteria.where("a").nul();
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "a IS NULL");

        criteria = Criteria.where("a").eq("test1").or("b").nul();
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "(a = :a0 OR b IS NULL)");
    }

    @Test
    public void testNotNull() {
        Criteria criteria = Criteria.where("a").nNul();
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "a IS NOT NULL");

        criteria = Criteria.where("a").eq("test1").or("b").nNul();
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "(a = :a0 OR b IS NOT NULL)");
    }

    @Test
    public void testColumnCompare() {
        Criteria criteria = Criteria.where("a").eq("b", false);
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "a = b");

        criteria = Criteria.where("a").ne("b", false);
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "a <> b");
    }

    @Test
    public void testLike() {
        Criteria criteria = Criteria.where("a").like(String.format("%%%s%%", "test"));
        QueryModel parse = CriteriaParserHandle.parse(criteria);
        assertEquals(parse.getConditionStatement(), "a LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%test%");
    }

    @Test
    public void testNoLike() {
        Criteria criteria = Criteria.where("a").nLike(String.format("%%%s%%", "test"));
        QueryModel parse = CriteriaParserHandle.parse(criteria);
        assertEquals(parse.getConditionStatement(), "a NOT LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%test%");
    }

    @Test
    public void testLess() {
        Criteria criteria = Criteria.where("a").lt(1).and("b").lte(2);
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "(a < :a0 AND b <= :b1)");
    }

    @Test
    public void testGreat() {
        Criteria criteria = Criteria.where("a").gt(1).and("b").gte(2);
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "(a > :a0 AND b >= :b1)");
    }

    @Test
    public void testIn() {
        List<Integer> idList = Arrays.asList(1, 2, 3, 4, 5);
        Criteria criteria = Criteria.where("a").in(idList);
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "a IN (:a0)");
    }

    @Test
    public void testNoIn() {
        List<Integer> idList = Arrays.asList(1, 2, 3, 4, 5);
        Criteria criteria = Criteria.where("a").nin(idList);
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "a NOT IN (:a0)");
        assertTrue(CriteriaParserHandle.parse(criteria).getParameters().size() == 1);
    }

    @Test
    public void testLoose() {
        Criteria criteria = Criteria.loose("a").gt(1).and("b").gte(2).and("c").eq(null).and("d").nul().and("e").like(null);
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "((a > :a0 AND b >= :b1) AND d IS NULL)");
    }

    @Test
    public void testInnerCriteria() {
        Criteria criteria = Criteria.where("a").eq(1).and(Criteria.where("b").eq(2).or("c").eq(3).or("d").gt(6)).or("c").gt(4);
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "((a = :a3 AND ((b = :b0 OR c = :c1) OR d > :d2)) OR c > :c4)");
    }

    @Test
    public void testMultiOperator() {
        Criteria criteria = Criteria.where("a").eq(1).gt(2).like(3).and("b").eq(2).lt(3).gt(4);
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "(((((a = :a0 AND a > :a1) AND a LIKE :a2) AND b = :b3) AND b < :b4) AND b > :b5)");
    }

    @Test
    public void testSubQuery() {
        Criteria criteria = Criteria.where("a").eq("12").sub("EXISTS (select id from B)");
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "(a = :a0 AND EXISTS (select id from B))");
        criteria = Criteria.where(null).sub("EXISTS (select * from b where id = :id)", Collections.singletonMap("id", 1));
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "EXISTS (select * from b where id = :id)");
        criteria = Criteria.where(null).sub("id in (select id from b where id = :id)", Collections.singletonMap("id", 1));
        assertEquals(CriteriaParserHandle.parse(criteria).getConditionStatement(), "id in (select id from b where id = :id)");
    }

}
