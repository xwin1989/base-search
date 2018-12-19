package com.qeeka.http;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qeeka.domain.QueryResponse;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by Neal on 10/12 0012.
 */
public class BaseSearchResponse<T> {
    @JsonProperty("record")
    protected T entity;
    @JsonProperty("records")
    protected List<T> recordList;
    @JsonProperty("total_records")
    protected Long totalRecords;
    @JsonProperty("page_index")
    protected Integer pageIndex;
    @JsonProperty("page_size")
    protected Integer pageSize;
    @JsonProperty("status_code")
    protected Integer statusCode = HttpURLConnection.HTTP_OK;
    @JsonProperty("message")
    protected String message;

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public List<T> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<T> recordList) {
        this.recordList = recordList;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
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

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void acquireFromRequest(BaseSearchRequest request) {
        if (request != null) {
            this.setPageIndex(request.getPageIndex());
            this.setPageSize(request.getPageSize());
        }
    }

    public void acquireFromQueryResponse(QueryResponse request) {
        if (request != null) {
            this.setTotalRecords(request.getTotalRecords());
            this.setPageIndex(request.getPageIndex());
            this.setPageSize(request.getPageSize());
        }
    }
}
