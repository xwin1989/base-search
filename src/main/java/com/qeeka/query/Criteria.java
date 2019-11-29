package com.qeeka.query;

import com.qeeka.domain.QueryHandle;
import com.qeeka.domain.QueryNode;
import com.qeeka.domain.QueryOperateNode;
import com.qeeka.enums.QueryLinkOperate;
import com.qeeka.enums.QueryOperate;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by neal.xu on 2019/10/30.
 */
public class Criteria {
    private String key;
    private QueryLinkOperate operate = QueryLinkOperate.AND;
    private final List<QueryHandle> criteriaChain = new LinkedList<>();
    private boolean strict = true;

    public Criteria(String key) {
        this.key = key;
    }

    private Criteria(String key, boolean strict) {
        this.key = key;
        this.strict = strict;
    }

    /**
     * Static factory method to create a Criteria using the provided key
     */
    public static Criteria where(String key) {
        return new Criteria(key);
    }

    /**
     * Static factory method to create a loose Criteria using the provided key
     */
    public static Criteria loose(String key) {
        return new Criteria(key, false);
    }

    //--------------------------------- base operator ---------------------------------

    /**
     * equals operator
     */
    public Criteria eq(Object o) {
        return eq(o, true);
    }

    /**
     * no equals operator
     */
    public Criteria ne(Object o) {
        return ne(o, true);
    }

    /**
     * null equals operator
     */
    public Criteria nul() {
        return addNode(QueryOperate.IS_NULL);
    }

    /**
     * not null equals operator
     */
    public Criteria nNul() {
        return addNode(QueryOperate.IS_NOT_NULL);
    }

    /**
     * analysis value operator
     */
    public Criteria eq(Object o, boolean analysis) {
        if (analysis) {
            return addNode(o, QueryOperate.EQUALS);
        } else {
            return addNode(o, QueryOperate.COLUMN_EQUALS);
        }
    }

    /**
     * not equals analysis value operator
     */
    public Criteria ne(Object o, boolean analysis) {
        if (analysis) {
            return addNode(o, QueryOperate.NO_EQUALS);
        } else {
            return addNode(o, QueryOperate.COLUMN_NO_EQUALS);
        }
    }

    /**
     * like operator
     */
    public Criteria like(Object o) {
        return addNode(o, QueryOperate.LIKE);
    }

    /**
     * not contain operator
     */
    public Criteria nLike(Object o) {
        return addNode(o, QueryOperate.NOT_LIKE);
    }

    /**
     * less then operator
     */
    public Criteria lt(Object o) {
        return addNode(o, QueryOperate.LESS_THAN);
    }

    /**
     * less then equals operator
     */
    public Criteria lte(Object o) {
        return addNode(o, QueryOperate.LESS_THAN_EQUALS);
    }

    /**
     * greater than operator
     */
    public Criteria gt(Object o) {
        return addNode(o, QueryOperate.GREAT_THAN);
    }

    /**
     * greater than equals operator
     */
    public Criteria gte(Object o) {
        return addNode(o, QueryOperate.GREAT_THAN_EQUALS);
    }

    /**
     * in operator
     */
    public Criteria in(Collection<?> o) {
        if (o == null || o.isEmpty()) throw new IllegalArgumentException("collect can't empty");
        return addNode(o, QueryOperate.IN);
    }

    /**
     * not in operator
     */
    public Criteria nin(Collection<?> o) {
        if (o == null || o.isEmpty()) throw new IllegalArgumentException("collect can't empty");
        return addNode(o, QueryOperate.NOT_IN);
    }

    /**
     * exists operator
     */
    public Criteria exists(String sql) {
        return sqlSub(sql, null, QueryOperate.EXISTS);
    }

    public Criteria exists(String sql, Map<String, Object> params) {
        return sqlSub(sql, params, QueryOperate.EXISTS);
    }

    /**
     * not exists operator
     */
    public Criteria noExists(String sql) {
        return sqlSub(sql, null, QueryOperate.NO_EXISTS);
    }

    public Criteria noExists(String sql, Map<String, Object> params) {
        return sqlSub(sql, params, QueryOperate.NO_EXISTS);
    }


    //--------------------------------- link operator ---------------------------------

    public Criteria and(String key) {
        this.key = key;
        this.operate = QueryLinkOperate.AND;
        return this;
    }

    public Criteria and(Criteria criteria) {
        return addCriteria(criteria, QueryLinkOperate.AND);
    }


    public Criteria or(String key) {
        this.key = key;
        this.operate = QueryLinkOperate.OR;
        return this;
    }

    public Criteria or(Criteria criteria) {
        return addCriteria(criteria, QueryLinkOperate.OR);
    }

    /**
     * sub query operator
     */
    public Criteria sub(String sql) {
        return sqlSub(sql, null, QueryOperate.SUB_QUERY);
    }

    /**
     * sub query operator
     */
    public Criteria sub(String sql, Map<String, Object> params) {
        return sqlSub(sql, params, QueryOperate.SUB_QUERY);
    }

    /**
     * sub query operator
     */
    private Criteria sqlSub(String sql, Map<String, Object> params, QueryOperate operate) {
        Objects.nonNull(sql);
        Map<String, Object> newParams = new HashMap<>();
        newParams.put(Query.SPECIAL_QUERY_SQL_KEY, sql);
        if (params != null && !params.isEmpty()) newParams.putAll(params);
        return addNode(newParams, operate);
    }

    /**
     * add node to criteriaChain
     */
    private Criteria addNode(QueryOperate queryOperate) {
        return addNode(this.key, null, queryOperate);
    }

    private Criteria addNode(Object value, QueryOperate queryOperate) {
        if (value == null) {
            if (this.strict) throw new IllegalArgumentException("column #" + this.key + " value can't null");
            return this;
        }
        return addNode(this.key, value, queryOperate);
    }

    private Criteria addNode(String key, Object value, QueryOperate queryOperate) {
        criteriaChain.add(new QueryNode(key, value, queryOperate));
        if (criteriaChain.size() > 1)
            criteriaChain.add(new QueryOperateNode(this.operate));
        return this;
    }

    private Criteria addCriteria(Criteria criteria, QueryLinkOperate linkOperate) {
        if (criteria == null) {
            throw new IllegalArgumentException("criteria can't null!");
        }
        for (QueryHandle handle : criteria.getCriteriaChain()) {
            this.criteriaChain.add(handle);
        }
        if (!criteria.getCriteriaChain().isEmpty() && !criteria.getCriteriaChain().isEmpty()) {
            this.criteriaChain.add(new QueryOperateNode(linkOperate));
        }
        return this;
    }

    public List<QueryHandle> getCriteriaChain() {
        return criteriaChain;
    }

    //--------------------------------- lambda ---------------------------------


}
