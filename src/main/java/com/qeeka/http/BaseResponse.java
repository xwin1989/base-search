package com.qeeka.http;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.qeeka.domain.QueryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.HttpURLConnection;
import java.util.Collection;

/**
 * Created by Neal on 2018/10/12.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
    @JsonProperty("record")
    private T entity;
    @JsonProperty("records")
    private Collection<T> records;
    @JsonProperty("total_records")
    private Long totalRecords;
    @JsonProperty("page_index")
    private Integer pageIndex;
    @JsonProperty("page_size")
    private Integer pageSize;
    @JsonProperty("status_code")
    private Integer statusCode = HttpURLConnection.HTTP_OK;
    @JsonProperty("message")
    private String message;
    @JsonProperty("trace")
    private String trace;

    public BaseResponse<T> body(T record) {
        this.entity = record;
        return this;
    }

    public BaseResponse<T> body(Collection<T> records) {
        this.records = records;
        return this;
    }

    public ResponseEntity<T> ok() {
        return new ResponseEntity(this, HttpStatus.OK);
    }

    public ResponseEntity<T> ok(String message) {
        this.message = message;
        return new ResponseEntity(this, HttpStatus.OK);
    }

    public ResponseEntity<T> failed(String message) {
        return failed(HttpStatus.BAD_REQUEST, message, null);
    }

    public ResponseEntity<T> failed(HttpStatus httpStatus, String message) {
        return failed(httpStatus, message, null);
    }

    public ResponseEntity<T> failed(HttpStatus httpStatus, String message, String trace) {
        this.statusCode = httpStatus.value();
        this.message = message;
        this.trace = trace;
        return new ResponseEntity(this, httpStatus);
    }

    public BaseResponse<T> fromRequest(BaseRequest request) {
        if (request != null) {
            this.pageIndex = request.getPageIndex();
            this.pageSize = request.getPageSize();
        }
        return this;
    }

    public BaseResponse<T> setResponse(Integer index, Integer size, Long total) {
        this.pageIndex = index;
        this.pageSize = size;
        this.totalRecords = total;
        return this;
    }

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Collection<T> getRecords() {
        return records;
    }

    public void setRecords(Collection<T> records) {
        this.records = records;
    }

    public BaseResponse<T> total(Long total) {
        this.totalRecords = total;
        return this;
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
}
