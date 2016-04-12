package com.qeeka.test.parse;

import com.qeeka.domain.QueryGroup;
import com.qeeka.domain.QueryParser;
import com.qeeka.operate.Direction;
import com.qeeka.operate.Sort;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Neal on 8/9 0009.
 */
public class SortTest {
    private QueryParser queryParser = new QueryParser();

    @Test
    public void sortASC() {
        QueryGroup group = new QueryGroup().sort(new Sort("a", "b", "c"));
        String orderStatement = queryParser.parse(group).getOrderStatement();
        Assert.assertEquals(orderStatement, "a ASC,b ASC,c ASC");
    }

    @Test
    public void sortDESC() {
        QueryGroup group = new QueryGroup().sort(Direction.DESC, "a", "b", "c");
        String orderStatement = queryParser.parse(group).getOrderStatement();
        Assert.assertEquals(orderStatement, "a DESC,b DESC,c DESC");
    }

    @Test
    public void sortMulti() {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Direction.ASC, "a"));
        orders.add(new Sort.Order(Direction.DESC, "b"));
        Assert.assertEquals(queryParser.parse(new QueryGroup().sort(new Sort(orders))).getOrderStatement(),
                "a ASC,b DESC");
    }

    @Test
    public void sortNull() {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Direction.ASC, "a"));
        orders.add(new Sort.Order(Direction.DESC, "b"));
        orders.add(new Sort.Order(Direction.ASC_NULL, "c"));
        orders.add(new Sort.Order(Direction.DESC_NULL, "d"));
        Assert.assertEquals(queryParser.parse(new QueryGroup().sort(orders)).getOrderStatement(),
                "a ASC,b DESC,ISNULL(c) ASC,ISNULL(d) DESC");
    }

    @Test
    public void sortField() {
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Direction.ASC, "id"));
        orders.add(new Sort.Order(Direction.FIELD, "userId,1,2,3,4"));
        orders.add(new Sort.Order(Direction.DESC, "updateTime"));
        Assert.assertEquals(queryParser.parse(new QueryGroup().sort(orders)).getOrderStatement(),
                "id ASC,FIELD(userId,1,2,3,4),updateTime DESC");
        Assert.assertEquals(queryParser.parse(new QueryGroup().sort(Direction.FIELD, "id,1,2,3,4")).getOrderStatement(),
                "FIELD(id,1,2,3,4)");
    }

}
