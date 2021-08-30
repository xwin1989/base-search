package com.qeeka.domain;

import com.qeeka.enums.Direction;
import com.qeeka.enums.QueryLinkOperate;
import com.qeeka.enums.QueryOperate;
import com.qeeka.enums.QueryResultType;
import com.qeeka.http.BaseSearchRequest;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by neal.xu on 2018/12/11.
 * Search condition inlet
 */
public class QueryGroup {

    /**
     * result type
     */
    private QueryResultType queryResultType = QueryResultType.LIST;
    /**
     * query handle node list
     */
    private final List<QueryHandle> queryHandleList = new LinkedList<>();
    /**
     * join other entity
     */
    private final List<MappingNode> entityMapping = new ArrayList<>();

    /**
     * query sort column
     */
    private Sort sort;
    /**
     * page index and set default
     */
    private Integer pageIndex = 0;

    /**
     * page size and set default
     */
    private Integer pageSize = 0;

    /**
     * need total record
     */
    private boolean needCount = false;
    /**
     * need record
     */
    private boolean needRecord = true;

    /**
     * record need distinct
     */
    private boolean needDistinct = false;
    /**
     * strict
     */
    private boolean strict = true;
    /**
     * select column
     */
    private CharSequence[] selects;
//--------------------------- constructor operator -------------------------

    public QueryGroup() {
    }

