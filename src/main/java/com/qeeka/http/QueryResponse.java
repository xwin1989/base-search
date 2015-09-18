package com.qeeka.http;

import java.util.List;

/**
 * Created by Neal on 8/9 0009.
 */
public class QueryResponse<T> {
    private T entity;
    private List<T> records;
    private Long totalRecords;

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }
}
