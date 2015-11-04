package com.qeeka.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.qeeka.deserializer.QueryGroupJsonDeserializer;
import com.qeeka.operate.QueryLinkOperate;
import com.qeeka.operate.QueryOperate;
import com.qeeka.operate.Sort;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by neal.xu on 7/31 0031.
 */
@JsonDeserialize(using = QueryGroupJsonDeserializer.class)
public class QueryGroup {

    /**
     * query handle node list
     */
    private final List<QueryHandle> queryHandleList = new LinkedList<>();
    /**
     * query sort column
     */
    private Sort sort;

    /**
     * join other entity
     */
    private final Map<String, Map<QueryLinkOperate, String>> entityMapping = new LinkedHashMap<>();

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
        boolean beginHandel = !queryHandleList.isEmpty();
        for (QueryHandle handle : group.getQueryHandleList()) {
            queryHandleList.add(handle);
        }
        if (beginHandel && !group.getQueryHandleList().isEmpty()) {
            queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
        }
        return this;
    }

    /**
     * null query node
     *
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
     *
     * @param columnName   if queryOperate equals sub_query , this columnName is sub query sub
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

    //------------------ Join ------------------

    /**
     * inner join fetch other entity with alias ,  Default return entity (E) and Can't Modify ,
     * Because BaseSearchRepository need return current <T> class.
     *
     * @param entityName
     * @param entityAlias
     * @return
     */
    public QueryGroup joinFetch(String entityName, String entityAlias) {
        joinEntityCheck(entityName, entityAlias, QueryLinkOperate.CROSS_JOIN, null);
        this.entityMapping.put(entityName, Collections.singletonMap(QueryLinkOperate.INNER_JOIN_FETCH, entityAlias));
        return this;
    }

    /**
     * inner join other entity with alias ,  Default return entity (E) and Can't Modify ,
     * Because BaseSearchRepository need return current <T> class.
     *
     * @param entityName
     * @param entityAlias
     * @return
     */
    public QueryGroup join(String entityName, String entityAlias) {
        joinEntityCheck(entityName, entityAlias, QueryLinkOperate.CROSS_JOIN, null);
        this.entityMapping.put(entityName, Collections.singletonMap(QueryLinkOperate.INNER_JOIN, entityAlias));
        return this;
    }

    /**
     * cross join other entity with alias ,  Default return entity (E) and Can't Modify , Only use to no relation Entity
     * Because BaseSearchRepository need return current <T> class.
     *
     * @param entityName
     * @param entityAlias
     * @return
     */
    public QueryGroup crossJoin(String entityName, String entityAlias) {
        joinEntityCheck(entityName, entityAlias, null, QueryLinkOperate.CROSS_JOIN);
        this.entityMapping.put(entityName, Collections.singletonMap(QueryLinkOperate.CROSS_JOIN, entityAlias));
        return this;
    }

    /**
     * left join other entity with alias ,  Default return entity (E) and Can't Modify ,
     * Because BaseSearchRepository need return current <T> class.
     *
     * @param entityName
     * @param entityAlias
     * @return
     */
    public QueryGroup leftJoin(String entityName, String entityAlias) {
        joinEntityCheck(entityName, entityAlias, QueryLinkOperate.CROSS_JOIN, null);
        this.entityMapping.put(entityName, Collections.singletonMap(QueryLinkOperate.LEFT_JOIN, entityAlias));
        return this;
    }

    /**
     * left join fetch other entity with alias ,  Default return entity (E) and Can't Modify ,
     * Because BaseSearchRepository need return current <T> class.
     *
     * @param entityName
     * @param entityAlias
     * @return
     */
    public QueryGroup leftJoinFetch(String entityName, String entityAlias) {
        joinEntityCheck(entityName, entityAlias, QueryLinkOperate.CROSS_JOIN, null);
        this.entityMapping.put(entityName, Collections.singletonMap(QueryLinkOperate.LEFT_JOIN_FETCH, entityAlias));
        return this;
    }

    private void joinEntityCheck(String entityName, String entityAlias, QueryLinkOperate excludeOperate, QueryLinkOperate aloneOperate) {
        if ("E".equalsIgnoreCase(entityAlias)) {
            throw new IllegalArgumentException("Can't use `E`  assign to entity!");
        }
        Map<QueryLinkOperate, String> operateStringMap = this.entityMapping.get(entityName);
        if (operateStringMap != null) {
            throw new IllegalArgumentException("Entity already exist! Please fix #" + entityName);
        }
        if (excludeOperate != null) {
            for (Map<QueryLinkOperate, String> queryLinkOperateStringMap : entityMapping.values()) {
                for (QueryLinkOperate queryLinkOperate : queryLinkOperateStringMap.keySet()) {
                    if (queryLinkOperate.equals(excludeOperate)) {
                        throw new IllegalArgumentException("join operate can't support other query link operate! #" + excludeOperate.getValue());
                    }
                }
            }
        }
        if (aloneOperate != null) {
            for (Map<QueryLinkOperate, String> queryLinkOperateStringMap : entityMapping.values()) {
                for (QueryLinkOperate queryLinkOperate : queryLinkOperateStringMap.keySet()) {
                    if (!queryLinkOperate.equals(aloneOperate)) {
                        throw new IllegalArgumentException("join operate only support query link operate! #" + aloneOperate.getValue());
                    }
                }
            }
        }
    }

    /**
     * on query
     * like: E.id = O.id
     *
     * @param masterColumn
     * @param otherColumn
     * @return
     */
    public QueryGroup on(String masterColumn, String otherColumn) {
        if (entityMapping.isEmpty()) {
            throw new IllegalArgumentException("on must after join operator!");
        }
        for (Map<QueryLinkOperate, String> queryLinkOperateStringMap : entityMapping.values()) {
            if (!queryLinkOperateStringMap.keySet().contains(QueryLinkOperate.CROSS_JOIN)) {
                throw new IllegalArgumentException("on operator only support cross join!");
            }
        }
        queryHandleList.add(new QueryNode(masterColumn, otherColumn, QueryOperate.COLUMN_EQUALS));
        if (queryHandleList.size() > 1)
            queryHandleList.add(new QueryOperateNode(QueryLinkOperate.AND));
        return this;
    }

    public Map<String, Map<QueryLinkOperate, String>> getEntityMapping() {
        return entityMapping;
    }

    public List<QueryHandle> getQueryHandleList() {
        return queryHandleList;
    }
}
