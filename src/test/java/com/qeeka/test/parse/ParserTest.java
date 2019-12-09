package com.qeeka.test.parse;

import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryModel;
import com.qeeka.enums.QueryOperate;
import com.qeeka.util.QueryParserHandle;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neal.xu on 7/31 0031.
 */
public class ParserTest {

//    @Test
//    public void sampleTest() {
//        QueryGroup group = new QueryGroup("a", 1).and("b", 2).or("c", 3);
//        Assert.assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "((a = :a0 AND b = :b1) OR c = :c2)");
//    }
//
//    @Test
//    public void testSimpleEquals() {
//        QueryGroup group = new QueryGroup("a", 1).and("b", 2).and("c", 3);
//        Assert.assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "((a = :a0 AND b = :b1) AND c = :c2)");
//    }
//
//    @Test
//    public void testLike() {
//        QueryGroup group = new QueryGroup("a", 1, QueryOperate.LIKE).and("b", 2, QueryOperate.NO_EQUALS);
//        Assert.assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(a LIKE :a0 AND b <> :b1)");
//    }
//
//    @Test
//    public void tet2() {
//        QueryGroup group = new QueryGroup("a", 4).and(
//                new QueryGroup("b", 3).and("c", 1).and("d", 2)
//        );
//        Assert.assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(a = :a3 AND ((b = :b0 AND c = :c1) AND d = :d2))");
//    }
//
//    @Test
//    public void test3() {
//        QueryGroup group = new QueryGroup("c", 5).or(
//                new QueryGroup("a", 3).and("b", 4).or("f", 9)
//        );
//        Assert.assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(c = :c3 OR ((a = :a0 AND b = :b1) OR f = :f2))");
//    }
//
//    @Test
//    public void test4() {
//        QueryGroup group = new QueryGroup(
//                new QueryGroup("a", 3).and("b", 4)
//        ).or(
//                new QueryGroup("c", 3).or("d", 5)
//        );
//        Assert.assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "((a = :a0 AND b = :b1) OR (c = :c2 OR d = :d3))");
//    }
//
//    @Test
//    public void testSimpleColumnParameters() {
//        QueryGroup group = new QueryGroup("a", 30).and("b", 10).or("a", 20);
//        QueryModel queryModel = QueryParserHandle.parse(group);
//        Assert.assertEquals(queryModel.getConditionStatement(), "((a = :a0 AND b = :b1) OR a = :a2)");
//        Assert.assertTrue(queryModel.getParameters().size() == 3);
//        Assert.assertEquals(queryModel.getParameters().get("a0"), 30);
//        Assert.assertEquals(queryModel.getParameters().get("b1"), 10);
//        Assert.assertEquals(queryModel.getParameters().get("a2"), 20);
//    }
//
//    @Test
//    public void testJsonTransaction() {
//        QueryGroup group = new QueryGroup(
//                new QueryGroup("a", 3).and("b", 4)
//        ).or(
//                new QueryGroup("c", 3).or("d", 5)
//        );
//
//        Assert.assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "((a = :a0 AND b = :b1) OR (c = :c2 OR d = :d3))");
//    }
//
//    @Test
//    public void parametersTransaction() {
//        List<Integer> ids = Arrays.asList(1, 2, 3, 4);
//        QueryGroup group = new QueryGroup("id", ids, QueryOperate.IN).and("id", ids, QueryOperate.NOT_IN)
//                .and("name", "hello").or(new QueryGroup("key", 1));
//        Assert.assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(((id IN (:id0) AND id NOT IN (:id1)) AND name = :name2) OR key = :key3)");
//    }
//
//    @Test
//    public void testSubQuery() {
//        QueryGroup group = new QueryGroup("E.id in (select * from a)", QueryOperate.SUB_QUERY).and("name", "hello", QueryOperate.LIKE);
//        QueryModel queryModel = QueryParserHandle.parse(group);
//        Assert.assertEquals(queryModel.getConditionStatement(), "(E.id in (select * from a)  AND name LIKE :name0)");
//        Assert.assertTrue(queryModel.getParameters().size() == 1);
//    }
//
//    @Test
//    public void testSubQuery2() {
//        QueryGroup group = new QueryGroup("exist (select * from a)", QueryOperate.SUB_QUERY).and("name", "hello");
//        QueryModel queryModel = QueryParserHandle.parse(group);
//        Assert.assertEquals(queryModel.getConditionStatement(), "(exist (select * from a)  AND name = :name0)");
//        Assert.assertTrue(queryModel.getParameters().size() == 1);
//    }
//
//    @Test
//    public void testSubQuery3() {
//        Map<String, Object> subParams = new HashMap<>();
//        subParams.put("subId", 10);
//        subParams.put("subName", "neal");
//        subParams.put("status", 2);
//
//        QueryGroup group = new QueryGroup("myId", 1).and("exist (select * from EE a where a.id = :subId and a.subName = :subName and a.status = :subStatus)", subParams, QueryOperate.SUB_QUERY)
//                .and("status", 1);
//        QueryModel queryModel = QueryParserHandle.parse(group);
//        Assert.assertEquals(queryModel.getConditionStatement(), "((myId = :myId0 AND exist (select * from EE a where a.id = :subId and a.subName = :subName and a.status = :subStatus) ) AND status = :status4)");
//    }
//
//    @Test
//    public void testSubQueryOr() {
//        QueryGroup group = new QueryGroup("name", "a").or("exist (select * from a)", QueryOperate.SUB_QUERY);
//        QueryModel queryModel = QueryParserHandle.parse(group);
//        Assert.assertEquals(queryModel.getConditionStatement(), "(name = :name0 OR exist (select * from a) )");
//    }
//
//    @Test
//    public void testSubQueryAnd() {
//        QueryGroup group = new QueryGroup("name", "a").and("exist (select * from a)", QueryOperate.SUB_QUERY);
//        QueryModel queryModel = QueryParserHandle.parse(group);
//        Assert.assertEquals(queryModel.getConditionStatement(), "(name = :name0 AND exist (select * from a) )");
//    }
//
//    @Test
//    public void testAndQuery() {
//        QueryGroup group = new QueryGroup();
//        group.and("status", "1");
//        QueryModel queryModel = QueryParserHandle.parse(group);
//        Assert.assertEquals(queryModel.getConditionStatement(), "status = :status0");
//    }
//
//    @Test
//    public void testOrQuery() {
//        QueryGroup group = QueryGroup.looseGroup("status", "1")
//                .and("a", 1, QueryOperate.EQUALS)
//                .or("b", "b");
//        QueryModel queryModel = QueryParserHandle.parse(group);
//        Assert.assertEquals(queryModel.getConditionStatement(), "((status = :status0 AND a = :a1) OR b = :b2)");
//    }
//
//    @Test
//    public void testLooseQuery() {
//        QueryGroup group = QueryGroup.looseGroup("status", "1")
//                .and("a", null, QueryOperate.EQUALS)
//                .or("b", "b");
//        QueryModel queryModel = QueryParserHandle.parse(group);
//        Assert.assertEquals(queryModel.getConditionStatement(), "(status = :status0 OR b = :b1)");
//    }
//
//    @Test(expected = IllegalArgumentException.class)
//    public void testStrictGroup() {
//        new QueryGroup("a", null, QueryOperate.EQUALS);
//    }


}
