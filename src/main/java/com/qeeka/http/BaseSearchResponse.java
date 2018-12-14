package com.qeeka.http;

import com.qeeka.domain.QueryResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.HttpURLConnection;
import java.util.List;

/**
 * Created by Neal on 10/12 0012.
 */
@XmlRootElement(name = "base_search_response")
@XmlAccessorType(XmlAccessType.FIELD)
public class BaseSearchResponse<T> {
    @XmlElement(name = "record")
    protected T entity;
    @XmlElementWrapper(name = "records")
    @XmlElement
    protected List<T> recordList;
    @XmlElement(name = "total_records")
    protected Long totalRecords;
    @XmlElement(name = "page_index")
    protected Integer pageIndex;
    @XmlElement(name = "page_size")
    protected Integer pageSize;
    @XmlElement(name = "status_code")
    protected Integer statusCode = HttpURLConnection.HTTP_OK;
    @XmlElement(name = "message")
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
