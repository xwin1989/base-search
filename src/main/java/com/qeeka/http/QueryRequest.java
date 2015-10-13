package com.qeeka.http;

import com.qeeka.domain.QueryGroup;
import com.qeeka.operate.QueryResultType;

/**
 * Created by Neal on 8/9 0009.
 */
public class QueryRequest {
    //query parameters main
    private QueryGroup queryGroup;

    //result type
    private QueryResultType queryResultType = QueryResultType.LIST;

    //page index and set default
    private Integer pageIndex = 0;

    //page size and set default
    private Integer pageSize = 10;

    //need total record
    private boolean needCount = false;

    //only need count
    private boolean needRecord = true;

    public QueryRequest() {
    }

    public QueryRequest(QueryGroup queryGroup) {
        this.queryGroup = queryGroup;
    }

    public QueryRequest(Integer pageIndex, Integer pageSize) {
        this.pageSize = pageSize;
        this.pageIndex = pageIndex;
    }

    public QueryRequest(QueryGroup queryGroup, Integer pageIndex, Integer pageSize) {
        this.queryGroup = queryGroup;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
    }

    public QueryRequest(QueryGroup queryGroup, Integer pageIndex, Integer pageSize, QueryResultType queryResultType) {
        this.queryGroup = queryGroup;
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.queryResultType = queryResultType;
    }

    public QueryGroup getQueryGroup() {
        return queryGroup;
    }

    public QueryRequest setQueryGroup(QueryGroup queryGroup) {
        this.queryGroup = queryGroup;
        return this;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public QueryRequest setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
        return this;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public QueryRequest setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public boolean isNeedCount() {
        return needCount;
    }

    public QueryRequest setNeedCount(boolean needCount) {
        this.needCount = needCount;
        return this;
    }

    public QueryRequest needCount() {
        this.needCount = true;
        return this;
    }

    public boolean isNeedRecord() {
        return needRecord;
    }

    public QueryRequest setNeedRecord(boolean needRecord) {
        this.needRecord = needRecord;
        return this;
    }

    public QueryResultType getQueryResultType() {
        return queryResultType;
    }

    public QueryRequest setQueryResultType(QueryResultType queryResultType) {
        this.queryResultType = queryResultType;
        return this;
    }
}
