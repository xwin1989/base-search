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
    private boolean needCount = true;

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

    public void setQueryGroup(QueryGroup queryGroup) {
        this.queryGroup = queryGroup;
    }

    public Integer getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(Integer pageIndex) {
        this.pageIndex = pageIndex;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public boolean isNeedCount() {
        return needCount;
    }

    public void setNeedCount(boolean needCount) {
        this.needCount = needCount;
    }

    public boolean isNeedRecord() {
        return needRecord;
    }

    public void setNeedRecord(boolean needRecord) {
        this.needRecord = needRecord;
    }

    public QueryResultType getQueryResultType() {
        return queryResultType;
    }

    public void setQueryResultType(QueryResultType queryResultType) {
        this.queryResultType = queryResultType;
    }
}
