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
        queryHandleList.add(node);
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
        queryHandleList.add(new QueryNode(columnName, value));
    }

    /**
     * add node with parameters and query operate
     *
     * @param columnName   column name
     * @param value        value
     * @param queryOperate query operate
     */
    public QueryGroup(String columnName, Object value, QueryOperate queryOperate) {
        queryHandleList.add(new QueryNode(columnName, value, queryOperate));
    }

    /**
     * Null Logic
     *
     * @param columnName
     */
    public QueryGroup(String columnName, QueryOperate queryOperate) {
        if (!queryOperate.equals(QueryOperate.IS_NULL) && !queryOperate.equals(QueryOperate.IS_NOT_NULL)) {
            throw new IllegalArgumentException("Constructor only support null reject logic!");
        }
        queryHandleList.add(new QueryNode(columnName, null, queryOperate));
    }

    /**
     * and with query node
     *
     * @param node
     * @return
     */
    public QueryGroup and(QueryNode node) {
        queryHandleList.add(node);
        queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
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
        queryHandleList.add(new QueryNode(columnName, value));
        queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
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
        queryHandleList.add(new QueryNode(columnName, value, queryOperate));
        queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
        return this;
    }

    /**
     * and with query group
     *
     * @param group query group
     * @return
     */
    public QueryGroup and(QueryGroup group) {
        for (QueryHandle handle : group.getQueryHandleList()) {
            queryHandleList.add(handle);
        }
        queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
        return this;
    }

    /**
     * null query node
     *
     * @param columnName
     */
    public QueryGroup and(String columnName, QueryOperate queryOperate) {
        if (!queryOperate.equals(QueryOperate.IS_NULL) && !queryOperate.equals(QueryOperate.IS_NOT_NULL)) {
            throw new IllegalArgumentException("Constructor only support null reject logic!");
        }
        queryHandleList.add(new QueryNode(columnName, null, queryOperate));
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
        queryHandleList.add(node);
        queryHandleList.add(new QueryOperateNode(QueryLinkOperate.OR));
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
        queryHandleList.add(new QueryNode(columnName, value));
        queryHandleList.add(new QueryOperateNode(QueryLinkOperate.OR));
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
        queryHandleList.add(new QueryNode(columnName, value, queryOperate));
        queryHandleList.add(new QueryOperateNode(QueryLinkOperate.OR));
        return this;
    }

    /**
     * null query node
     *
     * @param columnName
     */
    public QueryGroup or(String columnName, QueryOperate queryOperate) {
        if (!queryOperate.equals(QueryOperate.IS_NULL) && !queryOperate.equals(QueryOperate.IS_NOT_NULL)) {
            throw new IllegalArgumentException("Constructor only support null reject logic!");
        }
        queryHandleList.add(new QueryNode(columnName, null, queryOperate));
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
        for (QueryHandle handle : group.getQueryHandleList()) {
            queryHandleList.add(handle);
        }
        queryHandleList.add(new QueryOperateNode(QueryLinkOperate.OR));
        return this;
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
