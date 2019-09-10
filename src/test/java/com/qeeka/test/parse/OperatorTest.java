package com.qeeka.test.parse;

import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryModel;
import com.qeeka.enums.Direction;
import com.qeeka.enums.QueryOperate;
import com.qeeka.util.QueryParserHandle;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.qeeka.enums.QueryOperate.COLUMN_EQUALS;
import static com.qeeka.enums.QueryOperate.COLUMN_NO_EQUALS;
import static com.qeeka.enums.QueryOperate.CONTAIN;
import static com.qeeka.enums.QueryOperate.EQUALS;
import static com.qeeka.enums.QueryOperate.GREAT_THAN;
import static com.qeeka.enums.QueryOperate.GREAT_THAN_EQUALS;
import static com.qeeka.enums.QueryOperate.IN;
import static com.qeeka.enums.QueryOperate.IS_NOT_NULL;
import static com.qeeka.enums.QueryOperate.IS_NULL;
import static com.qeeka.enums.QueryOperate.LESS_THAN;
import static com.qeeka.enums.QueryOperate.LESS_THAN_EQUALS;
import static com.qeeka.enums.QueryOperate.LIKE;
import static com.qeeka.enums.QueryOperate.NOT_CONTAIN;
import static com.qeeka.enums.QueryOperate.NOT_IN;
import static com.qeeka.enums.QueryOperate.NOT_LIKE;
import static com.qeeka.enums.QueryOperate.NO_EQUALS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Neal on 8/6 0006.
 */
public class OperatorTest {