    public QueryGroup(Integer pageIndex, Integer pageSize) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }

    /**
     * set index & size from search request
     */
    public QueryGroup setSearchRequest(BaseSearchRequest searchRequest) {
        this.pageIndex = searchRequest.getPageIndex();
        this.pageSize = searchRequest.getPageSize();
        return this;
    }

    /**
     * add node by parameters
     */
    public QueryGroup(String columnName, Object value) {
        this(columnName, value, QueryOperate.EQUALS);
    }

    /**
     * Null and subQuery Logic
     */
    public QueryGroup(String columnName, QueryOperate queryOperate) {
        this(columnName, null, queryOperate);
    }

    /**
     * add node with parameters and query operate
     */
    public QueryGroup(String columnName, Object value, QueryOperate queryOperate) {
        addNode(columnName, value, queryOperate, QueryLinkOperate.AND);
    }

    /**
     * add node with parameters, query operate, strict mode
     */
    public QueryGroup(String columnName, Object value, QueryOperate queryOperate, boolean strict) {
        this.strict = strict;
        addNode(columnName, value, queryOperate, QueryLinkOperate.AND);
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
//--------------------------- and operator -------------------------

    /**
     * and operator , default operate `and`
     */
    public QueryGroup and(String columnName, Object value) {
        return addNode(columnName, value, QueryOperate.EQUALS, QueryLinkOperate.AND);
    }

    /**
     * and operator , Specifies operate
     */
    public QueryGroup and(String columnName, Object value, QueryOperate queryOperate) {
        return addNode(columnName, value, queryOperate, QueryLinkOperate.AND);
    }

    /**
     * null query node
     *
     * @param columnName if queryOperate equals sub_query , this columnName is sub query sub
     */
    public QueryGroup and(String columnName, QueryOperate queryOperate) {
        return addNode(columnName, null, queryOperate, QueryLinkOperate.AND);
    }

    /**
     * sub group
     */
    public QueryGroup and(QueryGroup group) {
        return addQueryGroup(group, QueryLinkOperate.AND);
    }

//--------------------------- or operator -------------------------

    /**
     * and operator , default operate `and`
     */
    public QueryGroup or(String columnName, Object value) {
        return addNode(columnName, value, QueryOperate.EQUALS, QueryLinkOperate.OR);
    }

    /**
     * and operator , Specifies operate
     */
    public QueryGroup or(String columnName, Object value, QueryOperate queryOperate) {
        return addNode(columnName, value, queryOperate, QueryLinkOperate.OR);
    }

    /**
     * null query node
     *
     * @param columnName if queryOperate equals sub_query , this columnName is sub query sub
     */
    public QueryGroup or(String columnName, QueryOperate queryOperate) {
        return addNode(columnName, null, queryOperate, QueryLinkOperate.OR);
    }

    /**
     * sub group
     */
    public QueryGroup or(QueryGroup group) {
        return addQueryGroup(group, QueryLinkOperate.OR);
    }

//--------------------------- node operator -------------------------

    /**
     * add node to query handle list
     */
    private QueryGroup addNode(String columnName, Object value, QueryOperate queryOperate, QueryLinkOperate queryLinkOperate) {
        if (value == null && !(QueryOperate.IS_NULL.equals(queryOperate) || QueryOperate.IS_NOT_NULL.equals(queryOperate) || QueryOperate.SUB_QUERY.equals(queryOperate))) {
            if (this.strict) throw new IllegalArgumentException("column #" + columnName + " value can't null");
            return this;
        }
        queryHandleList.add(new QueryNode(columnName, value, queryOperate));
        if (queryHandleList.size() > 1)
            queryHandleList.add(new QueryOperateNode(queryLinkOperate));
        return this;
    }

    /**
     * add query group to master node's query handle list
     */
    private QueryGroup addQueryGroup(QueryGroup group, QueryLinkOperate linkOperate) {
        if (group == null) {
            throw new IllegalArgumentException("query can't null!");
        }
        boolean beginHandel = !queryHandleList.isEmpty();
        for (QueryHandle handle : group.getQueryHandleList()) {
            queryHandleList.add(handle);
        }
        if (beginHandel && !group.getQueryHandleList().isEmpty()) {
            queryHandleList.add(new QueryOperateNode(linkOperate));
        }
        return this;
    }
//--------------------------- join operator -------------------------


    /**
     * inner join other entity with alias ,  Default return entity (E) and Can't Modify
     */
    public QueryGroup join(String entityName, String entityAlias) {
        this.entityMapping.add(new MappingNode(entityName, entityAlias, QueryLinkOperate.INNER_JOIN));
        return this;
    }

    /**
     * left join other entity with alias ,  Default return entity (E) and Can't Modify
     */
    public QueryGroup leftJoin(String entityName, String entityAlias) {
        this.entityMapping.add(new MappingNode(entityName, entityAlias, QueryLinkOperate.LEFT_JOIN));
        return this;
    }

    /**
     * left out join other entity with alias ,  Default return entity (E) and Can't Modify
     */
    public QueryGroup leftOutJoin(String entityName, String entityAlias) {
        this.entityMapping.add(new MappingNode(entityName, entityAlias, QueryLinkOperate.LEFT_OUT_JOIN));
        return this;
    }

    /**
     * right join other entity with alias ,  Default return entity (E) and Can't Modify
     */
    public QueryGroup rightJoin(String entityName, String entityAlias) {
        this.entityMapping.add(new MappingNode(entityName, entityAlias, QueryLinkOperate.RIGHT_JOIN));
        return this;
    }

    /**
     * right out join other entity with alias ,  Default return entity (E) and Can't Modify
     */
    public QueryGroup rightOutJoin(String entityName, String entityAlias) {
        this.entityMapping.add(new MappingNode(entityName, entityAlias, QueryLinkOperate.RIGHT_OUT_JOIN));
        return this;
    }

    /**
     * cross join other entity with alias ,  Default return entity (E) and Can't Modify
     */
    public QueryGroup crossJoin(String entityName, String entityAlias) {
        this.entityMapping.add(new MappingNode(entityName, entityAlias, QueryLinkOperate.CROSS_JOIN));
        return this;
    }


    /**
     * on operator
     */
    public QueryGroup on(String column, Object value) {
        return on(column, value, QueryOperate.COLUMN_EQUALS);
    }

    /**
     * on operator ,Specifies operator
     */
    public QueryGroup on(String column, Object value, QueryOperate queryOperate) {
        if (entityMapping.isEmpty()) {
            throw new IllegalArgumentException("on must after join operator!");
        }
        MappingNode lastNode = entityMapping.get(entityMapping.size() - 1);
        lastNode.getLinkMapping().add(new QueryNode(column, value, queryOperate));
        return this;
    }
//--------------------------- sort operator -------------------------

    /**
     * Sort by orders
     */
    public QueryGroup sort(List<Sort.Order> orders) {
        this.sort = new Sort(orders);
        return this;
    }

    /**
     * Sort by direction & column name
     */
    public QueryGroup sort(Direction direction, String... columnNames) {
        this.sort = new Sort(direction, columnNames);
        return this;
    }

    /**
     * Sort by column names
     */
    public QueryGroup sort(Direction direction, List<String> columnNames) {
        this.sort = new Sort(direction, columnNames);
        return this;
    }

//--------------------------- get set operator -------------------------

    public QueryGroup needCount() {
        this.needCount = true;
        return this;
    }

    public QueryGroup needDistinct() {
        this.needDistinct = true;
        return this;
    }

    public QueryGroup onlyRecord() {
        this.needCount = false;
        this.needRecord = true;
        return this;
    }

    public QueryGroup onlyCount() {
        this.needCount = true;
        this.needRecord = false;
        this.needDistinct = false;
        return this;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public QueryGroup setPageable(Integer pageIndex, Integer pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public QueryGroup index(Integer pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public QueryGroup size(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }


    public List<QueryHandle> getQueryHandleList() {
        return queryHandleList;
    }

    public Sort getSort() {
        return sort;
    }

    public List<MappingNode> getEntityMapping() {
        return entityMapping;
    }

    public QueryResultType getQueryResultType() {
        return queryResultType;
    }

    public void setQueryResultType(QueryResultType queryResultType) {
        this.queryResultType = queryResultType;
    }

    public boolean isNeedCount() {
        return needCount;
    }

    public boolean isNeedRecord() {
        return needRecord;
    }

    public boolean isNeedDistinct() {
        return needDistinct;
    }

    public QueryGroup selects(CharSequence... selectFields) {
        this.selects = selectFields;
        return this;
    }

    public CharSequence[] getSelects() {
        return selects;
    }

    public CharSequence getSelectsChar() {
        if (this.selects == null) return null;
        StringBuilder sb = new StringBuilder();
        for (CharSequence field : this.selects) {
            sb.append(field).append(',');
        }
        sb.setLength(sb.length() - 1);
        return sb;
    }

    public static QueryGroup looseGroup() {
        return looseGroup(null, null, null);
    }

    public static QueryGroup looseGroup(String columnName, Object value) {
        return looseGroup(columnName, value, QueryOperate.EQUALS);
    }

    public static QueryGroup looseGroup(String columnName, QueryOperate queryOperate) {
        return looseGroup(columnName, null, queryOperate);
    }

    public static QueryGroup looseGroup(String columnName, Object value, QueryOperate queryOperate) {
        return new QueryGroup(columnName, value, queryOperate, false);
    }

}
