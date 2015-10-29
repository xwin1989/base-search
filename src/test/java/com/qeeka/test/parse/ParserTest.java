package com.qeeka.test.parse;

import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryModel;
import com.qeeka.domain.QueryNode;
import com.qeeka.domain.QueryParser;
import com.qeeka.operate.QueryOperate;
import com.qeeka.util.QueryJSONBinder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by neal.xu on 7/31 0031.
 */
public class ParserTest {
    QueryParser parser;

    @Before
    public void init() {
        parser = new QueryParser();
    }

    @Test
    public void sampleTest() {
        QueryGroup group = new QueryGroup("a", 1).and("b", 2).or("c", 3);
        Assert.assertEquals(parser.parse(group).getStatement(), "((a = :a0 AND b = :b1) OR c = :c2)");
    }

    @Test
    public void testSimpleEquals() {
        QueryGroup group = new QueryGroup(new QueryNode("a", 1)).and(new QueryNode("b", 2)).and("c", 3);
        Assert.assertEquals(parser.parse(group).getStatement(), "((a = :a0 AND b = :b1) AND c = :c2)");
    }

    @Test
    public void testLike() {
        QueryGroup group = new QueryGroup(new QueryNode("a", 1, QueryOperate.LIKE)).and(new QueryNode("b", 2, QueryOperate.NO_EQUALS));
        Assert.assertEquals(parser.parse(group).getStatement(), "(a LIKE :a0 AND b <> :b1)");
    }

    @Test
    public void tet2() {
        QueryGroup group = new QueryGroup(new QueryNode("d", 4)).and(
                new QueryGroup(new QueryNode("c", 3)).and(new QueryNode("a", 1))
                        .and(new QueryNode("b", 2)));
        Assert.assertEquals(parser.parse(group).getStatement(), "((c = :c0 AND a = :a1) AND (d = :d2 AND b = :b3))");
    }

    @Test
    public void test3() {
        QueryGroup group = new QueryGroup("c", 5).or(
                new QueryGroup("a", 3).and("b", 4).or("f", 9)
        );
        Assert.assertEquals(parser.parse(group).getStatement(), "((a = :a0 AND b = :b1) OR (c = :c2 OR f = :f3))");
    }

    @Test
    public void test4() {
        QueryGroup group = new QueryGroup(
                new QueryGroup("a", 3).and("b", 4)
        ).or(
                new QueryGroup("c", 3).or("d", 5)
        );
        Assert.assertEquals(parser.parse(group).getStatement(), "((a = :a0 AND b = :b1) OR (c = :c2 OR d = :d3))");
    }

    @Test
    public void testSimpleColumnParameters() {
        QueryGroup group = new QueryGroup("a", 30).and("b", 10).or("a", 20);
        QueryModel queryModel = parser.parse(group);
        Assert.assertEquals(queryModel.getStatement(), "((a = :a0 AND b = :b1) OR a = :a2)");
        Assert.assertTrue(queryModel.getParameters().size() == 3);
        Assert.assertEquals(queryModel.getParameters().get("a0"), 30);
        Assert.assertEquals(queryModel.getParameters().get("b1"), 10);
        Assert.assertEquals(queryModel.getParameters().get("a2"), 20);
    }

    @Test
    public void testJsonTransaction() {
        QueryGroup group = new QueryGroup(
                new QueryGroup("a", 3).and("b", 4)
        ).or(
                new QueryGroup("c", 3).or("d", 5)
        );

        String s = QueryJSONBinder.binder(QueryGroup.class).toJSON(group);
        QueryGroup queryGroup = QueryJSONBinder.binder(QueryGroup.class).fromJSON(s);
        Assert.assertEquals(parser.parse(queryGroup).getStatement(), "((a = :a0 AND b = :b1) OR (c = :c2 OR d = :d3))");
    }

    @Test
    public void parametersTransaction() {
        List<Integer> ids = Arrays.asList(1, 2, 3, 4);
        QueryGroup group = new QueryGroup("id", ids, QueryOperate.IN).and("id", ids, QueryOperate.NOT_IN)
                .and("name", "hello").or(new QueryGroup("key", 1));
        String s = QueryJSONBinder.binder(QueryGroup.class).toJSON(group);
        QueryGroup queryGroup = QueryJSONBinder.binder(QueryGroup.class).fromJSON(s);
        Assert.assertEquals(parser.parse(queryGroup).getStatement(), "(((id IN (:id0) AND id NOT IN (:id1)) AND name = :name2) OR key = :key3)");
    }

    @Test
    public void testSubQuery() {
        QueryGroup group = new QueryGroup("id", "in (select * from a)", QueryOperate.SUB_QUERY).and("name", "hello", QueryOperate.CONTAIN);
        QueryModel queryModel = parser.parse(group);
        Assert.assertEquals(queryModel.getStatement(), "(id in (select * from a) AND name LIKE :name0)");
        Assert.assertTrue(queryModel.getParameters().size() == 1);
    }

    @Test
    public void testSubQuery2() {
        QueryGroup group = new QueryGroup("exist (select * from a)", QueryOperate.SUB_QUERY).and("name", "hello");
        QueryModel queryModel = parser.parse(group);
        Assert.assertEquals(queryModel.getStatement(), "( exist (select * from a) AND name = :name0)");
        Assert.assertTrue(queryModel.getParameters().size() == 1);
    }

    @Test
    public void testSubQueryOr() {
        QueryGroup group = new QueryGroup("name", "a").or("exist (select * from a)", QueryOperate.SUB_QUERY);
        QueryModel queryModel = parser.parse(group);
        Assert.assertEquals(queryModel.getStatement(), "(name = :name0 OR  exist (select * from a))");
    }

    @Test
    public void testSubQueryAnd() {
        QueryGroup group = new QueryGroup("name", "a").and("exist (select * from a)", QueryOperate.SUB_QUERY);
        QueryModel queryModel = parser.parse(group);
        Assert.assertEquals(queryModel.getStatement(), "(name = :name0 AND  exist (select * from a))");
    }

    @Test
    public void testAndQuery() {
        QueryGroup group = new QueryGroup();
        group.and("status", "1");
        QueryModel queryModel = parser.parse(group);
        Assert.assertEquals(queryModel.getStatement(), "status = :status0");
    }

    @Test
    public void testOrQuery() {
        QueryGroup group = new QueryGroup();
        group.or("status", "1");
        group.and("a", null, QueryOperate.EQUALS);
        group.or("b", "b");
        QueryModel queryModel = parser.parse(group);
        Assert.assertEquals(queryModel.getStatement(), "(status = :status0 OR b = :b1)");
    }
}