    @Test
    public void testEquals() {
        QueryGroup group = new QueryGroup("a", "test1");
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "a = :a0");
        group = new QueryGroup("a", "test2", EQUALS);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "a = :a0");
    }

    @Test
    public void testNoEquals() {
        QueryGroup group = new QueryGroup("a", "test1", NO_EQUALS);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "a <> :a0");
    }

    @Test
    public void testIsNull() {
        QueryGroup group = new QueryGroup("a", IS_NULL);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "a IS NULL");

        group = new QueryGroup("a", "test1").or("b", IS_NULL);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(a = :a0 OR b IS NULL)");
    }

    @Test
    public void testNotNull() {
        QueryGroup group = new QueryGroup("a", IS_NOT_NULL);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "a IS NOT NULL");

        group = new QueryGroup("a", "test1").or("b", IS_NOT_NULL);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(a = :a0 OR b IS NOT NULL)");
    }

    @Test
    public void testColumnCompare() {
        QueryGroup group = new QueryGroup("a", "b", COLUMN_EQUALS);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "a = b");

        group = new QueryGroup("a", "b", COLUMN_NO_EQUALS);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "a <> b");
    }

    @Test
    public void testLike() {
        QueryGroup group = new QueryGroup("a", "%s%", LIKE);
        QueryModel parse = QueryParserHandle.parse(group);
        assertEquals(parse.getConditionStatement(), "a LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%s%");
    }

    @Test
    public void testNoLike() {
        QueryGroup group = new QueryGroup("a", "%s%", NOT_LIKE);
        QueryModel parse = QueryParserHandle.parse(group);
        assertEquals(parse.getConditionStatement(), "a NOT LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%s%");
    }

    @Test
    public void testContain() {
        QueryGroup group = new QueryGroup("a", "s", CONTAIN);
        QueryModel parse = QueryParserHandle.parse(group);
        assertEquals(parse.getConditionStatement(), "a LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%s%");

    }

    @Test
    public void testNotContain() {
        QueryGroup group = new QueryGroup("a", "s", NOT_CONTAIN);
        QueryModel parse = QueryParserHandle.parse(group);
        assertEquals(parse.getConditionStatement(), "a NOT LIKE :a0");
        assertEquals(parse.getParameters().get("a0"), "%s%");
    }

    @Test
    public void testLess() {
        QueryGroup group = new QueryGroup("a", 1, LESS_THAN).and("b", 2, LESS_THAN_EQUALS);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(a < :a0 AND b <= :b1)");
    }

    @Test
    public void testGreat() {
        QueryGroup group = new QueryGroup("a", 1, GREAT_THAN).and("b", 2, GREAT_THAN_EQUALS);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(a > :a0 AND b >= :b1)");
    }

    @Test
    public void testIn() {
        List<Integer> idList = Arrays.asList(1, 2, 3, 4, 5);
        QueryGroup group = new QueryGroup("a", idList, IN);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "a IN (:a0)");
    }

    @Test
    public void testNoIn() {
        List<Integer> idList = Arrays.asList(1, 2, 3, 4, 5);
        QueryGroup group = new QueryGroup("a", idList, NOT_IN);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "a NOT IN (:a0)");
        assertTrue(QueryParserHandle.parse(group).getParameters().size() == 1);
    }

    @Test
    public void testNullValue() {
        QueryGroup group = QueryGroup.looseGroup("a", 1, GREAT_THAN).and("b", 2, GREAT_THAN_EQUALS).and("c", null, EQUALS).and("d", null, LIKE);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(a > :a0 AND b >= :b1)");
    }

    @Test
    public void testNullValue2() {
        QueryGroup group = QueryGroup.looseGroup("a", null, GREAT_THAN).and("b", null, GREAT_THAN_EQUALS).and("c", null, EQUALS).and("d", null, LIKE);
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), null);
    }

    @Test
    public void testCrossJoin() {
        QueryGroup group = new QueryGroup("E.a", 1).or("E.b", 2).crossJoin("OtherEntity", "O").on("O.id", "E.id");
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(E.a = :E_a0 OR E.b = :E_b1)");
    }

    @Test
    public void testInnerJoin() {
        QueryGroup group = new QueryGroup("E.a", 1).or("E.b", 2).join("OtherEntity", "O");
        assertEquals(QueryParserHandle.parse(group).getConditionStatement(), "(E.a = :E_a0 OR E.b = :E_b1)");
    }

    @Test
    public void testGroupGroup() {
        Integer status = null;
        String title = null;
        QueryGroup queryGroup = QueryGroup.looseGroup()
                .join("IconChannelVersionMappingEntity", "IE")
                .and("E.status", status)
                .and(new QueryGroup("E.platform", 3).or("E.platform", "all"))
                .and(new QueryGroup("IE.channelCode", 4).or("IE.channelCode", "all"))
                .and(new QueryGroup("IE.appVersion", 5).or("IE.appVersion", "all"));
        queryGroup.and("E.title", "%" + title + "%", QueryOperate.LIKE);
        queryGroup.sort(Direction.ASC, "E.sequenceNumber");
        Assert.assertEquals(QueryParserHandle.parse(queryGroup).getConditionStatement(), "((((E.platform = :E_platform0 OR E.platform = :E_platform1) AND (IE.channelCode = :IE_channelCode2 OR IE.channelCode = :IE_channelCode3)) AND (IE.appVersion = :IE_appVersion4 OR IE.appVersion = :IE_appVersion5)) AND E.title LIKE :E_title6)");
    }


    @Test
    public void testGroupGroup2() {
        Integer status = null;
        String title = null;
        QueryGroup queryGroup = QueryGroup.looseGroup()
                .join("IconChannelVersionMappingEntity", "IE")
                .and("E.status", status)
                .and(QueryGroup.looseGroup("E.platform", status).or("E.platform", status))
                .and(QueryGroup.looseGroup("IE.channelCode", status).or("IE.channelCode", status))
                .and(QueryGroup.looseGroup("IE.appVersion", status).or("IE.appVersion", status));
        queryGroup.and("E.title", title, QueryOperate.LIKE);
        queryGroup.sort(Direction.ASC, "E.sequenceNumber");
        Assert.assertNull(QueryParserHandle.parse(queryGroup).getConditionStatement());
    }
}
