package com.qeeka.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.qeeka.deserializer.QueryGroupJsonDeserializer;
import com.qeeka.operate.QueryLinkOperate;
import com.qeeka.operate.QueryOperate;
import com.qeeka.operate.Sort;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by neal.xu on 7/31 0031.
 */
@JsonDeserialize(using = QueryGroupJsonDeserializer.class)
public class QueryGroup {

    /**
     * query handle node list
     */
    private List<QueryHandle> queryHandleList = new LinkedList<>();
    /**
     * query sort column
     */
    private Sort sort;

    public QueryGroup() {
    }

    /**
     * add query node
     *
     * @param node
     */
    public QueryGroup(QueryNode node) {
        if (isNotNullValue(node)) {
            queryHandleList.add(node);
        }
    }

    /**
     * add query group
     *
     * @param group
     */
    public QueryGroup(QueryGroup group) {
        for (QueryHandle handle : group.getQueryHandleList()) {
            queryHandleList.add(handle);
        }
    }

    /**
     * Sort
     *
     * @param sort
     * @return
     */
    public QueryGroup sort(Sort sort) {
        this.sort = sort;
        return this;
    }

    /**
     * add node by parameters
     *
     * @param columnName column name
     * @param value      value
     */
    public QueryGroup(String columnName, Object value) {
        if (isNotNullValue(value)) {
            queryHandleList.add(new QueryNode(columnName, value));
        }
    }

    /**
     * add node with parameters and query operate
     *
     * @param columnName   column name
     * @param value        value
     * @param queryOperate query operate
     */
    public QueryGroup(String columnName, Object value, QueryOperate queryOperate) {
        if (isNotNullValue(value)) {
            queryHandleList.add(new QueryNode(columnName, value, queryOperate));
        }
    }

    /**
     * Null Logic
     *
     * @param columnName if queryOperate equals sub_query , this columnName is sub query sub
     */
    public QueryGroup(String columnName, QueryOperate queryOperate) {
        if (!QueryOperate.IS_NULL.equals(queryOperate) && !QueryOperate.IS_NOT_NULL.equals(queryOperate) && !QueryOperate.SUB_QUERY.equals(queryOperate)) {
            throw new IllegalArgumentException("Constructor only support null reject logic!");
        }
        if (QueryOperate.SUB_QUERY.equals(queryOperate)) {
            queryHandleList.add(new QueryNode("", columnName, queryOperate));
        } else {
            queryHandleList.add(new QueryNode(columnName, null, queryOperate));
        }
    }

    /**
     * and with query node
     *
     * @param node
     * @return
     */
    public QueryGroup and(QueryNode node) {
        if (isNotNullValue(node)) {
            queryHandleList.add(node);
            if (queryHandleList.size() > 1)
                queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
        }
        return this;
    }

    /**
     * and with parameters and default operate `and`
     *
     * @param columnName column name
     * @param value      value
     * @return
     */
    public QueryGroup and(String columnName, Object value) {
        if (isNotNullValue(value)) {
            queryHandleList.add(new QueryNode(columnName, value));
            if (queryHandleList.size() > 1)
                queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
        }
        return this;
    }

    /**
     * and with parameters and operate
     *
     * @param columnName   column name
     * @param value        value
     * @param queryOperate query operate
     * @return
     */
    public QueryGroup and(String columnName, Object value, QueryOperate queryOperate) {
        if (isNotNullValue(value)) {
            queryHandleList.add(new QueryNode(columnName, value, queryOperate));
            if (queryHandleList.size() > 1)
                queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
        }
        return this;
    }

    /**
     * and with query group
     *
     * @param group query group
     * @return
     */
    public QueryGroup and(QueryGroup group) {
        if (group == null) {
            throw new IllegalArgumentException("query can't null!");
        }
        for (QueryHandle handle : group.getQueryHandleList()) {
            queryHandleList.add(handle);
        }
        if (!group.getQueryHandleList().isEmpty()) {
            queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
        }
        return this;
    }

