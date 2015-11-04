package com.qeeka.test.parse;

import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryModel;
import com.qeeka.domain.QueryParser;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.qeeka.operate.QueryOperate.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Neal on 8/6 0006.
 */
public class OperatorTest {

    QueryParser parser = new QueryParser();

    @Test
    public void testEquals() {
        QueryGroup group = new QueryGroup("a", "test1");
        assertEquals(parser.parse(group).getStatement(), "a = :a0");
        group = new QueryGroup("a", "test2", EQUALS);
        assertEquals(parser.parse(group).getStatement(), "a = :a0");
    }

    @Test
    public void testNoEquals() {
        QueryGroup group = new QueryGroup("a", "test1", NO_EQUALS);
        assertEquals(parser.parse(group).getStatement(), "a <> :a0");
    }

    @Test
    public void testIsNull() {
        QueryGroup group = new QueryGroup("a", IS_NULL);
        assertEquals(parser.parse(group).getStatement(), "a IS NULL");

        group = new QueryGroup("a", "test1").or("b", IS_NULL);
        assertEquals(parser.parse(group).getStatement(), "(a = :a0 OR b IS NULL)");
    }

    @Test
    public void testNotNull() {
        QueryGroup group = new QueryGroup("a", IS_NOT_NULL);
        assertEquals(parser.parse(group).getStatement(), "a IS NOT NULL");

        group = new QueryGroup("a", "test1").or("b", IS_NOT_NULL);
        assertEquals(parser.parse(group).getStatement(), "(a = :a0 OR b IS NOT NULL)");
    }

    @Test
    public void testColumnCompare() {
        QueryGroup group = new QueryGroup("a", "b", COLUMN_EQUALS);
        assertEquals(parser.parse(group).getStatement(), "a = b");

        group = new QueryGroup("a", "b", COLUMN_NO_EQUALS);
        assertEquals(parser.parse(group).getStatement(), "a <> b");
    }

    @Test
    public void testLike() {
        QueryGroup group = new QueryGroup("a", "%s%", LIKE);
        QueryModel parse = parser.parse(group);
        assertEquals(parse.getStatement(), "a LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%s%");
    }

    @Test
    public void testNoLike() {
        QueryGroup group = new QueryGroup("a", "%s%", NOT_LIKE);
        QueryModel parse = parser.parse(group);
        assertEquals(parse.getStatement(), "a NOT LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%s%");
    }

    @Test
    public void testContain() {
        QueryGroup group = new QueryGroup("a", "s", CONTAIN);
        QueryModel parse = parser.parse(group);
        assertEquals(parse.getStatement(), "a LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%s%");

    }

    @Test
    public void testNotContain() {
        QueryGroup group = new QueryGroup("a", "s", NOT_CONTAIN);
        QueryModel parse = parser.parse(group);
        assertEquals(parse.getStatement(), "a NOT LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%s%");
    }

    @Test
    public void testLess() {
        QueryGroup group = new QueryGroup("a", 1, LESS_THAN).and("b", 2, LESS_THAN_EQUALS);
        assertEquals(parser.parse(group).getStatement(), "(a < :a0 AND b <= :b1)");
    }

    @Test
    public void testGreat() {
        QueryGroup group = new QueryGroup("a", 1, GREAT_THAN).and("b", 2, GREAT_THAN_EQUALS);
        assertEquals(parser.parse(group).getStatement(), "(a > :a0 AND b >= :b1)");
    }

    @Test
    public void testIn() {
        List<Integer> idList = Arrays.asList(1, 2, 3, 4, 5);
        QueryGroup group = new QueryGroup("a", idList, IN);
        assertEquals(parser.parse(group).getStatement(), "a IN (:a0)");
    }

    @Test
    public void testNoIn() {
        List<Integer> idList = Arrays.asList(1, 2, 3, 4, 5);
        QueryGroup group = new QueryGroup("a", idList, NOT_IN);
        assertEquals(parser.parse(group).getStatement(), "a NOT IN (:a0)");
        assertTrue(parser.parse(group).getParameters().size() == 1);
    }

    @Test
    public void testNullValue() {
        QueryGroup group = new QueryGroup("a", 1, GREAT_THAN).and("b", 2, GREAT_THAN_EQUALS).and("c", null, EQUALS).and("d", null, LIKE);
        assertEquals(parser.parse(group).getStatement(), "(a > :a0 AND b >= :b1)");
    }

    @Test
    public void testNullValue2() {
        QueryGroup group = new QueryGroup("a", null, GREAT_THAN).and("b", null, GREAT_THAN_EQUALS).and("c", null, EQUALS).and("d", null, LIKE);
        assertEquals(parser.parse(group).getStatement(), null);
    }

    @Test
    public void testCrossJoin() {
        QueryGroup group = new QueryGroup("E.a", 1).or("E.b", 2).crossJoin("OtherEntity", "O").on("O.id", "E.id");
        assertEquals(parser.parse(group).getStatement(), "((E.a = :E_a0 OR E.b = :E_b1) AND O.id = E.id)");
    }


    @Test(expected = IllegalArgumentException.class)
    public void testCrossJoinAndInner() {
        QueryGroup group = new QueryGroup("E.a", 1).or("E.b", 2).join("OtherEntity2", "O2").crossJoin("OtherEntity", "O");
        assertEquals(parser.parse(group).getStatement(), "(E.a = :E_a0 OR E.b = :E_b1)");
    }

    @Test
    public void testInnerJoin() {
        QueryGroup group = new QueryGroup("E.a", 1).or("E.b", 2).join("OtherEntity", "O");
        assertEquals(parser.parse(group).getStatement(), "(E.a = :E_a0 OR E.b = :E_b1)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInnerJoinCross() {
        QueryGroup group = new QueryGroup("E.a", 1).join("OtherEntity", "O").on("O.id", "E.id").crossJoin("CrossEntity", "CE");
        assertEquals(parser.parse(group).getStatement(), "(E.a = :E_a0 OR E.b = :E_b1)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLeftJoin() {
        QueryGroup group = new QueryGroup("E.a", 1).or("E.b", 2).leftJoin("OtherEntity", "O").on("O.id", "E.id");
        assertEquals(parser.parse(group).getStatement(), "(E.a = :E_a0 OR E.b = :E_b1)");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLeftJoinCrossJoin() {
        QueryGroup group = new QueryGroup("E.a", 1).or("E.b", 2).leftJoin("OtherEntity", "O").on("O.id", "E.id").crossJoin("CrossEntity", "CE");
        assertEquals(parser.parse(group).getStatement(), "(E.a = :E_a0 OR E.b = :E_b1)");
    }
}