    /**
     * null query node
     * @param columnName if queryOperate equals sub_query , this columnName is sub query sub
     */
    public QueryGroup and(String columnName, QueryOperate queryOperate) {
        if (!QueryOperate.IS_NULL.equals(queryOperate) && !QueryOperate.IS_NOT_NULL.equals(queryOperate) && !QueryOperate.SUB_QUERY.equals(queryOperate)) {
            throw new IllegalArgumentException("Constructor only support null or sub query operate logic!");
        }
        if (QueryOperate.SUB_QUERY.equals(queryOperate)) {
            queryHandleList.add(new QueryNode("", columnName, queryOperate));
        } else {
            queryHandleList.add(new QueryNode(columnName, null, queryOperate));
        }
        if (queryHandleList.size() > 1)
            queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
        return this;
    }

    /**
     * or with query node
     *
     * @param node
     * @return
     */
    public QueryGroup or(QueryNode node) {
        if (isNotNullValue(node)) {
            queryHandleList.add(node);
            if (queryHandleList.size() > 1)
                queryHandleList.add(new QueryOperateNode(QueryLinkOperate.OR));
        }
        return this;
    }


    /**
     * or with parameters and default operate `or`
     *
     * @param columnName column name
     * @param value      value
     * @return
     */
    public QueryGroup or(String columnName, Object value) {
        if (isNotNullValue(value)) {
            queryHandleList.add(new QueryNode(columnName, value));
            if (queryHandleList.size() > 1)
                queryHandleList.add(new QueryOperateNode(QueryLinkOperate.OR));
        }
        return this;
    }

    /**
     * or with parameters and operate
     *
     * @param columnName   column name
     * @param value        value
     * @param queryOperate query operate
     * @return
     */
    public QueryGroup or(String columnName, Object value, QueryOperate queryOperate) {
        if (isNotNullValue(value)) {
            queryHandleList.add(new QueryNode(columnName, value, queryOperate));
            if (queryHandleList.size() > 1)
                queryHandleList.add(new QueryOperateNode(QueryLinkOperate.OR));
        }
        return this;
    }

    /**
     * null query node
     * @param columnName if queryOperate equals sub_query , this columnName is sub query sub
     * @param queryOperate
     * @return
     */
    public QueryGroup or(String columnName, QueryOperate queryOperate) {
        if (!QueryOperate.IS_NULL.equals(queryOperate) && !QueryOperate.IS_NOT_NULL.equals(queryOperate) && !QueryOperate.SUB_QUERY.equals(queryOperate)) {
            throw new IllegalArgumentException("Constructor only support null or sub query operate logic!");
        }
        if (QueryOperate.SUB_QUERY.equals(queryOperate)) {
            queryHandleList.add(new QueryNode("", columnName, queryOperate));
        } else {
            queryHandleList.add(new QueryNode(columnName, null, queryOperate));
        }
        if (queryHandleList.size() > 1)
            queryHandleList.add(new QueryOperateNode(QueryLinkOperate.OR));
        return this;
    }

    /**
     * or with query group
     *
     * @param group query group
     * @return
     */
    public QueryGroup or(QueryGroup group) {
        if (group == null) {
            throw new IllegalArgumentException("query can't null!");
        }
        for (QueryHandle handle : group.getQueryHandleList()) {
            queryHandleList.add(handle);
        }
        if (!group.getQueryHandleList().isEmpty()) {
            queryHandleList.add(new QueryOperateNode(QueryLinkOperate.OR));
        }
        return this;
    }


    private boolean isNotNullValue(QueryNode queryNode) {
        if (queryNode == null) {
            throw new IllegalArgumentException("query node can't null!");
        }
        return queryNode.getValue() != null;
    }

    private boolean isNotNullValue(Object value) {
        return value != null;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }

    public List<QueryHandle> getQueryHandleList() {
        return queryHandleList;
    }

    public void setQueryHandleList(List<QueryHandle> queryHandleList) {
        this.queryHandleList = queryHandleList;
    }
}
